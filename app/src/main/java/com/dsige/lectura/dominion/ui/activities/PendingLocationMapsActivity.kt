package com.dsige.lectura.dominion.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dsige.lectura.dominion.R
import com.dsige.lectura.dominion.data.local.model.SuministroCortes
import com.dsige.lectura.dominion.data.local.model.SuministroLectura
import com.dsige.lectura.dominion.data.local.model.SuministroReconexion
import com.dsige.lectura.dominion.data.viewModel.SuministroViewModel
import com.dsige.lectura.dominion.data.viewModel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class PendingLocationMapsActivity : DaggerAppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnMarkerClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var suministroViewModel: SuministroViewModel
    lateinit var mMap: GoogleMap
    lateinit var locationManager: LocationManager

    private var estado: Int = 0
    private var isFirstTime: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_location_maps)
        val b = intent.extras
        if (b != null) {
            estado = b.getInt("estado")
            bindUI()
        }
    }

    private fun bindUI() {
        suministroViewModel =
            ViewModelProvider(this, viewModelFactory).get(SuministroViewModel::class.java)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        if (!this.isGPSEnabled()) {
            showInfoAlert()
        }
    }

    override fun onMapReady(p: GoogleMap) {
        val permisos = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            mMap = p
            zoomToLocation("-12.036175", "-76.999561")
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mMap.isMyLocationEnabled = true
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0f, this)

            when (estado) {
                2 -> {
                    suministroViewModel.getSuministroLectura(estado, 1, 0).observe(this) {
                        mMap.clear()
                        for (s: SuministroLectura in it) {
                            if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(
                                            LatLng(
                                                s.latitud.toDouble(),
                                                s.longitud.toDouble()
                                            )
                                        )
                                        .title(s.iD_Suministro.toString())
                                        .icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_RED
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
                3 -> {
                    suministroViewModel.getSuministroCortes(estado, 1).observe(this) {
                        mMap.clear()
                        for (s: SuministroCortes in it) {
                            if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(
                                            LatLng(
                                                s.latitud.toDouble(),
                                                s.longitud.toDouble()
                                            )
                                        )
                                        .title(s.iD_Suministro.toString())
                                        .icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_RED
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
                4 -> {
                    suministroViewModel.getSuministroReconexion(estado, 1).observe(this) {
                        mMap.clear()
                        for (s: SuministroReconexion in it) {
                            if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(
                                            LatLng(
                                                s.latitud.toDouble(),
                                                s.longitud.toDouble()
                                            )
                                        )
                                        .title(s.iD_Suministro.toString())
                                        .icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_BLUE
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
                9 -> {
                    suministroViewModel.getSuministroReclamos(estado.toString(), 1).observe(this) {
                        mMap.clear()
                        for (s: SuministroLectura in it) {
                            if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(
                                            LatLng(
                                                s.latitud.toDouble(),
                                                s.longitud.toDouble()
                                            )
                                        )
                                        .title(s.iD_Suministro.toString())
                                        .icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_BLUE
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }
            mMap.setOnMarkerClickListener(this)

        } else {
            ActivityCompat.requestPermissions(this, permisos, 1)
        }
    }

    private fun zoomToLocation(latitud: String, longitud: String) {
        val camera = CameraPosition.Builder()
            .target(LatLng(latitud.toDouble(), longitud.toDouble()))
            .zoom(10f)  // limite 21
            //.bearing(165) // 0 - 365°
            .tilt(30f)        // limit 90
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))
    }

    private fun Context.isGPSEnabled() =
        (getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(
            LocationManager.GPS_PROVIDER
        )

    private fun showInfoAlert() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        builder.setTitle("GPS Signal")
        builder.setMessage("Necesitas tener habilitado la señal de GPS. Te gustaria habilitar la señal de GPS ahora ?.")
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    override fun onLocationChanged(p: Location) {
        if (isFirstTime) {
            zoomToLocation(p.latitude.toString(), p.longitude.toString())
            isFirstTime = false
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    override fun onMarkerClick(p: Marker): Boolean {
        dialogResumen(p.title!!)
        return true
    }


    private fun dialogResumen(t: String) {
        val builder =
            android.app.AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(this).inflate(R.layout.cardview_resumen_maps, null)

        val buttonSalir = v.findViewById<Button>(R.id.buttonSalir)
        val textViewTitle = v.findViewById<TextView>(R.id.textViewTitle)
        val textViewMedidor = v.findViewById<TextView>(R.id.textViewMedidor)
        val textViewContrato = v.findViewById<TextView>(R.id.textViewContrato)
        val textViewDireccion = v.findViewById<TextView>(R.id.textViewDireccion)

        when (estado) {
            3 -> {
                suministroViewModel.getSuministroCorteById(t.toInt()).observe(this) {
                    if (it != null) {
                        textViewTitle.text = String.format("Orden : %s", it.orden)
                        textViewMedidor.text = String.format("Medidor :%s", it.suministro_Medidor)
                        textViewContrato.text = String.format("Contrato :%s", it.suministro_Numero)
                        textViewDireccion.text = it.suministro_Direccion
                    }
                }
            }
            4 -> {
                suministroViewModel.getSuministroReconexionById(t.toInt()).observe(this) {
                    if (it != null) {
                        textViewTitle.text = String.format("Orden : %s", it.orden)
                        textViewMedidor.text = String.format("Medidor :%s", it.suministro_Medidor)
                        textViewContrato.text = String.format("Contrato :%s", it.suministro_Numero)
                        textViewDireccion.text = it.suministro_Direccion
                    }
                }
            }
            else -> {
                suministroViewModel.getSuministroLecturaById(t.toInt()).observe(this) {
                    if (it != null) {
                        textViewTitle.text = String.format("Orden : %s", it.orden)
                        textViewMedidor.text = String.format("Medidor :%s", it.suministro_Medidor)
                        textViewContrato.text = String.format("Contrato :%s", it.suministro_Numero)
                        textViewDireccion.text = it.suministro_Direccion
                    }
                }
            }
        }

        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        buttonSalir.setOnClickListener {
            dialog.dismiss()
        }

    }
}