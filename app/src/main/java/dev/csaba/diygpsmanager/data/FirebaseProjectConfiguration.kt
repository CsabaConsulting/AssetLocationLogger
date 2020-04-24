package dev.csaba.diygpsmanager.data


data class FirebaseProjectConfiguration(
    var projectId: String = "",
    var applicationId: String = "",
    var apiKey: String = "",
    var googleAuth: Boolean = false
)
