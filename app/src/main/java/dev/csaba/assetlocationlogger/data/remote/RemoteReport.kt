package dev.csaba.assetlocationlogger.data.remote

import com.google.firebase.Timestamp


data class RemoteReport(
    var id: String = "",
    var lat: Float = 0f,
    var lon: Float = 0f,
    var battery: Float = 0f,
    var created: Timestamp = Timestamp(0, 0)
)
