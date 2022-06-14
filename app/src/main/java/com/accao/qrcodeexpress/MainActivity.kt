package com.accao.qrcodeexpress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.accao.qrcodeexpress.application.QrCodeApplication
import com.accao.qrcodeexpress.databinding.ActivityMainBinding
import com.accao.qrcodeexpress.ui.viewmodel.QrCodesViewModel
import com.accao.qrcodeexpress.ui.viewmodel.QrCodesViewModelFactory

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}