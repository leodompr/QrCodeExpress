package com.uruklabs.qrcodeexpress.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qrcode_table")
data class QrCode(
    @ColumnInfo(name = "qr")
    val title : String,
    val codeText : String,
    val colorName: String,
    val origin: Int,
    val type : String
){
    @PrimaryKey(autoGenerate = true)
    var uid : Int = 0
}
