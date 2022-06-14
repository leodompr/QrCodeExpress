package com.uruklabs.qrcodeexpress.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uruklabs.qrcodeexpress.repository.QrCodeRepository

class QrCodesViewModelFactory (private val repository: QrCodeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QrCodesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QrCodesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}