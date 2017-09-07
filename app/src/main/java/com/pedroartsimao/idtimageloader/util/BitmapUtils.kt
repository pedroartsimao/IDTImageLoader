package com.pedroartsimao.idtimageloader.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory

class BitmapUtils {

    companion object {

        fun createMemoryOptimizedBitmapOptions(filePath: String): BitmapFactory.Options {
            return BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                BitmapFactory.decodeFile(filePath, this)
                inSampleSize = calculateInSampleSize(this)
                inJustDecodeBounds = false
                inPreferredConfig = Bitmap.Config.RGB_565
            }
        }

        fun createMemoryOptimizedBitmapOptions(byteArray: ByteArray): BitmapFactory.Options {
            return BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, this)
                inSampleSize = calculateInSampleSize(this)
                inJustDecodeBounds = false
                inPreferredConfig = Bitmap.Config.RGB_565
            }
        }

        private fun calculateInSampleSize(options: BitmapFactory.Options): Int {
            val reqHeight = 1500
            val reqWidth = 1500
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val halfHeight = height / 3
                val halfWidth = width / 3
                while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }
    }
}