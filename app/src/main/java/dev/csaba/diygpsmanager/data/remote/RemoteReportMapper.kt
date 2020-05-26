package dev.csaba.diygpsmanager.data.remote

import dev.csaba.diygpsmanager.data.Report
import kotlin.collections.HashMap


fun mapToReport(remoteReport: RemoteReport): Report {
    return Report(
        remoteReport.id,
        remoteReport.lat,
        remoteReport.lon,
        remoteReport.speed,
        remoteReport.battery,
        remoteReport.created.toDate()
    )
}

fun mapToRemoteReport(report: Report): RemoteReport {
    return RemoteReport(
        report.id,
        report.lat,
        report.lon,
        report.speed,
        report.battery,
        mapDateToTimestamp(report.created)
    )
}

fun mapToReportData(report: Report): HashMap<String, Any> {
    return hashMapOf(
        "lat" to report.lat,
        "lon" to report.lon,
        "speed" to report.speed,
        "battery" to report.battery,
        "created" to mapDateToTimestamp(report.created)
    )
}
