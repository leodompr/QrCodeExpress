package com.accao.qrcodeexpress.repository

import androidx.annotation.WorkerThread
import com.accao.qrcodeexpress.database.daos.QrCodeDao
import com.accao.qrcodeexpress.database.model.QrCode
import kotlinx.coroutines.flow.Flow

class QrCodeRepository(private val qrCodeDao: QrCodeDao) {

    val allQrs : Flow<List<QrCode>> = qrCodeDao.getAllQrs()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(qrCode: QrCode) {
        qrCodeDao.insert(qrCode)
    }

    suspend fun delete(qrCode: QrCode){
        qrCodeDao.deleteQr(qrCode)
    }

}