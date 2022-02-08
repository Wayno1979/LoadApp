package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var selectedUrl: String? = null
    private var selectedFileName: String? = null

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createChannel()

        custom_button.setOnClickListener {
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action = intent?.action

            if (downloadID == id && action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query()
                query.setFilterById(id)

                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val downloadStatus = if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        "Success"
                    } else {
                        "Failed"
                    }

                    //send notification
                    notificationManager.sendNotification(
                        CHANNEL_ID,
                        getString(R.string.notification_description),
                        applicationContext,
                        selectedFileName!!,
                        downloadStatus
                    )

                    custom_button.buttonState = ButtonState.Completed
                    custom_button.isEnabled = true
                }
            }
        }
    }

    private fun download() {
        if (selectedUrl != null) {

            // radio button selected, try to download
            custom_button.buttonState = ButtonState.Loading
            custom_button.isEnabled = false

            val request = DownloadManager.Request(Uri.parse(selectedUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            // enqueue puts the download request in the queue.
            downloadID = downloadManager.enqueue(request)
        } else {
            // no radio button selected
            Toast.makeText(
                applicationContext,
                getString(R.string.select_radio_button),
                Toast.LENGTH_LONG)
                .show()
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {

            val isChecked = view.isChecked

            when (view.getId()) {
                R.id.radio_glide -> if (isChecked) {
                    selectedUrl = GLIDE_URL
                    selectedFileName = getString(R.string.glide)
                }
                R.id.radio_load_app -> if (isChecked)  {
                    selectedUrl = LOAD_APP_URL
                    selectedFileName = getString(R.string.load_app)
                }
                R.id.radio_retrofit -> if (isChecked) {
                    selectedUrl = RETROFIT_URL
                    selectedFileName = getString(R.string.retrofit)
                }
            }
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "channelId"
    }
}
