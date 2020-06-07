package dev.csaba.diygpsmanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import dev.csaba.diygpsmanager.addTo
import dev.csaba.diygpsmanager.data.FirestoreAssetRepository
import dev.csaba.diygpsmanager.data.IAssetRepository
import dev.csaba.diygpsmanager.data.Asset


class MainViewModel(firestore: FirebaseFirestore) : ViewModel() {

    private val _assetList = MutableLiveData<List<Asset>>()
    val assetList: LiveData<List<Asset>>
        get() = _assetList

    private var repository: IAssetRepository = FirestoreAssetRepository(firestore)

    private val disposable = CompositeDisposable()

    init {
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

    fun setAssetLockState(assetId: String, lockState: Boolean) {
        repository.setAssetLockState(assetId, lockState)
            .subscribeOn(Schedulers.io())
            .subscribe(
                {},
                {
                    it.printStackTrace()
                })
            .addTo(disposable)
    }

    fun setAssetLockRadius(assetId: String, lockRadius: Int) {
        repository.setAssetLockRadius(assetId, lockRadius)
            .subscribeOn(Schedulers.io())
            .subscribe(
                {},
                {
                    it.printStackTrace()
                })
            .addTo(disposable)
    }

    fun setAssetPeriodInterval(assetId: String, periodIntervalProgress: Int) {
        repository.setAssetPeriodInterval(assetId, periodIntervalProgress)
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
