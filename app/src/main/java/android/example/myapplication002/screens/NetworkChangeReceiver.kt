package android.example.myapplication002.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import android.example.myapplication002.screens.UploadDownloadWorker



class NetworkChangeReceiver : BroadcastReceiver() {
    var isConnected:Boolean=false
    override fun onReceive(context: Context?, intent: Intent?) {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo =cm.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            // Network is connected
            Toast.makeText(context, "Συνδέθηκε στο διαδίκτυο", Toast.LENGTH_SHORT).show()
            //Worker is called
            val workManager = WorkManager.getInstance(context)
            val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadDownloadWorker>().build()
            workManager.enqueue(uploadWorkRequest)

        } else {
            // Network is disconnected
            Toast.makeText(context, "Αποσυνδέθηκε από το διαδίκτυο", Toast.LENGTH_SHORT).show()
             }
       }
    }