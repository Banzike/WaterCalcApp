package android.example.myapplication002

import android.annotation.SuppressLint
import android.content.res.Resources
import android.example.myapplication002.screens.database.Reservoir
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("EEEE MMM-dd-yyyy")   // ' Time: 'HH:mm
        .format(systemTime).toString()
}

fun formatReserves(reserves: List<Reservoir>, resources: Resources): Spanned {
    val sb = StringBuilder()
    sb.apply {
        append(resources.getString(R.string.title))
        reserves.forEach {
         //   append("<br>")
            append(resources.getString(R.string.time_created))
            append("\t${convertLongToDateString(it.creationDate)}<br>")
            append("\tΓεωγραφικό Μήκος: ${it.locationX}")
            append("\tΓεωγραφικό Πλάτος: ${it.locationY}")
            append("\t Διάμετρος: ${it.diameter} μέτρα")
            append("\t Βάθος: ${it.depth} μέτρα\n")
            append("\t Επίπεδο νερού: ${it.waterlvl} μέτρα\n\n")
            append("\t Χρώμα: ${it.imageColour}")
            append("\t Χρήστης: ${it.userusername}")
//            if (it.endTimeMilli != it.startTimeMilli) {
//                append(resources.getString(R.string.end_time))
//                append("\t${convertLongToDateString(it.endTimeMilli)}<br>")
//                append(resources.getString(R.string.quality))
//                append("\t${convertNumericQualityToString(it.sleepQuality, resources)}<br>")
//                append(resources.getString(R.string.hours_slept))
//                // Hours
//                append("\t ${it.endTimeMilli.minus(it.startTimeMilli) / 1000 / 60 / 60}:")
//                // Minutes
//                append("${it.endTimeMilli.minus(it.startTimeMilli) / 1000 / 60}:")
//                // Seconds
//                append("${it.endTimeMilli.minus(it.startTimeMilli) / 1000}<br><br>")
//            }
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        return HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}


