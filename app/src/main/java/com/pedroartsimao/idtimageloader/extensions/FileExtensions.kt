package com.pedroartsimao.idtimageloader.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.pedroartsimao.idtimageloader.util.BitmapUtils
import java.io.File
import java.io.FileOutputStream

fun File.storeAsBitmap(byteArray: ByteArray) {
    FileOutputStream(absoluteFile).run {
        write(byteArray)
        close()
    }
}

fun File.loadAsBitmap(): Bitmap {
    return BitmapFactory.decodeFile(absolutePath,
            BitmapUtils.createMemoryOptimizedBitmapOptions(absolutePath))
}