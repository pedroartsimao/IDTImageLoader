package com.pedroartsimao.idtimageloader

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.webkit.URLUtil
import com.pedroartsimao.idtimageloader.extensions.*
import java.io.File
import java.net.URL

class ImageLoaderService : IntentService("ImageLoaderService") {

    companion object {
        val INTENT_FILTER_ACTION = "image_loader_intent_filter"

        val RESULT_STATUS_ERROR = 0
        val RESULT_STATUS_SUCCESS = 1

        val INTENT_RESULT_STATUS = "image_loader_service_status"
        val INTENT_RESULT_DATA_BITMAP = "image_loader_service_data_bitmap"
        val INTENT_RESULT_DATA_MESSAGE = "image_loader_service_data_message"

        private val EXTRA_URL = "extra_url"
        private val EXTRA_FILE_PATH = "extra_file_path"
        private val EXTRA_LOAD_FROM_CACHE = "extra_load_from_cache"

        private val IMAGE_ROTATION = 180f

        var serviceRunning: Boolean = false
            private set

        fun getIntent(context: Context, url: String, downloadFilePath: String): Intent {
            return Intent(context, ImageLoaderService::class.java).apply {
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_FILE_PATH, downloadFilePath)
                putExtra(EXTRA_LOAD_FROM_CACHE, false)
            }
        }

        fun getCacheLoaderIntent(context: Context, downloadFilePath: String): Intent {
            return Intent(context, ImageLoaderService::class.java).apply {
                putExtra(EXTRA_FILE_PATH, downloadFilePath)
                putExtra(EXTRA_LOAD_FROM_CACHE, true)
            }
        }
    }

    override fun onHandleIntent(intent: Intent) {
        serviceRunning = true

        val url = intent.getStringExtra(EXTRA_URL)
        val filePath = intent.getStringExtra(EXTRA_FILE_PATH)

        val resultIntent =
                if (intent.getBooleanExtra(EXTRA_LOAD_FROM_CACHE, false)) loadCachedImage(filePath)
                else loadUrlImage(url, filePath)

        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent)

        serviceRunning = false
    }

    private fun loadUrlImage(url: String, filePath: String): Intent {
        val resultIntent = Intent(INTENT_FILTER_ACTION)
        var operationStatus = RESULT_STATUS_ERROR

        if (URLUtil.isNetworkUrl(url)) {
            try {
                URL(url).apply {
                    if (isAnImageUrl()) {
                        downloadUrlContent().let {
                            File(filePath).storeAsBitmap(it)
                            it.transformToOptimizedBitmap().let {
                                it.rotate(IMAGE_ROTATION).let {
                                    resultIntent.putExtra(INTENT_RESULT_DATA_BITMAP, it)
                                    operationStatus = RESULT_STATUS_SUCCESS
                                }
                            }
                        }
                    } else {
                        resultIntent.putExtra(INTENT_RESULT_DATA_MESSAGE, getString(R.string.error_invalid_url_not_image))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                resultIntent.putExtra(INTENT_RESULT_DATA_MESSAGE, exceptionMapper(e))
            }
        } else {
            resultIntent.putExtra(INTENT_RESULT_DATA_MESSAGE, getString(R.string.error_invalid_url))
        }

        resultIntent.putExtra(INTENT_RESULT_STATUS, operationStatus)

        return resultIntent
    }

    private fun loadCachedImage(filePath: String): Intent {
        val resultIntent = Intent(INTENT_FILTER_ACTION)
        var operationStatus = RESULT_STATUS_ERROR

        File(filePath).run {
            if (exists()) {
                try {
                    loadAsBitmap().let {
                        resultIntent.putExtra(INTENT_RESULT_DATA_BITMAP, it.rotate(IMAGE_ROTATION))
                        operationStatus = RESULT_STATUS_SUCCESS
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    resultIntent.putExtra(INTENT_RESULT_DATA_MESSAGE, exceptionMapper(e))
                }
            } else {
                resultIntent.putExtra(INTENT_RESULT_DATA_MESSAGE, getString(R.string.no_image_loaded))
            }
        }

        resultIntent.putExtra(INTENT_RESULT_STATUS, operationStatus)

        return resultIntent
    }

    private fun exceptionMapper(e: Exception): String {
        var message: String = getString(R.string.error_unexpected)
        when (e) {
            is IllegalArgumentException -> message = getString(R.string.error_invalid_url)
            else -> message = e.message ?: message
        }
        return message
    }

}