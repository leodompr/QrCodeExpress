package com.accao.qrcodeexpress.database.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qrcode_table")
data class QrCode(
    @ColumnInfo(name = "qr")
    val title : String,
    val codeText : String,
    val colorName: String,
){
    @PrimaryKey(autoGenerate = true)
    var uid : Int = 0
}
