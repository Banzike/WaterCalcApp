package android.example.myapplication002.screens.database

import androidx.room.*
import kotlinx.coroutines.Job

@Entity(tableName = "user_table",
    indices=[
    Index(value=["userName"],unique=true),
    Index(value=["firebaseUserId"], unique = true),
    Index(value=["dateJoined"], unique = true)
    ]
)
data class User (
    @PrimaryKey(autoGenerate = true)
    var userId:Long=0L,

    @ColumnInfo
    var firebaseUserId:String?=null,

    @ColumnInfo
    val dateJoined:Long=System.currentTimeMillis(),

    @ColumnInfo
    var userName:String="",

    @ColumnInfo
    var passWord:String="",

    @ColumnInfo
    var userSurname:String="",

    @ColumnInfo
    var userPhone:String="",

    //@ColumnInfo
    //var user:Enum<> social identity Resident Tourist Scientist Farmer Municipality Businessman

    @ColumnInfo
    var placeOfResidence:String="",

    @ColumnInfo
    var userEmail:String="",

    @ColumnInfo
    var lastloggedIn:Long=System.currentTimeMillis()
)

