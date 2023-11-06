package com.waterreserve.myapplication002

import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.content.BroadcastReceiver
import com.waterreserve.myapplication002.screens.droplet.PreferenceKeys.ID_VALUE
import com.waterreserve.myapplication002.screens.droplet.dataStore
import android.os.Bundle
import androidx.navigation.NavDeepLinkBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking



class NotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

     //Retrieving DataStore flow
        val idValueFlow: Flow<Long?> = context?.dataStore?.data?.map { preferences ->
            preferences[ID_VALUE]
        } ?: flowOf(null)

        val bundle = Bundle()
        var iD:Long=0L
        //Retrieving data from DataStore flow
        CoroutineScope(Dispatchers.Default).launch {
            iD = idValueFlow.firstOrNull() ?: 0L
            bundle.putLong("idIs", iD)
            bundle.putBoolean("isOld", true)
            Timber.i("iD is $iD")

            //DataStore will be used instead of preferences because it is recommended by the devs
            //It uses protocol buffers, coroutines and Flow to store data asynchronously, consistently, and transactionally.


            //Creating deepLink intent with the desired data
            val deepLinkIntent = NavDeepLinkBuilder(context!!)
                .setGraph(R.navigation.navigation)
                .setDestination(R.id.waterLevelFragment)
                .setArguments(bundle)
                .createPendingIntent()

            val builder = NotificationCompat.Builder(context, "channel_1")
                .setSmallIcon(R.drawable.well_notification)
                .setContentTitle("Ενημέρωση του ταμιευτήρα $iD")
                .setContentText("Μετρήστε το επίπεδο νερού και εισάγετέ το")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(deepLinkIntent)

            Timber.i("Notification builder built")
            with(NotificationManagerCompat.from(context)) {
                //Notification permissions already requested at droplet fragment
                notify(R.integer.update_reserve_notification_ID, builder.build())
                Timber.i("Notification built")
            }
        }
    }
//    private fun convertFlowToLong(flow: Flow<Long?>): Long = runBlocking {
//        var result: Long = 0
//        flow.collect {
//            result = it!!
//        }
//        result
//    }
}