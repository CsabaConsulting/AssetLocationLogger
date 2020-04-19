package dev.csaba.diygpsmanager.data

import java.util.Date


data class Report(
    var id: String = "",
    var lat: Float = 0f,
    var lon: Float = 0f,
    var battery: Float = 0f,
    var created: Date = Date()
)
