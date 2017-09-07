package com.pedroartsimao.idtimageloader.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.pedroartsimao.idtimageloader.util.BitmapUtils

fun ByteArray.transformToOptimizedBitmap() : Bitmap {
    return BitmapFactory.decodeByteArray(
            this,
            0,
            size,
            BitmapUtils.createMemoryOptimizedBitmapOptions(this))
}