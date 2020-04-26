package dev.csaba.diygpsmanager.data.remote

import com.google.firebase.Timestamp
import dev.csaba.diygpsmanager.data.Asset
import java.util.Date


fun mapToAsset(remoteAsset: RemoteAsset): Asset {
    return Asset(
        remoteAsset.id,
        remoteAsset.title,
        remoteAsset.lockLat,
        remoteAsset.lockLon,
        remoteAsset.lockRadius,
        remoteAsset.periodInterval,
        remoteAsset.created.toDate(),
        remoteAsset.updated.toDate()
    )
}

fun mapDateToTimestamp(date: Date): Timestamp {
    return Timestamp(date.time / 1000, (date.time % 1000 * 1000).toInt())
}

fun mapToRemoteAsset(asset: Asset): RemoteAsset {
    return RemoteAsset(
        asset.id,
        asset.title,
        asset.lockLat,
        asset.lockLon,
        asset.lockRadius,
        asset.periodInterval,
        mapDateToTimestamp(asset.created),
        mapDateToTimestamp(asset.updated)
    )
}

fun mapToAssetData(asset: Asset): HashMap<String, Any> {
    return hashMapOf(
        "title" to asset.title,
        "lockLat" to asset.lockLat,
        "lockLon" to asset.lockLon,
        "lockRadius" to asset.lockRadius,
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

    if (periodIntervalProgress < 0)
        return intervals.first()

    if (periodIntervalProgress >= intervals.size)
        return intervals.last()

    return intervals[periodIntervalProgress]
}

fun mapToPeriodIntervalUpdate(periodIntervalProgress: Int): HashMap<String, Any> {
    return hashMapOf(
        "periodInterval" to mapPeriodIntervalProgressToSeconds(periodIntervalProgress),
        "updated" to mapDateToTimestamp(Date())
    )
}
