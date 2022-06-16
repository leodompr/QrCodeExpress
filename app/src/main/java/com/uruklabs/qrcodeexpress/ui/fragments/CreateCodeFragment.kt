package com.uruklabs.qrcodeexpress.ui.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.uruklabs.qrcodeexpress.*
import com.uruklabs.qrcodeexpress.application.QrCodeApplication
import com.uruklabs.qrcodeexpress.database.model.QrCode
import com.uruklabs.qrcodeexpress.databinding.FragmentCreateCodeBinding
import com.uruklabs.qrcodeexpress.functions.QRGContents
import com.uruklabs.qrcodeexpress.functions.QRGEncoder
import com.uruklabs.qrcodeexpress.ui.viewmodel.QrCodesViewModel
import com.uruklabs.qrcodeexpress.ui.viewmodel.QrCodesViewModelFactory


class CreateCodeFragment : Fragment(R.layout.fragment_create_code) {

    private var _binding: FragmentCreateCodeBinding? = null
    private val binding get() = _binding!!
    private var color = "black"
    var bitmapShare: Bitmap? = null


    private val qrCodeViewModel: QrCodesViewModel by viewModels {
        QrCodesViewModelFactory((requireActivity().application as QrCodeApplication).repository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateCodeBinding.inflate(inflater, container, false)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSalvar.setOnClickListener {
            if (chekText()) {
                val qrCriado = QrCode(
                    binding.inputTitleQrCode.text.toString(),
                    binding.inputConteudoQrCode.text.toString(),
                    color,
                    0
                )
                qrCodeViewModel.insert(qrCriado)
                exit()
            } else {
                Toast.makeText(requireContext(), "Nenhum QrCode disponível.", Toast.LENGTH_LONG)
                    .show()
            }
        }

        binding.btnCancelar.setOnClickListener {
            exit()
        }


        binding.cvRedColor.setOnClickListener {
            if (chekText()) {
                color = "red"
                generateQrCode(binding.inputConteudoQrCode.text.toString(), 255, 106, 106)
            } else toastColor()

        }

        binding.cvOrangeColor.setOnClickListener {
            if (chekText()) {
                color = "orange"
                generateQrCode(binding.inputConteudoQrCode.text.toString(), 255, 178, 106)
            } else toastColor()

        }

        binding.cvYellowColor.setOnClickListener {
            if (chekText()) {
                color = "yellow"
                generateQrCode(binding.inputConteudoQrCode.text.toString(), 253, 217, 105)
            } else toastColor()

        }

        binding.cvGreenColor.setOnClickListener {
            if (chekText()) {
                color = "green"
                generateQrCode(binding.inputConteudoQrCode.text.toString(), 44, 182, 125)
            } else toastColor()

        }

        binding.cvCianoColor.setOnClickListener {
            if (chekText()) {
                color = "ciano"
                generateQrCode(binding.inputConteudoQrCode.text.toString(), 80, 242, 203)
            } else toastColor()
        }

        binding.cvPurpleColor.setOnClickListener {
            if (chekText()) {
                color = "purple"
                generateQrCode(binding.inputConteudoQrCode.text.toString(), 114, 80, 242)
            } else toastColor()

        }


    }


    fun toastColor() {
        Toast.makeText(requireContext(), "Favor preencher todos os campos.", Toast.LENGTH_LONG)
            .show()
    }


    fun chekText(): Boolean {
        return binding.inputConteudoQrCode.text.toString()
            .isNotEmpty() && binding.inputTitleQrCode.text.toString().isNotEmpty()
    }




    fun generateQrCode(strQrCode: String, r: Int, g: Int, b: Int) {
        val qrgEncoder = QRGEncoder(strQrCode, null, QRGContents.Type.TEXT, 500)
        qrgEncoder.colorBlack = Color.rgb(r, g, b)
        qrgEncoder.colorWhite = Color.WHITE
        try {
            bitmapShare = qrgEncoder.bitmap

            binding.imgQrCode.setImageBitmap(bitmapShare)
        } catch (e: WriterException) {

        }
    }


    fun exit() {
        findNavController().navigate(R.id.action_createCodeFragment_to_codeFragment)

    }



}