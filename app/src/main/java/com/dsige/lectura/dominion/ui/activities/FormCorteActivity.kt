package com.dsige.lectura.dominion.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dsige.lectura.dominion.ui.adapters.DetalleGrupoAdapter
import com.dsige.lectura.dominion.R
import com.dsige.lectura.dominion.data.local.model.*
import com.dsige.lectura.dominion.data.viewModel.SuministroViewModel
import com.dsige.lectura.dominion.data.viewModel.ViewModelFactory
import com.dsige.lectura.dominion.helper.Gps
import com.dsige.lectura.dominion.helper.Util
import com.dsige.lectura.dominion.ui.adapters.MenuItemAdapter
import com.dsige.lectura.dominion.ui.adapters.MotivoAdapter
import com.dsige.lectura.dominion.ui.listeners.OnItemClickListener
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_form_corte.*
import javax.inject.Inject

class FormCorteActivity : DaggerAppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonGrabar -> formRegistro()
            R.id.editTextMotivo -> dialogSpinner(1)
            R.id.editTextDialogObservacion -> dialogSpinner(2)
            R.id.editTextArtefacto -> dialogSpinner(3)
            R.id.editTextResultado -> dialogSpinner(4)
            R.id.editTextUbicacion -> dialogSpinner(5)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Util.hideKeyboard(this)
        when (item.itemId) {
            R.id.before -> beforeOrAfterSuministro(estado, "Before")
            R.id.after -> beforeOrAfterSuministro(estado, "Next")
        }
        return super.onOptionsItemSelected(item)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var suministroViewModel: SuministroViewModel

    private var orden: Int = 0
    private var ordenOperario: Int = 0
    private var titulo: String = ""
    private var estado: Int = 0

    private var online: Int = 0

    // Se utiliza para Lectura Recuperadas
    private var recuperada = 0
    private var pidePhoto: String = ""
    private var pideLectura: String = ""


    private var lecturaMax: Double = 0.0
    private var lecturaMin: Double = 0.0


    private var contrato: String = ""
    private var fechaAsignacion: String = ""
    private var grupoId: Int = 0
    private var ubicacionId: Int = 0
    private var direccion: String = ""

    private var latitud = ""
    private var longitud = ""
    private var suministro_Numero = ""


    lateinit var r: Registro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_corte)

        r = Registro()

        val b = intent.extras
        if (b != null) {
            titulo = b.getString("nombre", "")
            orden = b.getInt("orden")
            estado = b.getInt("estado")
            ubicacionId = b.getInt("ubicacionId")
            bindUI()
        }
    }

    private fun bindUI() {
        suministroViewModel =
            ViewModelProvider(this, viewModelFactory).get(SuministroViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = titulo
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            Util.hideKeyboard(this)
            finish()
        }

        suministroViewModel.user.observe(this) {
            online = it.operario_EnvioEn_Linea
            r.iD_Operario = it.iD_Operario
        }

        getLecturaByOrden(orden)

        Util.showKeyboard(editTextLectura, this)
        editTextLectura.setOnEditorActionListener { v, _, _ ->
            if (v.text.toString().isNotEmpty()) {
                Util.hideKeyboard(this)
                if (ubicacionId == 1) {
                    dialogSpinner(5)
                }
            }
            true
        }

        textInputLayoutArtefacto.hint = String.format("%s", "Motivo")
        textViewUnidadLectura.visibility = View.VISIBLE
        textViewAvisoCorte.visibility = View.VISIBLE

        buttonGrabar.setOnClickListener(this)
        editTextMotivo.setOnClickListener(this)
        editTextUbicacion.setOnClickListener(this)
        imageViewMap.setOnClickListener(this)
        editTextDialogObservacion.setOnClickListener(this)

        textViewContrato.visibility = View.VISIBLE
        linearLayoutCorte2.visibility = View.VISIBLE
        linearLayout.visibility = View.GONE
        editTextArtefacto.setOnClickListener(this)
        editTextResultado.setOnClickListener(this)
        editTextCausa.setOnClickListener(this)

        suministroViewModel.mensajeSuccess.observe(this) {
            if (it == "photo") {
                goToPhoto()
            } else {
                Util.toastMensaje(this, it)
                beforeOrAfterSuministro(estado, "Next")
            }
        }
        suministroViewModel.mensajeError.observe(this) {
            Util.toastMensaje(this, it)
        }
    }

    private fun getDetalleTask(id: Int) {
        suministroViewModel.getDetalleGrupoById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<DetalleGrupo> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
                override fun onNext(t: DetalleGrupo) {
                    pidePhoto = t.pideFoto
                    pideLectura = t.pideLectura
                    r.codigo_Resultado = t.iD_DetalleGrupo.toString()
                    r.causaNombre = t.descripcion
                    editTextCausa.setText(t.descripcion)
                    if (pideLectura == "NO") {
                        editTextLectura.text = null
                        editTextLectura.isEnabled = false
                    } else {
                        editTextLectura.isEnabled = true
                    }
                }
            })
    }

    private fun clearView() {

    }

    /**
     * estado = Tipo de Servicio
     * */
    private fun beforeOrAfterSuministro(estado: Int, type: String) {
        clearView()
        when (type) {
            "Before" -> suministroViewModel.getSuministroLeft(estado, orden, ordenOperario)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Int> {
                    override fun onComplete() {}
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {}
                    override fun onNext(t: Int) {
                        getLecturaByOrden(t)
                    }
                })
            "Next" -> suministroViewModel.getSuministroRight(estado, orden, ordenOperario)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Int> {
                    override fun onComplete() {}
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {}
                    override fun onNext(t: Int) {
                        getLecturaByOrden(t)
                    }
                })
        }
    }

    private fun getLecturaByOrden(number: Int) {
        suministroViewModel.getSuministroCorteByOrdenTask(number)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SuministroCortes> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onNext(s: SuministroCortes) {
//                    clearText()
                    showSuministro(s)
                }
            })
    }

    private fun showSuministro(it: SuministroCortes) {
        lecturaMax = it.suministro_LecturaMaxima.toDouble()
        lecturaMin = it.suministro_LecturaMinima.toDouble()

        textViewContrato.text = String.format("C : %s", it.suministro_Numero)
        contrato = it.suministro_Numero
        fechaAsignacion = it.fechaAsignacion
        textViewMedidor.text = String.format("Medidor : %s", it.suministro_Medidor)
        textViewCliente.text = it.suministro_Cliente
        textViewDireccion.text = it.suministro_Direccion
        direccion = it.suministro_Direccion
        textViewOrden.text = String.format("Orden : %s", it.orden)
        textViewUnidadLectura.text = String.format("U.L : %s", it.suministro_UnidadLectura)
        textViewAvisoCorte.text = String.format("A.C : %s", it.avisoCorte)

        r.iD_Suministro = it.iD_Suministro
        r.registro_TipoProceso = it.suministro_TipoProceso
        r.fecha_Sincronizacion_Android = Util.getFechaActual()

        r.suministro_Numero = it.suministro_Numero.toInt()
        orden = it.orden
        ordenOperario = it.suministroOperario_Orden

        latitud = it.latitud
        longitud = it.longitud
        suministro_Numero = it.suministro_Numero

        suministroViewModel.getRegistroBySuministroTask(it.iD_Suministro)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Registro> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {
                    getComboObservacion()
                }

                override fun onNext(d: Registro) {
                    r = d
                    editTextResultado.setText(d.parentIdNombre)
                    if (d.parentId == 55) {
                        editTextCausa.isEnabled = false
                    }

                    editTextArtefacto.setText(r.artefactoNombre)
                    editTextCausa.setText(r.causaNombre)
                    editTextMotivo.setText(d.motivoNombre)

                    when (d.registro_Desplaza) {
                        "1" -> editTextUbicacion.setText(String.format("%s", "Externo"))
                        "2" -> editTextUbicacion.setText(String.format("%s", "Interno"))
                        "3" -> editTextUbicacion.setText(String.format("%s", "Sotano"))
                        "4" -> editTextUbicacion.setText(String.format("%s", "Azotea"))
                    }

                    grupoId = d.grupo_Incidencia_Codigo.toInt()
                    editTextDialogObservacion.setText(d.grupoIndicenciaCodigoNombre)
                    if (grupoId == 5) {
                        layoutMedidor.visibility = View.VISIBLE
                        layoutLectura.visibility = View.VISIBLE
                        layoutNroLectura.visibility = View.GONE
                        editTextLectura.text = null

                        val obs: List<String>? = d.registro_Observacion.split("/")
                        if (obs != null) {
                            editTextMedidor.setText(obs[0])
                            editTextLectura2.setText(obs[1])
                        }
                    } else {
                        layoutMedidor.visibility = View.GONE
                        layoutLectura.visibility = View.GONE
                        layoutNroLectura.visibility = View.VISIBLE
                        layoutObservacion.visibility = View.VISIBLE
                        editTextObservacion.setText(d.registro_Observacion)
                    }

                    editTextLectura.setText(d.registro_Lectura)
                }
            })
    }

    private fun getComboObservacion() {
        suministroViewModel.getDetalleGrupoByMotivoTask(estado,"1")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<DetalleGrupo> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
                override fun onNext(d: DetalleGrupo) {
                    pidePhoto = d.pideFoto
                    pideLectura = d.pideLectura
                    r.grupo_Incidencia_Codigo = d.iD_DetalleGrupo.toString()
                    r.artefactoNombre = d.descripcion
                    editTextArtefacto.setText(d.descripcion)
                }
            })
    }

    private fun formRegistro() {
        val gps = Gps(this)
        if (gps.isLocationEnabled()) {
            if (gps.getLatitude().toString() == "0.0" || gps.getLongitude().toString() == "0.0") {
                gps.showAlert(this)
            } else {
                Util.hideKeyboard(this)
                r.registro_Latitud = gps.getLatitude().toString()
                r.registro_Longitud = gps.getLongitude().toString()
                r.registro_Lectura = editTextLectura.text.toString()
                r.registro_Confirmar_Lectura = editTextLectura.text.toString()

                r.registro_Observacion = if (grupoId == 5) {
                    editTextMedidor.text.toString() + "/" + editTextLectura2.text.toString()
                } else {
                    editTextObservacion.text.toString()
                }

                if (pideLectura == "SI") {
                    if (editTextLectura.text.toString().isEmpty()) {
                        Util.toastMensaje(this, "Digite Lectura..")
                        return
                    }
                }

                if (estado == 3 || estado == 4) {
                    if (r.registro_Desplaza.isEmpty()) {
                        Util.toastMensaje(this, "Eliga una Ubicación de Medidor")
                        return
                    }
                }

                if (r.parentId == 0) {
                    suministroViewModel.setError("Eliga un resultado")
                    return
                }

//                if (r.codigo_Resultado.isEmpty()) {
//                    suministroViewModel.setError("Eliga una causa")
//                    return
//                }

                if (pideLectura == "SI") {
                    if (editTextLectura.text.toString().isEmpty()) {
                        editTextLectura.error = "Ingrese Lectura"
                        editTextLectura.requestFocus()
                        return
                    }
                }

                r.registro_Fecha_SQLITE = Util.getFechaActual()
                r.fecha_Sincronizacion_Android = r.registro_Fecha_SQLITE
                r.registro_TieneFoto = "1"
                r.estado = 2
                r.tipo = if (recuperada == 10) 10 else estado
                suministroViewModel.insertRegistro(r)
            }
        } else {
            gps.showSettingsAlert(this)
        }
    }

    private fun goToPhoto() {
        val intent = Intent(this, PhotoActivity::class.java)
        intent.putExtra("envioId", r.iD_Suministro)
        intent.putExtra("orden", orden)
        intent.putExtra("orden_2", ordenOperario)
        intent.putExtra("tipo", if (estado == 10) 10 else estado)
        intent.putExtra("estado", estado)
        intent.putExtra("nombre", titulo)
        intent.putExtra("suministro", contrato.trim())
        intent.putExtra("fechaAsignacion", fechaAsignacion.trim())
        intent.putExtra("direccion", direccion)
        intent.putExtra("ubicacionId", ubicacionId)
        startActivity(intent)
        finish()
    }

    private fun dialogSpinner(tipo: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(this).inflate(R.layout.dialog_combo, null)
        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        when (tipo) {
            1 -> {
                textViewTitulo.text = String.format("%s", "Motivo")
                val motivoAdapter = MotivoAdapter(object : OnItemClickListener.MotivoListener {
                    override fun onItemClick(m: Motivo, view: View, position: Int) {
                        r.motivoId = m.codigo
                        r.motivoNombre = m.descripcion
                        editTextMotivo.setText(m.descripcion)
                        dialog.dismiss()
                    }
                })
                recyclerView.adapter = motivoAdapter
                suministroViewModel.getMotivos().observe(this) {
                    motivoAdapter.addItems(it)
                }
            }
            2 -> {
                textViewTitulo.text = String.format("%s", "Codigo de Observación")
                val detalleGrupoAdapter =
                    DetalleGrupoAdapter(object : OnItemClickListener.DetalleGrupoListener {
                        override fun onItemClick(d: DetalleGrupo, view: View, position: Int) {
                            pidePhoto = d.pideFoto
                            pideLectura = d.pideLectura
                            r.grupo_Incidencia_Codigo = d.iD_DetalleGrupo.toString()
                            r.grupoIndicenciaCodigoNombre = d.descripcion
                            editTextDialogObservacion.setText(d.descripcion)
                            grupoId = d.iD_DetalleGrupo
                            if (estado != 3 || estado != 4) {
                                if (d.iD_DetalleGrupo == 5) {
                                    layoutMedidor.visibility = View.VISIBLE
                                    layoutLectura.visibility = View.VISIBLE
                                    layoutNroLectura.visibility = View.GONE
//                                layoutObservacion.visibility = View.GONE
                                    editTextLectura.text = null
                                } else {
                                    layoutMedidor.visibility = View.GONE
                                    layoutLectura.visibility = View.GONE
                                    layoutNroLectura.visibility = View.VISIBLE
                                    layoutObservacion.visibility = View.VISIBLE
                                }
                            }
                            if (pideLectura == "NO") {
                                editTextLectura.text = null
                                editTextLectura.isEnabled = false
                            } else {
                                editTextLectura.isEnabled = true
                            }

                            if (d.ubicaMedidor != 0) {
                                r.registro_Desplaza = d.ubicaMedidor.toString()
                                when (d.ubicaMedidor) {
                                    1 -> editTextUbicacion.setText(String.format("%s", "Externo"))
                                    2 -> editTextUbicacion.setText(String.format("%s", "Interno"))
                                    3 -> editTextUbicacion.setText(String.format("%s", "Sotano"))
                                    4 -> editTextUbicacion.setText(String.format("%s", "Azotea"))
                                }
                            }

                            dialog.dismiss()
                        }
                    })
                recyclerView.adapter = detalleGrupoAdapter
                val lecturaEstado =
                    if (estado == 1 || estado == 7 || estado == 6 || estado == 9 || estado == 10) 1 else estado

                suministroViewModel.getDetalleGrupoByLectura(lecturaEstado).observe(this) {
                    detalleGrupoAdapter.addItems(it)
                }
            }
            3 -> {
                if (estado == 3) {
                    textViewTitulo.text = String.format("%s", "Motivo")
                } else {
                    textViewTitulo.text = String.format("%s", "Artefacto")
                }

                val detalleGrupoAdapter =
                    DetalleGrupoAdapter(object : OnItemClickListener.DetalleGrupoListener {
                        override fun onItemClick(d: DetalleGrupo, view: View, position: Int) {
                            pidePhoto = d.pideFoto
                            pideLectura = d.pideLectura
                            r.grupo_Incidencia_Codigo = d.iD_DetalleGrupo.toString()
                            r.artefactoNombre = d.descripcion
                            editTextArtefacto.setText(d.descripcion)
                            if (d.iD_DetalleGrupo == 47) {
                                suministroViewModel.getDetalleGrupoById(55)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(object : Observer<DetalleGrupo> {
                                        override fun onSubscribe(d: Disposable) {}
                                        override fun onError(e: Throwable) {}
                                        override fun onComplete() {}
                                        override fun onNext(t: DetalleGrupo) {
                                            r.parentId = t.iD_DetalleGrupo
                                            r.parentIdNombre = t.descripcion
                                            editTextResultado.setText(t.descripcion)
                                            getDetalleTask(89)
                                        }
                                    })
                            }

                            if (d.iD_DetalleGrupo == 50) {
                                suministroViewModel.getDetalleGrupoById(55)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(object : Observer<DetalleGrupo> {
                                        override fun onSubscribe(d: Disposable) {}
                                        override fun onError(e: Throwable) {}
                                        override fun onComplete() {}
                                        override fun onNext(t: DetalleGrupo) {
                                            r.parentId = t.iD_DetalleGrupo
                                            r.parentIdNombre = t.descripcion
                                            editTextResultado.setText(t.descripcion)
                                            editTextCausa.text = null

                                        }
                                    })
                            }

                            dialog.dismiss()
                        }
                    })
                recyclerView.adapter = detalleGrupoAdapter


                suministroViewModel.getDetalleGrupoByMotivo(estado, "1").observe(this) {
                    detalleGrupoAdapter.addItems(it)
                }
            }
            4 -> {
                textViewTitulo.text = String.format("%s", "Resultado")
                val detalleGrupoAdapter =
                    DetalleGrupoAdapter(object : OnItemClickListener.DetalleGrupoListener {
                        override fun onItemClick(d: DetalleGrupo, view: View, position: Int) {
                            r.parentId = d.iD_DetalleGrupo
                            r.parentIdNombre = d.descripcion
                            editTextResultado.setText(d.descripcion)
                            when (r.grupo_Incidencia_Codigo) {
                                "47" -> if (d.iD_DetalleGrupo == 55) {
                                    getDetalleTask(89)
                                }
                                "50" -> if (d.iD_DetalleGrupo == 55) {
                                    getDetalleTask(90)
                                }
                            }

                            if (d.iD_DetalleGrupo != 55) {
                                pidePhoto = ""
                                pideLectura = ""
                                r.codigo_Resultado = ""
                                editTextCausa.text = null
                                editTextCausa.isEnabled = true

                                if (d.iD_DetalleGrupo == 92) {
                                    editTextArtefacto.setText("")
                                    editTextArtefacto.isEnabled = false
                                }
                                if (d.iD_DetalleGrupo == 93) {
                                    editTextLectura.text = null
                                    editTextLectura.isEnabled = true
                                    r.grupo_Incidencia_Codigo = ""
                                    editTextArtefacto.setText("")
                                    editTextArtefacto.isEnabled = true
                                }
                            }
                            dialog.dismiss()
                        }
                    })
                recyclerView.adapter = detalleGrupoAdapter

                suministroViewModel.getDetalleGrupoByParentId(if (estado == 3) 1 else 2)
                    .observe(this) {
                        detalleGrupoAdapter.addItems(it)
                    }
            }
            5 -> {
                textViewTitulo.text = String.format("%s", "Ubicación del Medidor")
                val menuAdapter = MenuItemAdapter(object : OnItemClickListener.MenuListener {
                    override fun onItemClick(m: MenuPrincipal, v: View, position: Int) {
                        r.registro_Desplaza = m.menuId.toString()
                        editTextUbicacion.setText(m.title)
                        dialog.dismiss()
                    }
                })
                recyclerView.itemAnimator = DefaultItemAnimator()
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = menuAdapter

                val menus: ArrayList<MenuPrincipal> = ArrayList()
                menus.add(MenuPrincipal(1, "Externo", 0, 0))
                menus.add(MenuPrincipal(2, "Interno", 0, 0))
                if (estado != 3 && estado != 4) {
                    menus.add(MenuPrincipal(3, "Sotano", 0, 0))
                    menus.add(MenuPrincipal(4, "Azotea", 0, 0))
                }
                menuAdapter.addItems(menus)
            }
            6 -> {
                textViewTitulo.text = String.format("%s", "Causa")
                val detalleGrupoAdapter =
                    DetalleGrupoAdapter(object : OnItemClickListener.DetalleGrupoListener {
                        override fun onItemClick(d: DetalleGrupo, view: View, position: Int) {
                            pidePhoto = d.pideFoto
                            pideLectura = d.pideLectura
                            r.codigo_Resultado = d.iD_DetalleGrupo.toString()
                            r.causaNombre =d.descripcion
                            editTextCausa.setText(d.descripcion)

                            if (pideLectura == "NO") {
                                editTextLectura.text = null
                                editTextLectura.isEnabled = false
                            } else {
                                editTextLectura.isEnabled = true
                            }
                            dialog.dismiss()
                        }
                    })
                recyclerView.adapter = detalleGrupoAdapter

                suministroViewModel.getDetalleGrupoByParentId(r.parentId).observe(this) {
                    detalleGrupoAdapter.addItems(it)
                }
            }
        }
    }
}