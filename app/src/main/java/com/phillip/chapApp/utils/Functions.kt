package com.phillip.chapApp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.webkit.MimeTypeMap
import com.phillip.chapApp.R


object Functions {

    fun getChannelName(name: String): String {
        if (!name.isNullOrBlank()) {
            var first = "C"
            var seccond = "N"
            val separated = name.split(" ".toRegex()).toTypedArray()
            if (separated.size == 1) {
                first = separated[0].get(0).toString()
                seccond = separated[0].last().toString()
                return (first + seccond).toUpperCase()
            } else {
                if (!separated[0].isNullOrBlank()) {
                    first = separated[0].get(0).toString()
                    seccond = separated.last().get(0).toString()
                    return (first + seccond).toUpperCase()
                } else {
                    first = name.get(0).toString()
                    seccond = name.last().toString()
                    return (first + seccond).toUpperCase()
                }
            }
        }
        return "CN"
    }

    fun Context.getBackgroundAvatar(name: String?): Int {
        if (!name.isNullOrBlank()) {
            val fist = name.get(0).toString().toLowerCase()
            return when (fist) {
                "0" -> getColor(R.color.color1)
                "a" -> getColor(R.color.color2)
                "b" -> getColor(R.color.color3)
                "c" -> getColor(R.color.color4)
                "d" -> getColor(R.color.color5)
                "e" -> getColor(R.color.color6)
                "g" -> getColor(R.color.color24)
                "h" -> getColor(R.color.color24)
                "i" -> getColor(R.color.color24)
                "k" -> getColor(R.color.color24)
                "l" -> getColor(R.color.color24)
                "m" -> getColor(R.color.color12)
                "n" -> getColor(R.color.color13)
                "o" -> getColor(R.color.color14)
                "p" -> getColor(R.color.color15)
                "r" -> getColor(R.color.color16)
                "s" -> getColor(R.color.color17)
                "t" -> getColor(R.color.color18)
                "u" -> getColor(R.color.color19)
                "v" -> getColor(R.color.color20)
                "x" -> getColor(R.color.color21)
                "y" -> getColor(R.color.color22)
                else -> getColor(R.color.color23)
            }
        }
        return getColor(R.color.color23)
    }

    fun convertByeToString(byte: Long): String {
        var str = "0 B"
        if (byte > 0) {
            if (byte <= 1024) {
                return byte.toString() + " B"
            } else if (byte > 1024 && byte < 10240 * 1024) {
                return (byte / 1024).toString() + "KB"
            } else if (byte > 10240 * 1024 && byte < 10240 * 1024 * 1024) {
                return (byte / (1024 * 1024)).toString() + "MB"
            } else if (byte > 10240 * 1024 * 1024 && byte < 10240 * 1024 * 1024 * 1024) {
                return (byte / (1024 * 1024 * 1024)).toString() + "GB"
            }
        }
        return str
    }

    fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            val mime = MimeTypeMap.getSingleton()
            type = mime.getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun getScreenShot(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas)
        else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }
}