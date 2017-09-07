package com.pedroartsimao.idtimageloader.extensions

import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.net.URL

fun URL.isAnImageUrl(): Boolean {
    return openConnection()
            .getHeaderField("Content-Type")
            .startsWith("image/")
}

fun URL.downloadUrlContent(): ByteArray {
    var response = ByteArray(0)

    BufferedInputStream(openStream()).run {
        ByteArrayOutputStream().run {
            val buffer = ByteArray(1024)
            var n = read(buffer)
            while (n != -1) {
                this.write(buffer, 0, n)
                n = read(buffer)
            }
            this.close()

            response = this.toByteArray()
        }
        this.close()
    }

    return response
}