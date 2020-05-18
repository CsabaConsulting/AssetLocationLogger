package dev.csaba.diygpsmanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import dev.csaba.diygpsmanager.addTo
import dev.csaba.diygpsmanager.data.FirestoreReportRepository
import dev.csaba.diygpsmanager.data.IReportRepository
import dev.csaba.diygpsmanager.data.Report
import java.util.Date


class MapsViewModel(firestore: FirebaseFirestore, assetId: String, lookBackDate: Date) : ViewModel() {

    private val _reportList = MutableLiveData<List<Report>>()
    val reportList: LiveData<List<Report>>
        get() = _reportList

    private var repository: IReportRepository = FirestoreReportRepository(firestore, assetId, lookBackDate)

    private val disposable = CompositeDisposable()

    init {
        repository.getChangeObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    _reportList.value = it
                },
                {
                    it.printStackTrace()
                }
            )
            .addTo(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
