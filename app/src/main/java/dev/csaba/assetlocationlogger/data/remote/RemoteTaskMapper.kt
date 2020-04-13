package dev.csaba.assetlocationlogger.data.remote

import com.google.firebase.Timestamp
import dev.csaba.assetlocationlogger.data.Asset
import java.util.Date


fun mapToAsset(remoteAsset: RemoteAsset): Asset {
    return Asset(
        remoteAsset.id,
        remoteAsset.title,
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
        mapDateToTimestamp(asset.created),
        mapDateToTimestamp(asset.updated)
    )
}
