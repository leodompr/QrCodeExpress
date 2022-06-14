package com.accao.qrcodeexpress.application

import android.app.Application
import com.accao.qrcodeexpress.database.QrCodeRoomDatabase
import com.accao.qrcodeexpress.repository.QrCodeRepository

class QrCodeApplication : Application() {
    var  link : String? = null
    val database by lazy { QrCodeRoomDatabase.getDataBase(this) }
    val repository by lazy { QrCodeRepository(database.qrCodeDao()) }


}