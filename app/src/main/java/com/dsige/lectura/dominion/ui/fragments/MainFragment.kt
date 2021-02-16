package com.dsige.lectura.dominion.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsige.lectura.dominion.R
import com.dsige.lectura.dominion.data.local.model.Servicio
import com.dsige.lectura.dominion.data.viewModel.SuministroViewModel
import com.dsige.lectura.dominion.data.viewModel.ViewModelFactory
import com.dsige.lectura.dominion.ui.activities.SuministroActivity
import com.dsige.lectura.dominion.ui.adapters.ServicioAdapter
import com.dsige.lectura.dominion.ui.listeners.OnItemClickListener
import com.google.android.material.button.MaterialButton
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"

class MainFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var suministroViewModel: SuministroViewModel
    private var usuarioId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuarioId = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    override fun onStart() {
        super.onStart()
        suministroViewModel.getServices()
    }

    private fun bindUI() {
        suministroViewModel =
            ViewModelProvider(this, viewModelFactory).get(SuministroViewModel::class.java)

        suministroViewModel.getServices()
        val layoutManager = LinearLayoutManager(context!!)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        val serviciodapter = ServicioAdapter(object : OnItemClickListener.ServicesListener {
            override fun onItemClick(s: Servicio, v: View, position: Int) {
                if (s.id_servicio == 1) {
                    tipoLectura(s)
                } else {
                    val intent = Intent(context!!, SuministroActivity::class.java)
                    intent.putExtra("estado", s.id_servicio)
                    intent.putExtra("nombre", s.nombre_servicio)
                    intent.putExtra("ubicacionId", s.ubicacion)
                    startActivity(intent)
                }

            }
        })
        recyclerView.adapter = serviciodapter
        suministroViewModel.getServicios().observe(viewLifecycleOwner) {
            serviciodapter.addItems(it)
        }
    }


    private fun tipoLectura(s: Servicio) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(requireContext(), R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_lectura, null)
        val linearLayoutNormales: LinearLayout = v.findViewById(R.id.linearLayoutNormales)
        val linearLayoutObservadas: LinearLayout = v.findViewById(R.id.linearLayoutObservadas)
        val linearLayoutReclamos: LinearLayout = v.findViewById(R.id.linearLayoutReclamos)
        val textViewCountNormales: TextView = v.findViewById(R.id.textViewCountNormales)
        val textViewCountObservadas: TextView = v.findViewById(R.id.textViewCountObservadas)
        val textViewCountReclamos = v.findViewById<TextView>(R.id.textViewCountReclamos)
        val buttonAceptar: MaterialButton = v.findViewById(R.id.buttonAceptar)
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        suministroViewModel.getTipoLectura()

        linearLayoutNormales.setOnClickListener {
            val intent = Intent(context, SuministroActivity::class.java)
            intent.putExtra("estado", 1)
            intent.putExtra("nombre", "Lectura")
            intent.putExtra("ubicacionId", s.ubicacion)
            startActivity(intent)
            dialog.dismiss()
        }

        linearLayoutObservadas.setOnClickListener {
            val intent = Intent(context, SuministroActivity::class.java)
            intent.putExtra("estado", 6)
            intent.putExtra("nombre", "Lectura Observadas")
            intent.putExtra("ubicacionId", s.ubicacion)
            startActivity(intent)
            dialog.dismiss()
        }

        linearLayoutReclamos.setOnClickListener {
            val intent = Intent(context!!, SuministroActivity::class.java)
            intent.putExtra("estado", 9)
            intent.putExtra("nombre", "Reclamos")
            intent.putExtra("ubicacionId", s.ubicacion)
            startActivity(intent)
        }

        suministroViewModel.getLecturas().observe(viewLifecycleOwner) {
            if (it[0] != 0) {
                textViewCountNormales.visibility = View.VISIBLE
                textViewCountNormales.text = it[0].toString()
            }
            if (it[1] != 0) {
                textViewCountObservadas.visibility = View.VISIBLE
                textViewCountObservadas.text = it[1].toString()
            }
            if (it[2] != 0) {
                textViewCountReclamos.visibility = View.VISIBLE
                textViewCountReclamos.text = it[2].toString()
            }
        }

        buttonAceptar.setOnClickListener {
            dialog.dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                }
            }
    }
}