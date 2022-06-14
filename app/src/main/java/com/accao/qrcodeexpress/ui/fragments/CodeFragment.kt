package com.accao.qrcodeexpress.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.accao.qrcodeexpress.QRGContents
import com.accao.qrcodeexpress.QRGEncoder
import com.accao.qrcodeexpress.R
import com.accao.qrcodeexpress.application.QrCodeApplication
import com.accao.qrcodeexpress.database.model.QrCode
import com.accao.qrcodeexpress.databinding.FragmentCodeBinding
import com.accao.qrcodeexpress.ui.ScanActivity
import com.accao.qrcodeexpress.ui.adapter.QrCodesAdapter
import com.accao.qrcodeexpress.ui.viewmodel.QrCodesViewModel
import com.accao.qrcodeexpress.ui.viewmodel.QrCodesViewModelFactory
import com.journeyapps.barcodescanner.ScanOptions


class CodeFragment : Fragment(R.layout.fragment_code) {

    private var _binding: FragmentCodeBinding? = null
    private val binding get() = _binding!!
    private var adapterQrCode = QrCodesAdapter {}
    private var listVerify : List<QrCode> = listOf()
    var qrapplication : QrCodeApplication? = null
    private lateinit var barcodeLauncher: ActivityResultLauncher<ScanOptions>
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

        if (qrapplication!!.link != null){
            openDialogQrLido(qrapplication!!.link.toString())
            qrapplication!!.link = null
        }


        binding.lnNavToScan.setOnClickListener {
           startActivity(Intent(requireContext(), ScanActivity::class.java))
        }


        binding.lnNavToCriar.setOnClickListener {
            findNavController().navigate(R.id.action_codeFragment_to_createCodeFragment)
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

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        qrapplication = requireActivity().application as QrCodeApplication
        qrCodeViewModel.allQrs.observe(this, Observer { qrCodes ->
            qrCodes?.let {
                listVerify = qrCodes.toMutableList()
                adapterQrCode.setDataSet(qrCodes)
                initRecyclerView()
                println(listVerify)
            }
        })
    }




    private fun filter(text: String) {
        val listaFiltrada: MutableList<QrCode> =
            mutableListOf()
        for (s in listVerify) {
            if (s.title.uppercase().contains(text.uppercase())) {
                listaFiltrada.add(s)
                adapterQrCode.filterList(listaFiltrada)
            }
        }

    }


    fun openDialogQrLido(strQrCode: String){
        val dialogQrLido = Dialog(requireContext())
        dialogQrLido.setContentView(R.layout.dialog_qr_lid)

        val txtTitulo = dialogQrLido.findViewById<EditText>(R.id.inputTitleQrCode)
        val txtConteudo = dialogQrLido.findViewById<EditText>(R.id.inputConteudoQrCode)
        val btnOpenLink = dialogQrLido.findViewById<CardView>(R.id.openLink)
        val btnCancelar = dialogQrLido.findViewById<LinearLayout>(R.id.btnCancelar)
        val btnSalvar = dialogQrLido.findViewById<LinearLayout>(R.id.btnSalvar)
        val imgQr = dialogQrLido.findViewById<ImageView>(R.id.imgQrCode)
        imgQr.setImageBitmap(generateQrCode(strQrCode))
        txtConteudo.setText(strQrCode)
        txtConteudo.isEnabled = false

        btnCancelar.setOnClickListener {
            dialogQrLido.dismiss()
        }

        btnSalvar.setOnClickListener {
            val qrCriado = QrCode(
                txtTitulo.text.toString(),
                txtConteudo.text.toString(),
                "black"
            )
            qrCodeViewModel.insert(qrCriado)
        }

        btnOpenLink.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(strQrCode))
            startActivity(browserIntent)
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




    fun generateQrCode(strQrCode: String) : Bitmap {
        val qrgEncoder = QRGEncoder(strQrCode, null, QRGContents.Type.TEXT, 500)
        qrgEncoder.colorBlack = Color.BLACK
        qrgEncoder.colorWhite = Color.WHITE

        val bitmapShare = qrgEncoder.bitmap
        return bitmapShare
    }



    fun sharedQrCode(bitmap: Bitmap) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(
            Intent.EXTRA_STREAM,
            Uri.parse("file://$bitmap")
        ) // Passe ao path da imagem aqui
        startActivity(Intent.createChooser(intent, "Compartilhat QRCode"))

    }


    override fun onStart() {
        super.onStart()


    }
}