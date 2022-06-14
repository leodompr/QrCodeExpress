package com.accao.qrcodeexpress.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.accao.qrcodeexpress.repository.QrCodeRepository

class QrCodesViewModelFactory (private val repository: QrCodeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QrCodesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QrCodesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}