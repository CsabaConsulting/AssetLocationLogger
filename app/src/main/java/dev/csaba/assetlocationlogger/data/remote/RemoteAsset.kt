package dev.csaba.assetlocationlogger.data.remote

import com.google.firebase.Timestamp


data class RemoteAsset(
    var id: String = "",
    var title: String = "",
    var lockLat: Float = 0f,
    var lockLon: Float = 0f,
    var lockRadius: Float = 0f,
    var periodInterval: Int = 3600,
    var created: Timestamp = Timestamp(0, 0),
    var updated: Timestamp = Timestamp(0, 0)
)
