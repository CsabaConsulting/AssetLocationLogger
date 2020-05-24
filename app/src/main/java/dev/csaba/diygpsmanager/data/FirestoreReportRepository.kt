package dev.csaba.diygpsmanager.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dev.csaba.diygpsmanager.data.remote.RemoteReport
import dev.csaba.diygpsmanager.data.remote.mapDateToTimestamp
import dev.csaba.diygpsmanager.data.remote.mapToReport
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.util.Date


class FirestoreReportRepository(secondaryDB: FirebaseFirestore, assetId: String, lookBackDate: Date) : IReportRepository {

    companion object {
        private const val ASSET_COLLECTION = "Assets"
        private const val REPORT_COLLECTION = "Reports"
    }

    private val remoteDB: FirebaseFirestore = secondaryDB
    private var changeObservable: Observable<List<DocumentSnapshot>>
    private val _assetId: String = assetId
    private var _lookBackDate: Date = lookBackDate

    init {
        changeObservable = BehaviorSubject.create { emitter: ObservableEmitter<List<DocumentSnapshot>> ->
            val listeningRegistration = remoteDB.collection(ASSET_COLLECTION)
                .document(_assetId).collection(REPORT_COLLECTION)
                .whereGreaterThan("created", mapDateToTimestamp(_lookBackDate))
                .addSnapshotListener { value, error ->
                    if (value == null || error != null) {
                        return@addSnapshotListener
                    }

                    if (!emitter.isDisposed) {
                        emitter.onNext(value.documents)
                    }

                    value.documentChanges.forEach {
                        Timber.d("Data changed type ${it.type} document ${it.document.id}")
                    }
                }

            emitter.setCancellable { listeningRegistration.remove() }
        }
    }

    private fun mapDocumentToRemoteReport(document: DocumentSnapshot) = document.toObject(RemoteReport::class.java)!!.apply { id = document.id }

    override fun getAllReports(): Single<List<Report>> {
        return Single.create<List<DocumentSnapshot>> { emitter ->
            remoteDB.collection(ASSET_COLLECTION)
                .document(_assetId).collection(REPORT_COLLECTION)
                .orderBy("created", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener {
                    if (!emitter.isDisposed) {
                        emitter.onSuccess(it.documents)
                    }
                }
                .addOnFailureListener {
                    if (!emitter.isDisposed) {
                        emitter.onError(it)
                    }
                }
        }
            .observeOn(Schedulers.io())
            .flatMapObservable { Observable.fromIterable(it) }
            .map(::mapDocumentToRemoteReport)
            .map(::mapToReport)
            .toList()
    }

    override fun getChangeObservable(): Observable<List<Report>> =
        changeObservable.hide()
            .observeOn(Schedulers.io())
            .map { list -> list.map(::mapDocumentToRemoteReport).map(::mapToReport) }

}
