package dev.csaba.diygpsmanager.data

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single


interface IReportRepository {

    fun getAllReports(): Single<List<Report>>

    fun getChangeObservable(): Observable<List<Report>>
}
