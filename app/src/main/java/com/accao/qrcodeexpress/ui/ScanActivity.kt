package com.accao.qrcodeexpress.ui

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.accao.qrcodeexpress.MainActivity
import com.accao.qrcodeexpress.application.QrCodeApplication
import com.accao.qrcodeexpress.repository.QrCodeRepository
import com.google.zxing.Result
import com.google.zxing.client.android.Intents.Scan.RESULT
import com.google.zxing.client.android.Intents.Scan.RESULT_BYTES
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    var qraplication : QrCodeApplication? = null
    var scannerView: ZXingScannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)
        setContentView(scannerView)
        setPermission()
        qraplication = applicationContext as QrCodeApplication
    }

    override fun handleResult(p0: Result?) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        qraplication!!.link = p0.toString()
        println(qraplication!!.link)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        scannerView?.setResultHandler(this)
        scannerView?.startCamera()
    }

    override fun onStop() {
        super.onStop()
        scannerView?.stopCamera()
        onBackPressed()
    }

    private fun setPermission() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.CAMERA),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        applicationContext,
                        "Você precisa permitir o uso da câmera.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }
}