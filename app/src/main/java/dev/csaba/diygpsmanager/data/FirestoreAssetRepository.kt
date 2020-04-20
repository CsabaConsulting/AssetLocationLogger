package dev.csaba.diygpsmanager.data

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dev.csaba.diygpsmanager.data.remote.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject


class FirestoreAssetRepository(secondaryDB: FirebaseFirestore) : IAssetRepository {

    companion object {
        private const val TAG = "FirestoreAssetRepo"
        private const val ASSET_COLLECTION = "Assets"
        private const val REPORT_COLLECTION = "Reports"
    }

    private var remoteDB: FirebaseFirestore
    private var changeObservable: Observable<List<DocumentSnapshot>>

    init {
        remoteDB = secondaryDB

        changeObservable = BehaviorSubject.create { emitter: ObservableEmitter<List<DocumentSnapshot>> ->
            val listeningRegistration = remoteDB.collection(ASSET_COLLECTION)
                .addSnapshotListener { value, error ->
                    if (value == null || error != null) {
                        return@addSnapshotListener
                    }

                    if (!emitter.isDisposed) {
                        emitter.onNext(value.documents)
                    }

                    value.documentChanges.forEach {
                        Log.d("FirestoreAssetRepo", "Data changed type ${it.type} document ${it.document.id}")
                    }
                }

            emitter.setCancellable { listeningRegistration.remove() }
        }
    }

    override fun getAllAssets(): Single<List<Asset>> {
        return Single.create<List<DocumentSnapshot>> { emitter ->
            remoteDB.collection(ASSET_COLLECTION)
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
            .map(::mapDocumentToRemoteAsset)
            .map(::mapToAsset)
            .toList()
    }

    private fun mapDocumentToRemoteAsset(document: DocumentSnapshot) = document.toObject(RemoteAsset::class.java)!!.apply { id = document.id }

    override fun addAsset(asset: Asset): Completable {
        return Completable.create { emitter ->
            remoteDB.collection(ASSET_COLLECTION)
                .add(mapToAssetData(asset))
                .addOnSuccessListener {
                    it.collection(REPORT_COLLECTION)
                    if (!emitter.isDisposed) {
                        emitter.onComplete()
                    }
                }
                .addOnFailureListener {
                    if (!emitter.isDisposed) {
                        emitter.onError(it)
                    }
                }
        }
    }

    override fun deleteAsset(assetId: String): Completable {
        // TODO: delete report collection, sub collections are not deleted automatically
        // https://stackoverflow.com/questions/49125183/how-to-model-this-structure-to-handle-delete/
        return Completable.create { emitter ->
            remoteDB.collection(ASSET_COLLECTION)
                .document(assetId)
                .delete()
                .addOnSuccessListener {
                    if (!emitter.isDisposed) {
                        emitter.onComplete()
                    }
                }
                .addOnFailureListener {
                    if (!emitter.isDisposed) {
                        emitter.onError(it)
                    }
                }
        }
    }

    private fun mapDocumentToRemoteReport(document: DocumentSnapshot) = document.toObject(RemoteReport::class.java)!!.apply { id = document.id }

    override fun lockUnlockAsset(assetId: String): Completable {
        Log.d(TAG, String.format("lockUnlockAsset"))
        return Completable.create { emitter ->
            remoteDB.collection(ASSET_COLLECTION)
                .document(assetId)
                .get()
                .addOnSuccessListener {
                    Log.d(TAG, String.format("Unlocking"))
                    val remoteAsset = mapDocumentToRemoteAsset(it)
                    if (remoteAsset.lockLat > 1e-6 && remoteAsset.lockLon > 1e-6) {
                        it.reference.update(getUnlockLocation())
                        if (!emitter.isDisposed) {
                            emitter.onComplete()
                        }
                    } else {
                        val assetReference = it.reference
                        assetReference.collection(REPORT_COLLECTION).orderBy("created", Query.Direction.DESCENDING).limit(1).get()
                            .addOnSuccessListener {
                                if (!it.isEmpty) {
                                    val report = mapDocumentToRemoteReport(it.documents[0])
                                    Log.d(TAG, String.format("Locking at %f, %f", report.lat, report.lon))
                                    assetReference.update(mapToLockLocation(mapToReport(report)))
                                }
                                if (!emitter.isDisposed) {
                                    emitter.onComplete()
                                }
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "Could not find latest location for locking")
                                if (!emitter.isDisposed) {
                                    emitter.onError(it)
                                }
                            }
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "Could not find asset to unlock")
                    if (!emitter.isDisposed) {
                        emitter.onError(it)
                    }
                }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
    }

    override fun getChangeObservable(): Observable<List<Asset>> =
        changeObservable.hide()
            .observeOn(Schedulers.io())
            .map { list -> list.map(::mapDocumentToRemoteAsset).map(::mapToAsset) }

//    override fun getAllReports(asset: Asset): Single<List<Report>> {
//        return Single.create<List<DocumentSnapshot>> { emitter ->
//            asset.collection(REPORT_COLLECTION)
//    }

}
