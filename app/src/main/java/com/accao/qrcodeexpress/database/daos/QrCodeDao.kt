package com.accao.qrcodeexpress.database.daos

import androidx.room.*
import com.accao.qrcodeexpress.database.model.QrCode
import kotlinx.coroutines.flow.Flow


@Dao
interface QrCodeDao {

    @Query("SELECT * FROM qrcode_table")
    fun getAllQrs(): Flow<List<QrCode>>

    @Query("SELECT * FROM qrcode_table WHERE uid = :uid")
    fun getQr(uid: Int) : QrCode

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(qrCode: QrCode)

    @Delete
    suspend fun deleteQr(qrCode: QrCode)

}