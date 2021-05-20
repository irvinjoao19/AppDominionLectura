package com.dsige.lectura.dominion.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.dsige.lectura.dominion.R
import com.dsige.lectura.dominion.data.local.model.GrandesClientes
import com.dsige.lectura.dominion.data.viewModel.ClienteViewModel
import com.dsige.lectura.dominion.data.viewModel.ViewModelFactory
import com.dsige.lectura.dominion.helper.Util
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_file.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FileFragment : DaggerFragment(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabVerificate -> if (c.estado == 7) {
                load()
                clienteViewModel.verificateFile(c)
            } else {
                clienteViewModel.setError("Favor de completar el primer formulario.")
            }
            R.id.fabClose -> requireActivity().finish()
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var clienteViewModel: ClienteViewModel

    lateinit var c: GrandesClientes
    private var clienteId: Int = 0
    lateinit var builder: AlertDialog.Builder
    private var dialog: AlertDialog? = null
    private var viewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            clienteId = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_file, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        clienteViewModel =
            ViewModelProvider(this, viewModelFactory).get(ClienteViewModel::class.java)
        viewPager = activity!!.findViewById(R.id.viewPager)
        clienteViewModel.getClienteById(clienteId).observe(viewLifecycleOwner) {
            c = it
            editTextCodigoEMR.setText(c.codigoEMR)
        }

        clienteViewModel.mensajeError.observe(viewLifecycleOwner) { s ->
            if (s != null) {
                closeLoad()
                if (s == "Favor de completar el primer formulario.") {
                    Util.dialogMensaje(context!!, "Mensaje", s)
                    viewPager?.currentItem = 0
                } else {
                    textViewMensaje.text = s
                }
            }
        }

        clienteViewModel.mensajeSuccess.observe(viewLifecycleOwner) { s ->
            if (s != null) {
                closeLoad()
                fabVerificate.visibility = View.GONE
                fabClose.visibility = View.VISIBLE
                textViewMensaje.text = s
            }
        }

        fabVerificate.setOnClickListener(this)
        fabClose.setOnClickListener(this)
    }

    private fun load() {
        builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        textViewTitle.text = String.format("%s", "Verificando...")
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
        fun newInstance(param1: Int) =
            FileFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                }
            }
    }
}