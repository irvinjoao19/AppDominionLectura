package com.dsige.lectura.dominion.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dsige.lectura.dominion.R
import com.dsige.lectura.dominion.data.local.model.Usuario
import com.dsige.lectura.dominion.helper.Util
import com.dsige.lectura.dominion.ui.fragments.MainFragment
import com.dsige.lectura.dominion.data.viewModel.UsuarioViewModel
import com.dsige.lectura.dominion.data.viewModel.ViewModelFactory
import com.dsige.lectura.dominion.helper.Permission
import com.dsige.lectura.dominion.ui.fragments.RecuperacionFragment
import com.dsige.lectura.dominion.ui.fragments.SendFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings


import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.io.File
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var usuarioViewModel: UsuarioViewModel
    private lateinit var remoteConfig: FirebaseRemoteConfig


    lateinit var builder: AlertDialog.Builder
    private var dialog: AlertDialog? = null
    private var usuarioId: Int = 0
    private var logout: String = "off"
    private var link: String = ""
    private var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindUI()
    }

    private fun bindUI() {
        usuarioViewModel =
            ViewModelProvider(this, viewModelFactory).get(UsuarioViewModel::class.java)
        usuarioViewModel.user.observe(this, { u ->
            if (u != null) {
                getUser(u)
                setSupportActionBar(toolbar)
                val toggle = ActionBarDrawerToggle(
                    this@MainActivity,
                    drawerLayout,
                    toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close
                )
                drawerLayout.addDrawerListener(toggle)
                toggle.syncState()
                navigationView.setNavigationItemSelectedListener(this@MainActivity)
                fragmentByDefault(MainFragment.newInstance(usuarioId))
                message()
                remoteConfig()
            } else {
                goLogin()
            }
        })
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.title) {
            "Sincronizar" -> dialogFunction(
                1,
                "Al sincronizar eliminando todo tus avances. Deseas Sincronizar ?"
            )
            "Inicio de Actividades" -> changeFragment(
                MainFragment.newInstance(usuarioId), item.title.toString()
            )
            "Envio de Pendientes" -> changeFragment(
                SendFragment.newInstance("", ""), item.title.toString()
            )
            "Servicio Gps" -> {
                Util.executeGpsWork(this)
                Util.executeBatteryWork(this)
            }
            "Recuperación de Fotos" -> changeFragment(
                RecuperacionFragment.newInstance("", ""), item.title.toString()
            )
            "Cerrar Sesión" -> dialogFunction(
                3,
                "Al cerrar Sesión estaras eliminando todo tus avances. \nDeseas Salir ?"
            )
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun load(title: String) {
        builder = AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this@MainActivity).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
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

    private fun changeFragment(fragment: Fragment, title: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
        supportActionBar!!.title = title
    }

    private fun fragmentByDefault(f: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, f)
            .commit()
        supportActionBar!!.title = "Inicio de Actividades"
    }

    private fun getUser(u: Usuario) {
        val header = navigationView.getHeaderView(0)
        header.textViewName.text = u.operario_Nombre
        header.textViewEmail.text = String.format("Versión: %s", Util.getVersion(this))
        usuarioId = u.iD_Operario
    }

    private fun goLogin() {
        if (logout == "off") {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun message() {
        usuarioViewModel.mensajeSuccess.observe(this, { s ->
            if (s != null) {
                closeLoad()
                if (s == "Close") {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Util.toastMensaje(this, s)
                }

                if (s == "Sincronización Completa") {
                    changeFragment(
                        MainFragment.newInstance(usuarioId), "Inicio de Actividades"
                    )
                }
            }
        })
        usuarioViewModel.mensajeError.observe(this@MainActivity, { s ->
            if (s != null) {
                closeLoad()
                Util.snackBarMensaje(window.decorView, s)
            }
        })
    }

    private fun dialogFunction(tipo: Int, title: String) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage(title)
            .setPositiveButton("SI") { dialog, _ ->
                when (tipo) {
                    1 -> {
                        load("Sincronizando..")
                        usuarioViewModel.sync(usuarioId, Util.getVersion(this))
                    }
                    3 -> {
                        logout = "on"
                        Util.closeGpsWork(this)
                        Util.closeBatteryWork(this)
                        load("Cerrando Session")
                        usuarioViewModel.logout()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.cancel()
            }
        dialog.show()
    }

    private fun remoteConfig() {

        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
//        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetch()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    remoteConfig.activate()
                    val isUpdate = remoteConfig.getBoolean(Util.KEY_UPDATE_ENABLE)
                    if (isUpdate) {
                        val version = remoteConfig.getString(Util.KEY_UPDATE_VERSION)
                        val appVersion = Util.getVersion(this)
                        val url = remoteConfig.getString(Util.KEY_UPDATE_URL)
                        val name = remoteConfig.getString(Util.KEY_UPDATE_NAME)

                        if (version != appVersion) {
                            updateAndroid(url, name, version)
                        }
                    }
                }
            }
    }

    private fun updateAndroid(url: String, nombre: String, title: String) {
        val builderUpdate =
            AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this@MainActivity).inflate(R.layout.dialog_new_version, null)
        val textViewTile = view.findViewById<TextView>(R.id.textViewTitle)
        val buttonUpdate = view.findViewById<MaterialButton>(R.id.buttonUpdate)
        builderUpdate.setView(view)
        val dialogUpdate = builderUpdate.create()
        dialogUpdate.setCanceledOnTouchOutside(false)
        dialogUpdate.setCancelable(false)
        dialogUpdate.show()
        textViewTile.text = String.format("%s %s", "Nueva Versión", title)
        buttonUpdate.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                download(url.trim { it <= ' ' }, nombre)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Permission.WRITE_REQUEST
                )
            }
            dialogUpdate.dismiss()
        }
        link = url.trim { it <= ' ' }
        name = nombre
    }

    private fun download(url: String, name: String) {
        val file = File(Environment.DIRECTORY_DOWNLOADS, name)
        if (file.exists()) {
            file.delete()
        }
        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        Log.i("TAG", url)
        val mUri = Uri.parse(url)
        val r = DownloadManager.Request(mUri)
        r.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        //r.setAllowedOverRoaming(false);
        //r.setVisibleInDownloadsUi(false)
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)
        r.setTitle(name)
        r.setMimeType("application/vnd.android.package-archive")
//        if (Build.VERSION.SDK_INT <= 25) {
//            val downloadId = dm.enqueue(r)
//            val onComplete = object : BroadcastReceiver() {
//                override fun onReceive(ctxt: Context, intent: Intent) {
//                    val uri = Uri.fromFile(File(Environment.getExternalStorageDirectory() , name))
//                    val install = Intent(Intent.ACTION_VIEW)
//                    install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    install.setDataAndType(uri, dm.getMimeTypeForDownloadedFile(downloadId))
//                    startActivity(install)
//                    unregisterReceiver(this)
//                    finish()
//                }
//            }
//            registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
//            Util.toastMensaje(this, getString(R.string.wait_download))
//        } else {
        dm.enqueue(r)
        usuarioViewModel.setError(getString(R.string.wait_download))

//        }
    }
}