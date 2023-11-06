package com.waterreserve.myapplication002.screens.database

import androidx.room.*
//import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    indices = [Index(value=["lastUpdated"], unique = false),
               Index(value=["user"],unique=false),
               Index(value=["userusername"],unique=false),
               Index(value=["creationDate"],unique=true),
               Index(value=["userDateJoined"], unique = false)],

    foreignKeys = [
        ForeignKey(entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["user"],
            onDelete = CASCADE),
        
        ForeignKey(entity=User::class,
            parentColumns=["dateJoined"],
            childColumns=["userDateJoined"],
            onDelete = CASCADE),

        ForeignKey(entity = User::class,
            parentColumns = ["userName"],
            childColumns = ["userusername"],
            onDelete = CASCADE)
        ],


    tableName = "reservoir_table"
)
data class Reservoir(

    @PrimaryKey(autoGenerate = true)
    var reservoirId: Long=0L,

    @ColumnInfo
    var reservoirName: String? =null,

    @ColumnInfo
    val creationDate:Long=System.currentTimeMillis(),

    @ColumnInfo
    var type:String="",

    @ColumnInfo
    var diameter:Double=0.0,

    @ColumnInfo
    var width: Double=0.0,

    @ColumnInfo
    var length: Double=0.0,

    @ColumnInfo
    var depth:Double=0.0,

    @ColumnInfo
    var waterlvl:Double=0.0,

    @ColumnInfo
    var capacity: Double=0.0,

    @ColumnInfo
    var locationX: Double=37.008902,

    @ColumnInfo
    var locationY: Double=24.747056,

    @ColumnInfo
    var lastUpdated: Long=System.currentTimeMillis(),

    @ColumnInfo
    var filledRatio:Double=0.0,

    @ColumnInfo
    var imageColour:String="black",

    @ColumnInfo
    var filledVolume:Double=0.0,

//    @ColumnInfo
//    var userName:String="",

//    @ColumnInfo
//    var primaryUsage:Enum(String:"Irrigation") //Household AnimalFarm Public

    //var secondaryUsage:Enum
    //var tertiaryUsage:Enum


    @ColumnInfo(name="user")
    var user:Long=0L,

    @ColumnInfo(name="userusername")
    var userusername:String="",
    
    @ColumnInfo(name="userDateJoined")
    var userDateJoined:Long=0L,

    @ColumnInfo
    var nextUpdateDue:Long=0L,

    @ColumnInfo
    var updateMeEvery:Long=null?:(1000*60*60*24*7)

)