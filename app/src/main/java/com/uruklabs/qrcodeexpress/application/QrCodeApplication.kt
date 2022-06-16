package com.uruklabs.qrcodeexpress.application

import android.app.Application
import android.content.Context
import com.uruklabs.qrcodeexpress.database.QrCodeRoomDatabase
import com.uruklabs.qrcodeexpress.repository.QrCodeRepository

class QrCodeApplication : Application() {
    var  link : String? = ""
    var  title : String? = ""
    var  ssid : String? = ""
    var  passowrd : String? = ""
    var type : String? = ""
    val database by lazy { QrCodeRoomDatabase.getDataBase(this) }
    val repository by lazy { QrCodeRepository(database.qrCodeDao()) }


}