package dev.csaba.assetlocationlogger.data

import java.util.Date


data class Asset(
    var id: String = "",
    var title: String = "",
    var created: Date = Date(),
    var updated: Date = Date()
)
