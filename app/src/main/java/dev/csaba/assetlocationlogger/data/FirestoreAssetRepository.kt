package dev.csaba.assetlocationlogger.data

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dev.csaba.assetlocationlogger.data.remote.RemoteAsset
import dev.csaba.assetlocationlogger.data.remote.mapDateToTimestamp
import dev.csaba.assetlocationlogger.data.remote.mapToAsset
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject


class FirestoreAssetRepository : IAssetRepository {

    companion object {
        private const val ASSET_COLLECTION = "Assets"
        private const val ASSET_FIELD_TITLE = "title"
        private const val ASSET_FIELD_CREATED = "created"
        private const val ASSET_FIELD_UPDATED = "updated"
    }

    private val remoteDB = FirebaseFirestore.getInstance().apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    private val changeObservable = BehaviorSubject.create<List<DocumentSnapshot>> { emitter: ObservableEmitter<List<DocumentSnapshot>> ->
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

            val assetData = HashMap<String, Any>()
            assetData[ASSET_FIELD_TITLE] = asset.title
            assetData[ASSET_FIELD_CREATED] = mapDateToTimestamp(asset.created)
            assetData[ASSET_FIELD_UPDATED] = mapDateToTimestamp(asset.updated)

            remoteDB.collection(ASSET_COLLECTION)
                .add(assetData)
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

    // second option to add data
//    override fun addAsset(asset: Asset): Completable {
//        return Completable.create { emitter ->
//
//            remoteDB.collection(ASSET_COLLECTION)
//                .add(mapToRemoteAsset(asset))
//                .addOnSuccessListener {
//                    if (!emitter.isDisposed) {
//                        emitter.onComplete()
//                    }
//                }
//                .addOnFailureListener {
//                    if (!emitter.isDisposed) {
//                        emitter.onError(it)
//                    }
//                }
//        }
//    }

    override fun deleteAsset(assetId: String): Completable {
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

    override fun getChangeObservable(): Observable<List<Asset>> =
        changeObservable.hide()
            .observeOn(Schedulers.io())
            .map { list -> list.map(::mapDocumentToRemoteAsset).map(::mapToAsset) }
}
