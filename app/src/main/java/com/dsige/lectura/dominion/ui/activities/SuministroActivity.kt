package com.dsige.lectura.dominion.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsige.lectura.dominion.R
import com.dsige.lectura.dominion.data.local.model.GrandesClientes
import com.dsige.lectura.dominion.data.local.model.SuministroCortes
import com.dsige.lectura.dominion.data.local.model.SuministroLectura
import com.dsige.lectura.dominion.data.local.model.SuministroReconexion
import com.dsige.lectura.dominion.data.viewModel.SuministroViewModel
import com.dsige.lectura.dominion.data.viewModel.ViewModelFactory
import com.dsige.lectura.dominion.helper.Util
import com.dsige.lectura.dominion.ui.adapters.ClientesAdapter
import com.dsige.lectura.dominion.ui.adapters.SuministroCortesAdapter
import com.dsige.lectura.dominion.ui.adapters.SuministroLecturaAdapter
import com.dsige.lectura.dominion.ui.adapters.SuministroReconexionAdapter
import com.dsige.lectura.dominion.ui.listeners.OnItemClickListener
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_suministro.*
import javax.inject.Inject

class SuministroActivity : DaggerAppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabMap -> startActivity(
                Intent(
                    this, PendingLocationMapsActivity::class.java
                ).putExtra("estado", estado)
            )
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var suministroViewModel: SuministroViewModel

    private lateinit var lecturaAdapter: SuministroLecturaAdapter
    private lateinit var corteAdapter: SuministroCortesAdapter
    private lateinit var reconexionAdapter: SuministroReconexionAdapter
    private lateinit var clienteAdapter: ClientesAdapter

    private var estado: Int = 0
    private var nombre: String = ""
    private var ubicacionId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suministro)
        val b = intent.extras
        if (b != null) {
            estado = b.getInt("estado")
            nombre = b.getString("nombre", "")
            ubicacionId = b.getInt("ubicacionId")
            bindUI()
        }
    }

    private fun bindUI() {
        suministroViewModel =
            ViewModelProvider(this, viewModelFactory).get(SuministroViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = nombre
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager

        when {
            estado <= 2 -> {
                if (estado == 2) {
                    fabMap.visibility = View.VISIBLE
                }
                lecturaAdapter =
                    SuministroLecturaAdapter(object : OnItemClickListener.LecturaListener {
                        override fun onItemClick(s: SuministroLectura, v: View, position: Int) {
                            when (v.id) {
                                R.id.imageViewMap -> {
                                    if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                        startActivity(
                                            Intent(
                                                this@SuministroActivity,
                                                MapsActivity::class.java
                                            )
                                                .putExtra("latitud", s.latitud)
                                                .putExtra("longitud", s.longitud)
                                                .putExtra("title", s.suministro_Numero)
                                        )
                                    } else
                                        suministroViewModel.setError("Este suministro no cuenta con coordenadas")
                                }
                                else -> {
                                    val nombre = if (estado == 1) "Lectura" else "Relectura"
                                    val intent = Intent(
                                        this@SuministroActivity,
                                        FormLecturaActivity::class.java
                                    )
                                    intent.putExtra("orden", s.orden)
                                    intent.putExtra("nombre", nombre)
                                    intent.putExtra("estado", s.estado)
                                    intent.putExtra("ubicacionId", ubicacionId)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    })
                recyclerView.adapter = lecturaAdapter

                suministroViewModel.getSuministroLectura(estado, 1, 0).observe(this) {
                    lecturaAdapter.addItems(it)
                }
            }
            estado == 3 -> {
                fabMap.visibility = View.VISIBLE
                corteAdapter =
                    SuministroCortesAdapter(object : OnItemClickListener.CorteListener {
                        override fun onItemClick(s: SuministroCortes, v: View, position: Int) {
                            when (v.id) {
                                R.id.imageViewMap -> {
                                    if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                        startActivity(
                                            Intent(
                                                this@SuministroActivity,
                                                MapsActivity::class.java
                                            )
                                                .putExtra("latitud", s.latitud)
                                                .putExtra("longitud", s.longitud)
                                                .putExtra("title", s.suministro_Numero)
                                        )
                                    } else
                                        suministroViewModel.setError("Este suministro no cuenta con coordenadas")
                                }
                                else -> {
                                    if (s.suministro_NoCortar == 1) {
                                        suministroViewModel.setError("Corte Cancelado")
                                    } else {
                                        val intent = Intent(
                                            this@SuministroActivity, FormCorteActivity::class.java
                                        )
                                        intent.putExtra("orden", s.orden)
                                        intent.putExtra("nombre", "Corte")
                                        intent.putExtra("estado", s.estado)
                                        intent.putExtra("ubicacionId", ubicacionId)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }
                        }
                    })
                recyclerView.adapter = corteAdapter

                suministroViewModel.getSuministroCortes(estado, 1).observe(this) {
                    corteAdapter.addItems(it)
                }
            }
            estado == 4 -> {
                fabMap.visibility = View.VISIBLE
                reconexionAdapter =
                    SuministroReconexionAdapter(object : OnItemClickListener.ReconexionListener {
                        override fun onItemClick(s: SuministroReconexion, v: View, position: Int) {
                            when (v.id) {
                                R.id.imageViewMap -> {
                                    if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                        startActivity(
                                            Intent(
                                                this@SuministroActivity,
                                                MapsActivity::class.java
                                            )
                                                .putExtra("latitud", s.latitud)
                                                .putExtra("longitud", s.longitud)
                                                .putExtra("title", s.suministro_Numero)
                                        )
                                    } else {
                                        Util.toastMensaje(
                                            this@SuministroActivity,
                                            "Este suministro no cuenta con coordenadas"
                                        )
                                    }
                                }
                                else -> {
                                    val intent = Intent(
                                        this@SuministroActivity,
                                        FormReconexionActivity::class.java
                                    )
                                    intent.putExtra("orden", s.orden)
                                    intent.putExtra("nombre", "Reconexion")
                                    intent.putExtra("estado", s.estado)
                                    intent.putExtra("ubicacionId", ubicacionId)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    })
                recyclerView.adapter = reconexionAdapter
                suministroViewModel.getSuministroReconexion(estado, 1).observe(this) {
                    reconexionAdapter.addItems(it)
                }
            }
            estado == 7 -> {
                clienteAdapter = ClientesAdapter(object : OnItemClickListener.ClientesListener {
                    override fun onItemClick(c: GrandesClientes, v: View, position: Int) {
                        startActivity(
                            Intent(this@SuministroActivity, BigClientsActivity::class.java)
                                .putExtra("clienteId", c.clienteId)
                        )
                    }
                })
                recyclerView.adapter = clienteAdapter
                suministroViewModel.getGrandesClientes().observe(this) {
                    clienteAdapter.addItems(it)
                }
            }
            estado == 9 -> {
                fabMap.visibility = View.VISIBLE
                lecturaAdapter =
                    SuministroLecturaAdapter(object : OnItemClickListener.LecturaListener {
                        override fun onItemClick(s: SuministroLectura, v: View, position: Int) {
                            when (v.id) {
                                R.id.imageViewMap -> {
                                    if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                        startActivity(
                                            Intent(
                                                this@SuministroActivity,
                                                MapsActivity::class.java
                                            )
                                                .putExtra("latitud", s.latitud)
                                                .putExtra("longitud", s.longitud)
                                                .putExtra("title", s.suministro_Numero)
                                        )
                                    } else {
                                        Util.toastMensaje(
                                            this@SuministroActivity,
                                            "Este suministro no cuenta con coordenadas"
                                        )
                                    }
                                }
                                else -> {
                                    val nombre = "Reclamos"
                                    val intent = Intent(
                                        this@SuministroActivity,
                                        FormLecturaActivity::class.java
                                    )
                                    intent.putExtra("orden", s.orden)
                                    intent.putExtra("nombre", nombre)
                                    intent.putExtra("estado", estado)
                                    intent.putExtra("ubicacionId", ubicacionId)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    })
                recyclerView.adapter = lecturaAdapter
                suministroViewModel.getSuministroReclamos(estado.toString(), 1).observe(this) {
                    lecturaAdapter.addItems(it)
                }
            }
            estado == 10 -> {
                lecturaAdapter =
                    SuministroLecturaAdapter(object : OnItemClickListener.LecturaListener {
                        override fun onItemClick(s: SuministroLectura, v: View, position: Int) {
                            when (v.id) {
                                R.id.imageViewMap -> {
                                    if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                        startActivity(
                                            Intent(
                                                this@SuministroActivity,
                                                MapsActivity::class.java
                                            )
                                                .putExtra("latitud", s.latitud)
                                                .putExtra("longitud", s.longitud)
                                                .putExtra("title", s.suministro_Numero)
                                        )
                                    } else {
                                        Util.toastMensaje(
                                            this@SuministroActivity,
                                            "Este suministro no cuenta con coordenadas"
                                        )
                                    }
                                }
                                else -> {
                                    val nombre = "Lectura Recuperada"
                                    val intent = Intent(
                                        this@SuministroActivity,
                                        FormLecturaActivity::class.java
                                    )
                                    intent.putExtra("orden", s.orden)
                                    intent.putExtra("nombre", nombre)
                                    intent.putExtra("estado", estado)
                                    intent.putExtra("ubicacionId", ubicacionId)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    })
                recyclerView.adapter = lecturaAdapter
                suministroViewModel.getSuministroLectura(estado, 1, 0).observe(this) {
                    lecturaAdapter.addItems(it)
                }
            }
            else -> {
                lecturaAdapter =
                    SuministroLecturaAdapter(object : OnItemClickListener.LecturaListener {
                        override fun onItemClick(s: SuministroLectura, v: View, position: Int) {
                            when (v.id) {
                                R.id.imageViewMap -> {
                                    if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                        startActivity(
                                            Intent(
                                                this@SuministroActivity,
                                                MapsActivity::class.java
                                            )
                                                .putExtra("latitud", s.latitud)
                                                .putExtra("longitud", s.longitud)
                                                .putExtra("title", s.suministro_Numero)
                                        )
                                    } else {
                                        Util.toastMensaje(
                                            this@SuministroActivity,
                                            "Este suministro no cuenta con coordenadas"
                                        )
                                    }
                                }
                                else -> {
                                    val nombre = when (estado) {
                                        6 -> "Lectura Observadas"
                                        7 -> "Lectura Manuales"
                                        8 -> "Tapas abiertas"
                                        9 -> "Reclamos"
                                        else -> ""
                                    }
                                    val intent = Intent(
                                        this@SuministroActivity,
                                        FormLecturaActivity::class.java
                                    )
                                    intent.putExtra("orden", s.orden)
                                    intent.putExtra("nombre", nombre)
                                    intent.putExtra("estado", estado)
                                    intent.putExtra("ubicacionId", ubicacionId)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    })
                recyclerView.adapter = lecturaAdapter
                val suministrosLectura = when (estado) {
                    6 -> suministroViewModel.getSuministroLectura(1, 1, 1)
                    8 -> suministroViewModel.getSuministroLectura(1, 1, 0)
                    9 -> suministroViewModel.getSuministroLectura(1, 1, 0)
                    else -> null
                }
                suministrosLectura?.observe(this) {
                    lecturaAdapter.addItems(it)
                }
            }

        }

        fabMap.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                val searchView = item.actionView as SearchView
                search(searchView)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun search(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                when {
                    estado <= 2 -> lecturaAdapter.getFilter().filter(newText)
                    estado == 9 -> lecturaAdapter.getFilter().filter(newText)
                    estado == 3 -> corteAdapter.getFilter().filter(newText)
                    estado == 4 -> reconexionAdapter.getFilter().filter(newText)
                    estado == 6 || estado == 7 -> lecturaAdapter.getFilter().filter(newText)
                }
                return true
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }
}