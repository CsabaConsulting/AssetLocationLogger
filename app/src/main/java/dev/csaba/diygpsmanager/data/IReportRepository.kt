package dev.csaba.diygpsmanager.data

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single


interface IReportRepository {

    fun getAllReports(asset: Asset): Single<List<Report>>

    fun addReport(report: Report): Completable

    fun getChangeObservable(): Observable<List<Report>>
}
