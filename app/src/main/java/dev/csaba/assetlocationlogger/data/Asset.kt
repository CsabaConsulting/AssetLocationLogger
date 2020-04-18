package dev.csaba.assetlocationlogger.data

import java.util.Date


data class Asset(
    var id: String = "",
    var title: String = "",
    var lockLat: Float = 0f,
    var lockLon: Float = 0f,
    var lockRadius: Float = 0f,
    var periodInterval: Int = 3600,
    var created: Date = Date(),
    var updated: Date = Date()
)
