package com.pedroartsimao.idtimageloader.extensions

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.rotate(degress: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degress)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}