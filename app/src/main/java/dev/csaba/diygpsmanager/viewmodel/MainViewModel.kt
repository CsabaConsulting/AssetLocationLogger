package dev.csaba.diygpsmanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import dev.csaba.assetlocationlogger.addTo
import dev.csaba.assetlocationlogger.data.FirestoreAssetRepository
import dev.csaba.assetlocationlogger.data.IAssetRepository
import dev.csaba.assetlocationlogger.data.Asset


class MainViewModel(secondaryDB: FirebaseFirestore) : ViewModel() {

    private val _assetList = MutableLiveData<List<Asset>>()
    val assetList: LiveData<List<Asset>>
        get() = _assetList

    private var repository: IAssetRepository

    private val disposable = CompositeDisposable()

    init {
        repository = FirestoreAssetRepository(secondaryDB)
        repository.getChangeObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    _assetList.value = it
                },
                {
                    it.printStackTrace()
                }
            )
            .addTo(disposable)
    }

    fun deleteAsset(assetId: String) {
        repository.deleteAsset(assetId)
            .subscribeOn(Schedulers.io())
            .subscribe(
                {},
                {
                    it.printStackTrace()
                })
            .addTo(disposable)
    }

    fun addAsset(assetTitle: String) {
        repository.addAsset(Asset("${System.currentTimeMillis()}", assetTitle))
            .subscribeOn(Schedulers.io())
            .subscribe(
                {},
                {
                    it.printStackTrace()
                })
            .addTo(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
