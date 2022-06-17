package com.uruklabs.qrcodeexpress.ui.fragments

import android.Manifest
import android.app.Dialog

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.content.ClipData
import android.content.ClipboardManager
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.provider.Settings.ACTION_WIFI_SETTINGS
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.uruklabs.qrcodeexpress.R
import com.uruklabs.qrcodeexpress.application.QrCodeApplication
import com.uruklabs.qrcodeexpress.database.model.QrCode
import com.uruklabs.qrcodeexpress.databinding.FragmentCodeBinding
import com.uruklabs.qrcodeexpress.functions.QRGContents
import com.uruklabs.qrcodeexpress.functions.QRGEncoder
import com.uruklabs.qrcodeexpress.ui.adapter.QrCodesAdapter
import com.uruklabs.qrcodeexpress.ui.viewmodel.QrCodesViewModel
import com.uruklabs.qrcodeexpress.ui.viewmodel.QrCodesViewModelFactory
import java.io.ByteArrayOutputStream


class CodeFragment : Fragment(R.layout.fragment_code) {

    private var _binding: FragmentCodeBinding? = null
    private val binding get() = _binding!!
    private var adapterQrCode = QrCodesAdapter {
        openDialogShared(it)
    }
    private var listVerify: List<QrCode> = listOf()
    var qrapplication: QrCodeApplication? = null

    private val qrCodeViewModel: QrCodesViewModel by viewModels {
        QrCodesViewModelFactory((requireActivity().application as QrCodeApplication).repository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCodeBinding.inflate(inflater, container, false)
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()


        binding.navToCriar.setOnClickListener {
            findNavController().navigate(R.id.action_codeFragment_to_createCodeFragment)
        }

        binding.lnNavToCriar.setOnClickListener {
            findNavController().navigate(R.id.action_codeFragment_to_createCodeFragment)
        }


        binding.lnNavToScan.setOnClickListener {
            readQr()
        }

        binding.navToScan.setOnClickListener {
            readQr()
        }


        binding.inputSearchQr.addTextChangedListener(object : TextWatcher {  //Listener do EditText
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                filter(p0.toString()) //Função que filtra a busca e atualiza a RecycleView

            }
        })

    }

    override fun onResume() {
        super.onResume()
        initRecyclerView()


        if (qrapplication!!.link != "" && qrapplication!!.ssid == "") {
            openDialogQrRead(qrapplication!!.link.toString(), qrapplication!!.title.toString(), qrapplication!!.type.toString())
            qrapplication!!.link = ""
            qrapplication!!.title = ""
        }

        if (qrapplication!!.ssid != ""){
            openDialogQrReadWifi(qrapplication!!.link.toString(), qrapplication!!.ssid.toString(), qrapplication!!.passowrd.toString())
            qrapplication!!.link = ""
            qrapplication!!.ssid = ""
            qrapplication!!.passowrd = ""

        }


        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            })

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        qrapplication = requireActivity().application as QrCodeApplication
        qrCodeViewModel.allQrs.observe(this, Observer { qrCodes ->
            qrCodes?.let {
                listVerify = qrCodes.toMutableList()
                adapterQrCode.setDataSet(qrCodes)
                initRecyclerView()
            }
        })

    }


    private fun filter(text: String) {
        val listaFiltrada: MutableList<QrCode> =
            mutableListOf()
        for (s in listVerify) {
            if (s.title.uppercase().contains(text.uppercase())) {
                listaFiltrada.add(s)
            }
        }
        adapterQrCode.filterList(listaFiltrada)
    }


    fun openDialogShared(qrCode: QrCode) {
        val dialogQrLido = Dialog(requireContext())
        dialogQrLido.setContentView(R.layout.dialog_shared)

        val txtConteudo = dialogQrLido.findViewById<EditText>(R.id.inputConteudoQrCode)
        val btnSharedQr = dialogQrLido.findViewById<CardView>(R.id.sharedQr)
        val btnOpenLink = dialogQrLido.findViewById<CardView>(R.id.openLink)
        val btnDelete = dialogQrLido.findViewById<LinearLayout>(R.id.deleteQr)
        val txt_btn_open_link = dialogQrLido.findViewById<TextView>(R.id.txt_btn_open_link)

        if (qrCode.type == "txt"){
            txt_btn_open_link.text = "Copiar Texto"
        }

        btnDelete.setOnClickListener {
            qrCodeViewModel.delete(qrCode)
            dialogQrLido.dismiss()
        }

        val imgQr = dialogQrLido.findViewById<ImageView>(R.id.imgQrCode)
        imgQr.setImageBitmap(generateQrCode(qrCode.codeText, qrCode.colorName))
        txtConteudo.setText(qrCode.codeText)
        txtConteudo.isEnabled = false

        btnSharedQr.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                try {
                    shared(generateQrCode(qrCode.codeText, qrCode.colorName))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0
                )
            }


        }

        btnOpenLink.setOnClickListener {
            if (qrapplication!!.type == "txt"){
                val clipboardManager = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", qrCode.codeText)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(requireContext(), "Texto copiado!", Toast.LENGTH_LONG).show()

            } else {
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(qrCode.codeText))
                    startActivity(browserIntent)
                } catch (e: Exception) {
                  
                }
            }
        }


        dialogQrLido.window!!
            .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogQrLido.show()

    }


    fun shared(bitmapShare: Bitmap) {
        val imageUri: Uri = getImageUri(requireContext(), bitmapShare)!!

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        sendIntent.type = "image/jpg"
        startActivity(Intent.createChooser(sendIntent, imageUri.toString()))
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )

        return Uri.parse(path)

    }


    fun openDialogQrRead(strQrCode: String, title : String, type : String) {
        val dialogQrLido = Dialog(requireContext())
        dialogQrLido.setContentView(R.layout.dialog_qr_lid)

        val txtTitulo = dialogQrLido.findViewById<EditText>(R.id.inputTitleQrCode)
        val txtConteudo = dialogQrLido.findViewById<EditText>(R.id.inputConteudoQrCode)
        val btnOpenLink = dialogQrLido.findViewById<CardView>(R.id.openLink)
        val btnCancelar = dialogQrLido.findViewById<LinearLayout>(R.id.btnCancelar)
        val btnSalvar = dialogQrLido.findViewById<LinearLayout>(R.id.btnSalvar)
        val txtBtnOpenLink = dialogQrLido.findViewById<TextView>(R.id.textBtnOpenLink)
        val imgQr = dialogQrLido.findViewById<ImageView>(R.id.imgQrCode)
        imgQr.setImageBitmap(generateQrCode(strQrCode, "black"))
        txtConteudo.setText(strQrCode)
        txtTitulo.setText(title)

        if (qrapplication!!.type == "txt"){
            txtBtnOpenLink.text = "Copiar Texto"
        }


        btnCancelar.setOnClickListener {
            dialogQrLido.dismiss()
        }

        btnSalvar.setOnClickListener {
            val qrCriado = QrCode(
                txtTitulo.text.toString(),
                txtConteudo.text.toString(),
                "black",
                1,
                type
            )
            qrCodeViewModel.insert(qrCriado)
            dialogQrLido.dismiss()
        }

        btnOpenLink.setOnClickListener {
            if (qrapplication!!.type == "txt"){
                val clipboardManager = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", strQrCode)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(requireContext(), "Texto copiado!", Toast.LENGTH_LONG).show()


            } else {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(strQrCode))
                startActivity(browserIntent)
            } catch (e: Exception) {
                
            }
        }
        }


        dialogQrLido.window!!
            .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogQrLido.setCancelable(false)
        dialogQrLido.show()
    }



    fun openDialogQrReadWifi(strQrCode : String, ssid: String, passowrd: String) {
        val dialogQrLido = Dialog(requireContext())
        dialogQrLido.setContentView(R.layout.dialog_qr_wifi)

        val txtSSID = dialogQrLido.findViewById<EditText>(R.id.inputSSIDQrCode)
        val txtPassowrd = dialogQrLido.findViewById<EditText>(R.id.inputSenharCode)
        val btnOpenLink = dialogQrLido.findViewById<CardView>(R.id.openLink)
        val btnCancelar = dialogQrLido.findViewById<LinearLayout>(R.id.btnCancelar)
        val imgQr = dialogQrLido.findViewById<ImageView>(R.id.imgQrCode)
        imgQr.setImageBitmap(generateQrCode(strQrCode, "black"))
        txtPassowrd.setText(passowrd)
        txtSSID.setText(ssid)

        btnCancelar.setOnClickListener {
            dialogQrLido.dismiss()
        }

        btnOpenLink.setOnClickListener {
            startActivity(Intent(ACTION_WIFI_SETTINGS))
        }

        dialogQrLido.window!!
            .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogQrLido.setCancelable(false)
        dialogQrLido.show()
    }



    fun initRecyclerView() {
        binding.rvQrCodes.apply {
            adapter = adapterQrCode
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }

        if (adapterQrCode.itemCount > 0) {
            binding.imgEmptyList.isVisible = false
            binding.rvQrCodes.isVisible = true
        }
    }


    fun generateQrCode(strQrCode: String, color: String): Bitmap {
        val qrgEncoder = QRGEncoder(
            strQrCode,
            null,
            QRGContents.Type.TEXT,
            500
        )
        if (color == "purple") {
            qrgEncoder.colorBlack = Color.rgb(114, 80, 242)
        } else if (color == "red") {
            qrgEncoder.colorBlack = Color.rgb(255, 106, 106)
        } else if (color == "orange") {
            qrgEncoder.colorBlack = Color.rgb(255, 178, 106)
        } else if (color == "yellow") {
            qrgEncoder.colorBlack = Color.rgb(253, 217, 105)
        } else if (color == "green") {
            qrgEncoder.colorBlack = Color.rgb(44, 182, 125)
        } else if (color == "ciano") {
            qrgEncoder.colorBlack = Color.rgb(80, 242, 203)
        } else {
            qrgEncoder.colorBlack = Color.BLACK
        }
        qrgEncoder.colorWhite = Color.WHITE

        val bitmapShare = qrgEncoder.bitmap
        return bitmapShare
    }








    fun readQr() {
        val gmsBarcodeScanner = GmsBarcodeScanning.getClient(requireContext())
        gmsBarcodeScanner
            .startScan()
            .addOnSuccessListener { barcode: Barcode ->
                when (barcode.valueType) {
                    Barcode.TYPE_WIFI -> {
                        val ssid = barcode.wifi!!.ssid
                        val password = barcode.wifi!!.password
                        qrapplication?.ssid = ssid
                        qrapplication?.passowrd = password
                        qrapplication?.link = barcode.rawValue

                    }
                    Barcode.TYPE_URL -> {
                        val title = barcode.url!!.title
                        val url = barcode.url!!.url
                        qrapplication?.link = url
                        qrapplication?.title = title
                        qrapplication?.type = "link"

                    }
                   Barcode.TYPE_TEXT, Barcode.TYPE_EMAIL, Barcode.TYPE_PHONE -> {
                       val url = barcode.rawValue
                       qrapplication?.link = url
                       qrapplication?.type = "txt"
                   }

                }

            }
            .addOnFailureListener { e: Exception ->

            }
    }



    

}


