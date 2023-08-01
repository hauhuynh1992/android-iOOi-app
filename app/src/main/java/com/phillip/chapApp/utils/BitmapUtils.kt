package com.phillip.chapApp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.*

object BitmapUtils {
    /**
     * decode file to image with sample size
     *
     * @param file
     * @param sampleSize
     * @return
     */
    fun decodeFileSize(file: File, sampleSize: Int): Bitmap? {
        var fis: FileInputStream? = null
        try {
            // Decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = sampleSize
            o2.inPreferredConfig = Bitmap.Config.RGB_565

            fis = FileInputStream(file)
            return BitmapFactory.decodeStream(fis, null, o2)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (fis != null) {
                try {
                    fis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return null
    }

    /**
     * decode file to bitmap with max size
     *
     * @param photoFilePath
     * @param maxSize
     * @return
     */
    fun decodeFile(photoFilePath: String, maxSize: Int): Bitmap? {
        try {
            val file = File(photoFilePath)
            if (!file.exists()) {
            }
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(photoFilePath, options)

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, maxSize, maxSize)

            // remove true
            options.inJustDecodeBounds = false

            // get bitmap
            val bm = BitmapFactory.decodeFile(photoFilePath, options)

            // Read EXIF Data
            val exif = ExifInterface(photoFilePath)
            val orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
            val orientation =
                if (orientString != null) Integer.parseInt(orientString) else ExifInterface.ORIENTATION_NORMAL
            var rotationAngle = 0
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270
            // Rotate Bitmap
            val matrix = Matrix()
            matrix.setRotate(
                rotationAngle.toFloat(),
                bm.width.toFloat() / 2,
                bm.height.toFloat() / 2
            )

            // return bitmap with rotate
            return Bitmap.createBitmap(bm, 0, 0, options.outWidth, options.outHeight, matrix, true)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * calculate sample size
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    /**
     * create temp file from bitmap
     *
     * @param context
     * @param bitmap
     * @param name
     * @return
     */
    fun createImageFromBitmap(context: Context, bitmap: Bitmap, name: String): File {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)

        val f = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .toString() + File.separator + name + ".png"
        )
        var fo: BufferedOutputStream? = null
        try {
            f.createNewFile()
            // write the bytes in file
            fo = BufferedOutputStream(FileOutputStream(f))
            fo.write(bytes.toByteArray())

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fo != null) {
                try {
                    fo.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return f
    }

    /**
     * delete temp file
     *
     * @param context
     */
    fun deleteImageTemp(context: Context) {
        val f = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        for (path in f!!.list()) {
            try {
                val file = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        .toString() + File.separator + path
                )
                val uri = Uri.fromFile(file)
                if (file.delete()) {
                    callBroadCast(context, uri)
                    deleteFileFromMediaStore(context, File(uri.path))
                }
            } catch (ex: Exception) {
            }

        }
    }

    /**
     * callBroadCast => not working with android 4.0
     *
     * @param context
     * @param uri
     */
    fun callBroadCast(context: Context, uri: Uri) {
        MediaScannerConnection.scanFile(
            context,
            arrayOf(uri.path),
            null
        ) { path, uri -> Log.e("AAA", "Scanned $path:") }
    }

    /**
     * delete file from media store
     *
     * @param context
     * @param file
     */
    fun deleteFileFromMediaStore(context: Context, file: File) {
        val contentResolver = context.contentResolver
        var canonicalPath: String
        try {
            canonicalPath = file.canonicalPath
        } catch (e: IOException) {
            canonicalPath = file.absolutePath
        }

        val uri = MediaStore.Files.getContentUri("external")
        val result = contentResolver.delete(
            uri,
            MediaStore.Files.FileColumns.DATA + "=?", arrayOf(canonicalPath)
        )
        if (result == 0) {
            val absolutePath = file.absolutePath
            if (absolutePath != canonicalPath) {
                contentResolver.delete(
                    uri,
                    MediaStore.Files.FileColumns.DATA + "=?", arrayOf(absolutePath)
                )
            }
        }
    }
}
