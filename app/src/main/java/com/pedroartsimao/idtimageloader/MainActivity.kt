package com.pedroartsimao.idtimageloader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private val FILE_NAME = "downloaded_image.bmp"

    private lateinit var filePath: String

    private val imageLoaderReceiver = ImageLoaderBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        filePath = filesDir.path + File.separator + FILE_NAME

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(imageLoaderReceiver, IntentFilter(ImageLoaderService.INTENT_FILTER_ACTION))

        if (!ImageLoaderService.serviceRunning) {
            showProgress()
            startService(ImageLoaderService.getCacheLoaderIntent(this@MainActivity, filePath))
        }

        buttonSubmitUrl.setOnClickListener {
            showProgress()
            val url = editTextUrl.text.toString()
            startService(ImageLoaderService.getIntent(this@MainActivity, url, filePath))
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(imageLoaderReceiver)
        super.onDestroy()
    }

    private fun setViewsVisibility(imageViewVisibility: Int, messageVisibility: Int, progressBarVisibility: Int) {
        imageView.visibility = imageViewVisibility
        textViewMessage.visibility = messageVisibility
        progressBar.visibility = progressBarVisibility
    }

    private fun showImageView(bitmap: Bitmap) {
        imageView.setImageBitmap(bitmap)
        setViewsVisibility(View.VISIBLE, View.GONE, View.GONE)
        buttonSubmitUrl.isEnabled = true
    }

    private fun showMessage(message: String) {
        textViewMessage.text = message
        setViewsVisibility(View.GONE, View.VISIBLE, View.GONE)
        buttonSubmitUrl.isEnabled = true
    }

    private fun showProgress() {
        setViewsVisibility(View.GONE, View.GONE, View.VISIBLE)
        buttonSubmitUrl.isEnabled = false
    }

    private inner class ImageLoaderBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = intent.getIntExtra(ImageLoaderService.INTENT_RESULT_STATUS,
                    ImageLoaderService.RESULT_STATUS_ERROR)
            when (status) {
                ImageLoaderService.RESULT_STATUS_SUCCESS -> showImageView(intent.getParcelableExtra(
                        ImageLoaderService.INTENT_RESULT_DATA_BITMAP))
                ImageLoaderService.RESULT_STATUS_ERROR -> showMessage(intent.getStringExtra(
                        ImageLoaderService.INTENT_RESULT_DATA_MESSAGE)
                        ?: getString(R.string.error_unexpected))
            }
        }

    }
}
