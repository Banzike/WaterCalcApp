package android.example.myapplication002.screens.insertDepth

import android.app.Application
import android.example.myapplication002.screens.database.Reservoir
import android.example.myapplication002.screens.database.ReservoirDatabaseDao
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.*
import timber.log.Timber


class InsertDepthViewModel(val database: ReservoirDatabaseDao, application: Application) :AndroidViewModel(application) {

    val viewModelJob= Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope= CoroutineScope(Dispatchers.Main+viewModelJob)
    var newDepthString=""

    init {

    }


    private suspend fun getReservefromDatabase():Reservoir?{
        return withContext(Dispatchers.IO){
            database.getReserve()
        }
    }

    fun updateDepth() {
        uiScope.launch {
            executeUpdateDepth()
        }
    }
    private suspend fun executeUpdateDepth(){
        withContext(Dispatchers.IO) {
            val newDepth=newDepthString.toDouble()
            val reserve=database.getReserve()
            reserve.depth= newDepth
            reserve.lastUpdated=System.currentTimeMillis()
            database.update(reserve)

        }
    }

}