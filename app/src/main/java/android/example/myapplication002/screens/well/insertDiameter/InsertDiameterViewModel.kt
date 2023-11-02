package android.example.myapplication002.screens.well.insertDiameter

import android.app.Application
import android.example.myapplication002.screens.database.Reservoir
import android.example.myapplication002.screens.database.ReservoirDatabaseDao
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import timber.log.Timber


class InsertDiameterViewModel(val database: ReservoirDatabaseDao, application: Application) :AndroidViewModel(application) {

    val viewModelJob= Job()


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope= CoroutineScope(Dispatchers.Main+viewModelJob)
    var newDiameterString=""

    init {
       typeWell()
    }

    private fun typeWell(){
        uiScope.launch {
            typeisWell()
        }
    }

    private suspend fun typeisWell(){
        withContext(Dispatchers.IO) {
            val reserve = database.getReserve()
            reserve.type = "Πηγάδι"
            database.update(reserve)
        }
    }



    fun updateDiameter() {
        uiScope.launch {
            executeUpdateDiameter()
        }
    }
    private suspend fun executeUpdateDiameter(){
        withContext(Dispatchers.IO) {
            val newDiameter=newDiameterString.toDouble()
            val reserve=database.getReserve()
            reserve.diameter = newDiameter
            database.update(reserve)
        }
    }



}