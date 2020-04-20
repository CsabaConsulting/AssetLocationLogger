package dev.csaba.diygpsmanager.data.remote

import dev.csaba.diygpsmanager.data.Report
import java.util.*
import kotlin.collections.HashMap


fun mapToReport(remoteReport: RemoteReport): Report {
    return Report(
        remoteReport.id,
        remoteReport.lat,
        remoteReport.lon,
        remoteReport.battery,
        remoteReport.created.toDate()
    )
}

fun mapToRemoteReport(report: Report): RemoteReport {
    return RemoteReport(
        report.id,
        report.lat,
        report.lon,
        report.battery,
        mapDateToTimestamp(report.created)
    )
}

fun mapToReportData(report: Report): HashMap<String, Any> {
    return hashMapOf(
        "lat" to report.lat,
        "lon" to report.lon,
        "battery" to report.battery,
        "created" to mapDateToTimestamp(report.created)
    )
}

fun mapToLockLocation(report: Report): HashMap<String, Any> {
    return hashMapOf(
        "lockLat" to report.lat,
        "lockLon" to report.lon,
        "modified" to mapDateToTimestamp(Date())
    )
}

fun getUnlockLocation(): HashMap<String, Any> {
    return hashMapOf(
        "lockLat" to 0.0,
        "lockLon" to 0.0,
        "modified" to mapDateToTimestamp(Date())
    )
}
