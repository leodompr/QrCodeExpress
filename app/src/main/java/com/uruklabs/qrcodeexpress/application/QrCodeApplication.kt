package com.uruklabs.qrcodeexpress.application

import android.app.Application
import com.uruklabs.qrcodeexpress.database.QrCodeRoomDatabase
import com.uruklabs.qrcodeexpress.repository.QrCodeRepository

class QrCodeApplication : Application() {
    var  link : String? = null
    val database by lazy { QrCodeRoomDatabase.getDataBase(this) }
    val repository by lazy { QrCodeRepository(database.qrCodeDao()) }


}