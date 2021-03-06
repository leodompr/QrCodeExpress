package com.uruklabs.qrcodeexpress.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.uruklabs.qrcodeexpress.database.model.QrCode
import com.uruklabs.qrcodeexpress.repository.QrCodeRepository
import kotlinx.coroutines.launch

class QrCodesViewModel(private val repository: QrCodeRepository) : ViewModel() {

    val allQrs: LiveData<List<QrCode>> = repository.allQrs.asLiveData()

    fun insert(qrCode: QrCode) = viewModelScope.launch {
        repository.insert(qrCode)
    }

    fun delete(qrCode: QrCode) = viewModelScope.launch {
        repository.delete(qrCode)
    }


}