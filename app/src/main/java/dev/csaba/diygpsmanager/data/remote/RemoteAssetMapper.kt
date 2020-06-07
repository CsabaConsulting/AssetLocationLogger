package dev.csaba.diygpsmanager.data.remote

import com.google.firebase.Timestamp
import dev.csaba.diygpsmanager.data.Asset
import dev.csaba.diygpsmanager.data.mapValueToInterval
import java.util.Date


fun mapToAsset(remoteAsset: RemoteAsset): Asset {
    return Asset(
        remoteAsset.id,
        remoteAsset.title,
        remoteAsset.lock,
        remoteAsset.lockLat,
        remoteAsset.lockLon,
        remoteAsset.lockRadius,
        remoteAsset.lockAlert,
        remoteAsset.periodInterval,
        remoteAsset.created.toDate(),
        remoteAsset.updated.toDate()
    )
}

fun mapDateToTimestamp(date: Date): Timestamp {
    return Timestamp(date.time / 1000, (date.time % 1000 * 1000).toInt())
}

fun mapToAssetData(asset: Asset): HashMap<String, Any> {
    return hashMapOf(
        "title" to asset.title,
        "lock" to asset.lock,
        "lockLat" to asset.lockLat,
        "lockLon" to asset.lockLon,
        "lockRadius" to asset.lockRadius,
        "lockAlert" to asset.lockAlert,
        "periodInterval" to asset.periodInterval,
        "created" to mapDateToTimestamp(asset.created),
        "updated" to mapDateToTimestamp(asset.updated)
    )
}

fun mapToLockRadiusUpdate(lockRadius: Int): HashMap<String, Any> {
    return hashMapOf(
        "lockRadius" to lockRadius,
        "updated" to mapDateToTimestamp(Date())
    )
}

fun mapPeriodIntervalToProgress(periodInterval: Int): Int {
    val intervals = intArrayOf(0, 10, 60, 600, 3600, 86400)

    for ((index, interval) in intervals.withIndex()) {
        if (periodInterval <= interval)
            return index
    }

    return intervals.size - 1
}

fun mapPeriodIntervalProgressToSeconds(periodIntervalProgress: Int): Int {
    val intervals = intArrayOf(0, 10, 60, 600, 3600, 86400)
    return mapValueToInterval(intervals, periodIntervalProgress)
}

fun mapToPeriodIntervalUpdate(periodIntervalProgress: Int): HashMap<String, Any> {
    return hashMapOf(
        "periodInterval" to mapPeriodIntervalProgressToSeconds(periodIntervalProgress),
        "updated" to mapDateToTimestamp(Date())
    )
}

fun getLockUpdate(lockState: Boolean): HashMap<String, Any> {
    if (lockState)
        return hashMapOf(
            "lock" to lockState,
            "updated" to mapDateToTimestamp(Date())
        )

    return hashMapOf(
        "lock" to lockState,
        "lockLat" to .0,
        "lockLon" to .0,
        "updated" to mapDateToTimestamp(Date())
    )
}
