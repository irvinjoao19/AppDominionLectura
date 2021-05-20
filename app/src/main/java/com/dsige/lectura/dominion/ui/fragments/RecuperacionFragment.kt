package com.dsige.lectura.dominion.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsige.lectura.dominion.R
import com.dsige.lectura.dominion.data.local.model.Photo
import com.dsige.lectura.dominion.data.local.model.Servicio
import com.dsige.lectura.dominion.data.viewModel.SuministroViewModel
import com.dsige.lectura.dominion.data.viewModel.ViewModelFactory
import com.dsige.lectura.dominion.helper.Gps
import com.dsige.lectura.dominion.helper.Permission
import com.dsige.lectura.dominion.helper.Util
import com.dsige.lectura.dominion.ui.activities.SuministroActivity
import com.dsige.lectura.dominion.ui.adapters.RecuperaPhotoAdapter
import com.dsige.lectura.dominion.ui.adapters.ServicioAdapter
import com.dsige.lectura.dominion.ui.listeners.OnItemClickListener
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_main.*
import java.io.File
import java.io.IOException
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RecuperacionFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var suministroViewModel: SuministroViewModel
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
        return inflater.inflate(R.layout.fragment_recuperacion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    override fun onStart() {
        super.onStart()
        suministroViewModel.getRecoveredPhotos()
    }

    private fun bindUI() {

        suministroViewModel =
            ViewModelProvider(this, viewModelFactory).get(SuministroViewModel::class.java)

        suministroViewModel.getRecoveredPhotos()

        val layoutManager = LinearLayoutManager(context!!)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        val recuperaPhotoAdapter = RecuperaPhotoAdapter(object : OnItemClickListener.PhotoListener {
            override fun onItemClick(f: Photo, view: View, position: Int) {
                Util.toastMensaje(requireContext(), "En desarrollo")
            }
        })
        recyclerView.adapter = recuperaPhotoAdapter
        suministroViewModel.getPhotosRecuperadas().observe(viewLifecycleOwner) {
            recuperaPhotoAdapter.addItems(it)
        }

    }

    private fun goCamera(nameImg: String) {
//        nameImg = Util.getFechaSuministro(
//            suministro.toInt(),
//            if (tipo == 2 || tipo == 9) 10 else tipo,
//            fechaAsignacion
//        )
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            takePictureIntent.resolveActivity(packageManager)?.also {
//                val photoFile: File? = try {
//                    Util.createImageFile(nameImg, this)
//                } catch (ex: IOException) {
//                    null
//                }
//                photoFile?.also {
//                    val uriSavedImage = Uri.fromFile(it)
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage)
//
//                    if (Build.VERSION.SDK_INT >= 24) {
//                        try {
//                            val m =
//                                StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
//                            m.invoke(null)
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//                    startActivityForResult(takePictureIntent, Permission.CAMERA_REQUEST)
//                }
//            }
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Permission.CAMERA_REQUEST && resultCode == DaggerAppCompatActivity.RESULT_OK) {

//                suministroViewModel.generarArchivo(
//                    nameImg,
//                    this@PhotoActivity,
//                    fechaAsignacion,
//                    direccion,
//                    gps.getLatitude().toString(),
//                    gps.getLongitude().toString(),
//                    receive,
//                    tipo
//                )


        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecuperacionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}