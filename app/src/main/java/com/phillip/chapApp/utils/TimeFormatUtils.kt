package com.phillip.chapApp.utils

import android.content.Context
import com.phillip.chapApp.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TimeFormatUtils {
    fun compareWithCurrentTime(origin: Date, context: Context): String? {
        try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss")
            simpleDateFormat.timeZone = TimeZone.getDefault()
            if (isToday(origin)) {
                return context.getString(R.string.social_txt_today)
            } else if (isYesterday(origin)) {
                return context.getString(R.string.social_txt_yesterday)
            } else {
                return SimpleDateFormat("dd/MM/yyyy").format(origin)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun isToday(date: Date): Boolean {
        return isSameDay(date, Calendar.getInstance().time)
    }

    fun isYesterday(date: Date): Boolean {
        var yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DATE, -1);
        return isSameDay(date, yesterday.time)
    }

    fun isSameDay(date1: Date?, date2: Date?): Boolean {
        if (date1 == null || date2 == null) {
            throw IllegalArgumentException("The dates must not be null")
        }
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        return isSameDay(cal1, cal2)
    }


    fun isSameDay(cal1: Calendar?, cal2: Calendar?): Boolean {
        if (cal1 == null || cal2 == null) {
            throw IllegalArgumentException("The dates must not be null")
        }
        return cal1.get(Calendar.ERA) === cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) === cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) === cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun String.toDate(
        format: String, locale: Locale = Locale.getDefault()
    ): Date? {
        if (this.isBlank() || format.isBlank()) return null
        try {
            val simpleDateFormat = SimpleDateFormat(format, locale)
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return simpleDateFormat.parse(this)
        } catch (e: Exception) {
            return null
        }
    }

    fun Long.toTimeString(
        format: String, locale: Locale = Locale.getDefault()
    ): String? {
        if (format.isBlank()) return null
        return try {
            val simpleDateFormat = SimpleDateFormat(format, locale)
            simpleDateFormat.timeZone = TimeZone.getDefault()
            simpleDateFormat.format(this * 1000L)
        } catch (e: Exception) {
            null
        }
    }

    fun longTimeString(time: Long, format: String, locale: Locale = Locale.getDefault()): String? {
        if (format.isBlank()) return null
        return try {
            SimpleDateFormat(format, locale).format(Date(time))
        } catch (e: Exception) {
            null
        }
    }

    fun Long.toDate(): Date? {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        cal.timeInMillis = this * 1000
        return cal.time
    }


    fun getDurationBreakdown(millis: Long): String? {
        var millis = millis
        require(millis >= 0) { "Duration must be greater than zero!" }
        val days: Long = TimeUnit.MILLISECONDS.toDays(millis)
        millis -= TimeUnit.DAYS.toMillis(days)
        val hours: Long = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(millis)
        val sb = StringBuilder(64)
        if (days > 0) {
            sb.append(days)
            sb.append("d ")
        }
        if (hours > 0) {
            sb.append(hours)
            sb.append("h ")
        }

        if (minutes > 0) {
            sb.append(minutes)
            sb.append("m ")
        }
        if (seconds > 0) {
            sb.append(seconds)
            sb.append("s")
        }
        return sb.toString()
    }
}