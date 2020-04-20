package dev.csaba.diygpsmanager.data

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single


interface IAssetRepository {

    fun getAllAssets(): Single<List<Asset>>

    fun addAsset(asset: Asset): Completable
    fun deleteAsset(assetId: String): Completable
    fun lockUnlockAsset(assetId: String): Completable
    fun setAssetLockRadius(assetId: String, lockRadius: Int): Completable
    fun setAssetPeriodInterval(assetId: String, periodIntervalProgress: Int): Completable

    fun getChangeObservable(): Observable<List<Asset>>

//    fun getAllReports(asset: Asset): Single<List<Report>>
//    fun addReport(asset: Asset, report: Report): Completable
}
