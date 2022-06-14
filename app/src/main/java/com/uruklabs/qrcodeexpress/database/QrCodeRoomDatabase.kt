package com.uruklabs.qrcodeexpress.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uruklabs.qrcodeexpress.database.daos.QrCodeDao
import com.uruklabs.qrcodeexpress.database.model.QrCode


@Database(entities = [QrCode::class], version = 1, exportSchema = false)
abstract class QrCodeRoomDatabase : RoomDatabase() {

    abstract fun qrCodeDao() : QrCodeDao

    companion object {

        @Volatile
        private var INSTANCE : QrCodeRoomDatabase? = null

        fun getDataBase(context: Context) : QrCodeRoomDatabase {
            return INSTANCE ?: synchronized(this){
                val instace = Room.databaseBuilder(
                    context.applicationContext,
                    QrCodeRoomDatabase::class.java,
                    "qrCode_table"
                ).build()
                INSTANCE = instace
                instace
            }
        }

    }


}