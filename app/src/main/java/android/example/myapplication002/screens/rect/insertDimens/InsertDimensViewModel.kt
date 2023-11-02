package android.example.myapplication002.screens.rect.insertDimens

import android.app.Application
import android.example.myapplication002.screens.database.Reservoir
import android.example.myapplication002.screens.database.ReservoirDatabaseDao
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import timber.log.Timber

class InsertDimensViewModel(val database: ReservoirDatabaseDao,application: Application) :AndroidViewModel(application){
    val viewModelJob=Job()
    val uiScope= CoroutineScope(Dispatchers.Main+viewModelJob)

    var lengthText:String=""
    var widthText:String=""
    init {

    }
    fun storeMyDimens() {
        uiScope.launch{
            executeStoring()
        }
    }
    private suspend fun executeStoring(){
        withContext(Dispatchers.IO){
            var reserve=Reservoir()
            reserve=database.getReserve()
            reserve.type="Στέρνα"
            reserve.length=lengthText.toDouble()
            reserve.width=widthText.toDouble()
            database.update(reserve)
            Timber.i("Stored values are width:${reserve.width} and length:${reserve.length} ")

        }
    }

}