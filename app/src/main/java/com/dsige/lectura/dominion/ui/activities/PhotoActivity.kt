package com.dsige.lectura.dominion.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.dsige.lectura.dominion.BuildConfig
import com.dsige.lectura.dominion.R
import com.dsige.lectura.dominion.data.local.model.Photo
import com.dsige.lectura.dominion.data.local.model.SuministroLectura
import com.dsige.lectura.dominion.data.viewModel.SuministroViewModel
import com.dsige.lectura.dominion.data.viewModel.ViewModelFactory
import com.dsige.lectura.dominion.helper.Gps
import com.dsige.lectura.dominion.helper.Permission
import com.dsige.lectura.dominion.helper.Util
import com.dsige.lectura.dominion.ui.adapters.PhotoAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_photo.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PhotoActivity : DaggerAppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonGrabar -> formValidate()
            R.id.buttonPhoto -> formPhoto()
        }
    }

    private fun formPhoto() {
        when {
            tipo <= 2 || tipo == 9 || tipo == 10 -> {
                if (cantidad < 1) {
                    goCamera()
                } else {
                    Util.dialogMensaje(this, "Mensaje", "Maximo 1 foto")
                }
            }
            tipo == 3 -> {
                if (cantidad < 2) {
                    goCamera()
                } else {
                    Util.dialogMensaje(this, "Mensaje", "Maximo 2 fotos")
                }
            }
            tipo == 4 -> {
                if (cantidad < 3) {
                    goCamera()
                } else {
                    Util.dialogMensaje(this, "Mensaje", "Maximo 3 fotos")
                }
            }
        }
    }

    private fun formValidate() {
        when {
            tipo <= 2 || tipo == 9 || tipo == 10 -> {
                if (cantidad == 1) {
                    updateData(receive, tipo)
                } else {
                    Util.dialogMensaje(this, "Mensaje", "Se requiere 1 foto")
                }
            }
            tipo == 3 -> {
                if (cantidad == 2) {
                    if (online == 1) {
                        confirmSend()
                    } else {
                        updateData(receive, tipo)
                    }
                } else {
                    Util.dialogMensaje(this, "Mensaje", "Se requiere 2 fotos")
                }
            }
            tipo == 4 -> {
                if (cantidad == 3) {
                    updateData(receive, tipo)
                } else {
                    Util.dialogMensaje(this, "Mensaje", "Se requiere 3 fotos")
                }
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var suministroViewModel: SuministroViewModel
    lateinit var builder: AlertDialog.Builder
    private var dialog: AlertDialog? = null

    private var receive: Int = 0
    private var tipo: Int = 0
    private var cantidad: Int = 0
    private var estado: Int = 0
    private var parentId: Int = 0

    private var registro_Desplaza: String = ""
    private var orden: Int = 0
    private var titulo: String = ""
    private var orden_2: Int = 0
    private var online: Int = 0
    private var suministro: String = ""
    private var fechaAsignacion: String = ""
    private var direccion: String = ""
    private var nameImg: String = ""
    private var ubicacionId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        val bundle = intent.extras
        if (bundle != null) {
            receive = bundle.getInt("envioId")
            titulo = bundle.getString("nombre")!!
            orden = bundle.getInt("orden")
            orden_2 = bundle.getInt("orden_2")
            tipo = bundle.getInt("tipo")
            estado = bundle.getInt("estado")
            suministro = bundle.getString("suministro", "")
            fechaAsignacion = bundle.getString("fechaAsignacion", "")
            direccion = bundle.getString("direccion", "")
            ubicacionId = bundle.getInt("ubicacionId")
            bindUI(receive, tipo)
        }
    }

    private fun bindUI(id: Int, tipo: Int) {
        suministroViewModel =
            ViewModelProvider(this, viewModelFactory).get(SuministroViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Ingresar Foto"
        if (tipo != 3) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener {
                when (tipo) {
                    3 -> goBackActivity(FormCorteActivity::class.java)
                    4 -> goBackActivity(FormReconexionActivity::class.java)
                    else -> goBackActivity(FormLecturaActivity::class.java)
                }
                finish()
            }
        }

        suministroViewModel.user.observe(this) {
            online = it.operario_EnvioEn_Linea
        }

        buttonGrabar.setOnClickListener(this)
        buttonPhoto.setOnClickListener(this)

        if (tipo == 4) {
            buttonGrabar.text = String.format("Siguiente")
        }

        suministroViewModel.getRegistroBySuministro(id).observe(this) {
            if (it != null){
                parentId = it.parentId
            }
        }

        val layoutManager = GridLayoutManager(this, 2)
        val photoAdapter = PhotoAdapter(object : PhotoAdapter.OnItemClickListener {
            override fun onItemClick(photo: Photo, view: View, position: Int) {
                showPopupMenu(photo, view)
            }
        })
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = photoAdapter


        suministroViewModel.getPhotoAllBySuministro(id, tipo, 0).observe(this) {
            if (it.isEmpty()) {
                goCamera()
            }

            cantidad = it.size
            photoAdapter.addItems(it)
        }

        suministroViewModel.mensajeError.observe(this) {
            closeLoad()
            Util.toastMensaje(this, it)
        }
        suministroViewModel.mensajeSuccess.observe(this) {
            closeLoad()
            if (it == "firma") {
                val intent = Intent(this@PhotoActivity, ReconexionFirmActivity::class.java)
                intent.putExtra("envioId", receive)
                intent.putExtra("tipo", tipo)
                intent.putExtra("online", online)
                intent.putExtra("orden", orden)
                intent.putExtra("orden_2", orden_2)
                intent.putExtra("suministro", suministro)
                intent.putExtra("estado", estado)
                intent.putExtra("nombre", titulo)
                intent.putExtra("fechaAsignacion", fechaAsignacion)
                intent.putExtra("direccion", direccion)
                startActivity(intent)
                finish()
            } else {
                siguienteOrden(tipo, estado)
            }
            Util.toastMensaje(this, it)
        }
    }

    private fun goBackActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        intent.putExtra("orden", orden)
        intent.putExtra("nombre", titulo)
        intent.putExtra("estado", estado)
        intent.putExtra("ubicacionId", ubicacionId)
        startActivity(intent)
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (tipo != 3) {
                when (tipo) {
                    4 -> goBackActivity(FormReconexionActivity::class.java)
                    else -> goBackActivity(FormLecturaActivity::class.java)
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    private fun showPopupMenu(p: Photo, v: View) {
        val popupMenu = PopupMenu(this, v)
        popupMenu.menu.add(0, Menu.FIRST, 0, getText(R.string.ver))
        popupMenu.menu.add(1, Menu.FIRST + 1, 1, getText(R.string.deletePhoto))
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
//                    val intent = Intent(this@PhotoActivity, ::class.java)
//                    intent.putExtra("nombre", p.rutaFoto)
//                    intent.putExtra("envioId", p.iD_Foto)
//                    startActivity(intent)
                }
                2 -> confirmDeletePhoto(p)

            }
            false
        }
        popupMenu.show()
    }

    private fun load() {
        builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        builder.setView(view)
        textViewTitle.text = String.format("Enviando...")
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

    private fun confirmDeletePhoto(p: Photo) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage("Estas seguro de Eliminar esta foto ?")
            .setPositiveButton("SI") { dialog, _ ->
                suministroViewModel.deletePhoto(p, this)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.cancel()
            }
        dialog.show()
    }

    private fun goCamera() {
        nameImg = Util.getFechaSuministro(
            suministro.toInt(),
            if (tipo == 2 || tipo == 9) 10 else tipo,
            fechaAsignacion
        )
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    Util.createImageFile(nameImg,this)
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val uriSavedImage = Uri.fromFile(it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage)

                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                            m.invoke(null)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    startActivityForResult(takePictureIntent, Permission.CAMERA_REQUEST)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Permission.CAMERA_REQUEST && resultCode == RESULT_OK) {
            val gps = Gps(this)
            if (gps.isLocationEnabled()) {
                suministroViewModel.generarArchivo(
                    nameImg,
                    this@PhotoActivity,
                    fechaAsignacion,
                    direccion,
                    gps.getLatitude().toString(),
                    gps.getLongitude().toString(),
                    receive,
                    tipo
                )
            } else {
                gps.showSettingsAlert(this)
            }
        }
    }

    private fun updateData(receive: Int, tipo: Int) {

        when {
            tipo <= 2 || tipo == 9 || tipo == 10 -> {
                suministroViewModel.updateRegistro(receive, tipo, 1)
            }
            tipo == 4 -> {
                if (parentId == 92) {
                    if (online == 1) {
                        load()
                        suministroViewModel.sendFiles(receive, this)
                    } else {
                        suministroViewModel.updateRegistro(receive, tipo, 1)
                    }
                } else {
                    suministroViewModel.updateRegistro(receive, tipo, 2)
                }
            }
            tipo == 3 -> {
                if (online == 1) {
                    load()
                    suministroViewModel.verificateCorte(suministro, receive, this)
                } else {
                    suministroViewModel.updateRegistro(receive, tipo, 1)

                }
            }
        }
    }

    private fun siguienteOrden(tipo: Int, estado: Int) {
        val nombre = when (estado) {
            1 -> "Lectura Normales"
            2 -> "Relectura"
            3 -> "Cortes"
            4 -> "Reconexión"
            6 -> "Lectura Observadas"
            7 -> "Lectura Manuales"
            10 -> "Lectura Recuperadas"
            9 -> "Reclamos"
            else -> {
                ""
            }
        }

        suministroViewModel.getSuministroRight(estado, orden, orden_2)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {
                    val intent = Intent(this@PhotoActivity, SuministroActivity::class.java)
                    intent.putExtra("nombre", nombre)
                    intent.putExtra("estado", estado)
                    startActivity(intent)
                    finish()
                }

                override fun onNext(t: Int) {
                    when (tipo) {
                        3 -> goListaSuministro("Corte", tipo)
                        4 -> goListaSuministro("Reconexión", tipo)
                        else -> {
                            val intent = Intent(this@PhotoActivity, FormLecturaActivity::class.java)
                            intent.putExtra("orden", t)
                            intent.putExtra("nombre", nombre)
                            intent.putExtra("estado", estado)
                            intent.putExtra("ubicacionId", ubicacionId)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            })
    }

    private fun goListaSuministro(title: String, tipo: Int) {
        val intent = Intent(this@PhotoActivity, SuministroActivity::class.java)
        intent.putExtra("nombre", title)
        intent.putExtra("estado", tipo)
        startActivity(intent)
        finish()
    }

    private fun confirmSend() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage("Estas seguro de enviar ?")
            .setPositiveButton("SI") { dialog, _ ->
                registro_Desplaza = "1"
                updateData(receive, tipo)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.cancel()
            }
        dialog.show()
    }
}