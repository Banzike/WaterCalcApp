package com.waterreserve.myapplication002.screens.database

import androidx.room.*
import kotlinx.coroutines.Job

@Entity
    (tableName = "measurement_table",
foreignKeys = [
    ForeignKey( entity = Reservoir::class,
        parentColumns = ["reservoirId"],
        childColumns= ["reserve_Id"])
    ,
    ForeignKey( entity = Reservoir::class,
        parentColumns = ["creationDate"],
        childColumns= ["reserve_created_Date"])
   ,
    ForeignKey( entity = User::class,
        parentColumns = ["userName"],
        childColumns= ["owner"])
    ],
    indices = [
        Index(value= ["reserve_Id"]),
        Index(value= ["reserve_created_Date"]),
        Index(value=["dateCreated"], unique = true)
    ]
)
data class Measurement(

    @PrimaryKey(autoGenerate = true)
    var measurementId:Long=0L,

    @ColumnInfo
    var measurementTypes:String="waterlvl",

    @ColumnInfo
    var dateCreated:Long=System.currentTimeMillis(),

    @ColumnInfo(name="reserve_Id")
    var reserveId:Long=0L,

    @ColumnInfo(name="reserve_created_Date")
    var reservecreatedDate:Long=0L,

    @ColumnInfo(name="owner")
    var ownerIs:String=""



 //   @ForeignKey()
//    reserveId
//    user has reservoir, reservoir has measurements look foreign key relationships

)
