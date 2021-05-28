package com.dsige.lectura.dominion.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.ViewModelProvider
import com.dsige.lectura.dominion.R
import com.dsige.lectura.dominion.data.viewModel.SendViewModel
import com.dsige.lectura.dominion.data.viewModel.ViewModelFactory
import com.dsige.lectura.dominion.helper.Util
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_send.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SendFragment : DaggerFragment(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnEnvio -> {
                btnEnvio.visibility = View.INVISIBLE
                confirmSend(1)
            }
            R.id.btnFile -> {
                btnFile.visibility = View.INVISIBLE
                confirmSend(2)
            }
        }

    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var sendViewModel: SendViewModel
    lateinit var builder: AlertDialog.Builder
    private var dialog: AlertDialog? = null

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_send, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        sendViewModel =
            ViewModelProvider(this, viewModelFactory).get(SendViewModel::class.java)

        sendViewModel.getRegistros().observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.isEmpty()) {
                    textViewValue.visibility = View.INVISIBLE
                }
                textViewValue.text = it.size.toString()
                txt1.text = String.format("Registros : %s", it.size)
            }
        }

        sendViewModel.getPhotos().observe(viewLifecycleOwner) {
            if (it != null) {
                txt2.text = String.format("Fotos : %s", it.size)
            }
        }

        sendViewModel.mensajeSuccess.observe(viewLifecycleOwner) {
            btnEnvio.visibility = View.VISIBLE
            btnFile.visibility = View.VISIBLE
            closeLoad()
            if (it == "Ok") {
                Looper.myLooper()?.let { i ->
                    Handler(i).postDelayed({
                        load("Enviando Registros..")
                        sendViewModel.sendSuministro()
                    }, 800)
                }
            } else {
                Util.toastMensaje(requireContext(), it)
            }
        }

        sendViewModel.mensajeError.observe(viewLifecycleOwner) {
            btnEnvio.visibility = View.VISIBLE
            btnFile.visibility = View.VISIBLE
            closeLoad()
            Util.toastMensaje(requireContext(), it)
        }

        btnEnvio.setOnClickListener(this)
        btnFile.setOnClickListener(this)
    }

    private fun confirmSend(tipo: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Mensaje")
            .setMessage(
                String.format(
                    "Antes de enviar asegurate de contar con internet !.%s",
                    "\nDeseas enviar los Registros ?."
                )
            )
            .setPositiveButton("Aceptar") { dialog, _ ->
                when (tipo) {
                    1 -> {
                        load("Enviando fotos...")
                        sendViewModel.sendFiles(requireContext())
                    }
                    2 -> {
                        load("Reenviando fotos...")
                        sendViewModel.sendPhoto(requireContext())
                    }
                }
                dialog.dismiss()
            }.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun load(title: String) {
        builder = AlertDialog.Builder(ContextThemeWrapper(requireContext(), R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_login, null)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        builder.setView(view)
        textViewTitle.text = title
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    private fun closeLoad() {
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SendFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}