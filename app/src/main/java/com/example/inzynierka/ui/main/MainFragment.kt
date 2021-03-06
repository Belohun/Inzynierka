package com.example.inzynierka.ui.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.BASE
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inzynierka.R
import com.example.inzynierka.models.Photo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.spec.PSSParameterSpec.DEFAULT
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.spec.OAEPParameterSpec.DEFAULT


class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = firebaseAuth.currentUser!!.uid
    private val CAMERA_CODE = 1888
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001

    val storageReference = Firebase.storage.reference


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val fabOpenAnim = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        val fabCloseAnim = AnimationUtils.loadAnimation(context, R.anim.fab_close)
        val fabRotateOpen = AnimationUtils.loadAnimation(context, R.anim.fab_rotate_open)
        val fabRotateClose = AnimationUtils.loadAnimation(context, R.anim.fab_rotate_close)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fab_add)
        val fabCamera = view.findViewById<FloatingActionButton>(R.id.fab_camera)
        val fabGallery = view.findViewById<FloatingActionButton>(R.id.fab_gallery)
        var isOpen = false

        fabAdd.setOnClickListener {
            if (isOpen) {
                fabAdd.startAnimation(fabRotateClose)
                fabCamera.startAnimation(fabCloseAnim)
                fabGallery.startAnimation(fabCloseAnim)

                isOpen = false

            } else {
                fabAdd.startAnimation(fabRotateOpen)
                fabCamera.startAnimation(fabOpenAnim)
                fabGallery.startAnimation(fabOpenAnim)

                isOpen = true
            }

        }
        fabCamera.setOnClickListener {
            fabAdd.startAnimation(fabRotateClose)
            fabCamera.startAnimation(fabCloseAnim)
            fabGallery.startAnimation(fabCloseAnim)
            isOpen = false
            if (checkSelfPermission(
                    requireContext(), android.Manifest.permission.CAMERA
                ) ==
                PackageManager.PERMISSION_DENIED
            ) {
                val permissions = arrayOf(android.Manifest.permission.CAMERA);
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                pickImageFromCamera();
            }

        }
        fabGallery.setOnClickListener {
            fabAdd.startAnimation(fabRotateClose)
            fabCamera.startAnimation(fabCloseAnim)
            fabGallery.startAnimation(fabCloseAnim)
            isOpen = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(
                        requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    //permission denied
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    //permission already granted
                    pickImageFromGallery();
                }
            } else {
                //system OS is < Marshmallow
                pickImageFromGallery();
            }
        }

        listFiles()
    }

    private fun pickImageFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_CODE)
    }

    private fun listFiles() = CoroutineScope(Dispatchers.IO).launch {
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerView_photos)
        try {
            val imageUrlsLeft = mutableListOf<Photo>()
            val imageUrlsRight = mutableListOf<Photo>()
            var i = 1


            storageReference.child("images/$currentUser/").listAll().addOnSuccessListener { images ->
                for (image in images.items) {
                    image.downloadUrl.addOnSuccessListener { url ->
                        Log.d("i", i.toString())
                        if (i % 2 == 0) {
                            imageUrlsRight.add(
                                Photo(
                                    url.toString(),
                                    url.toString().substringAfter("o/images%2F$currentUser%2F")
                                        .substringBefore(".")
                                )
                            )
                        } else {
                            imageUrlsLeft.add(
                                Photo(
                                    url.toString(),
                                    url.toString().substringAfter("o/images%2F$currentUser%2F")
                                        .substringBefore(".")
                                )
                            )
                        }
                        i++
                    }.continueWith {
                        val imageAdapter =
                            PhotoAdapter(
                                requireContext(),
                                imagesLeft = imageUrlsLeft,
                                imagesRight = imageUrlsRight
                            )
                        recyclerView.apply {
                            adapter = imageAdapter
                            layoutManager = LinearLayoutManager(requireContext())

                        }
                    }


                }

            }


        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun convertMediaUriToPath(uri: Uri?): String? {
        val proj = arrayOf<String>(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = requireActivity().contentResolver.query(uri!!, proj, null, null, null)
        val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val path: String = cursor.getString(column_index)
        cursor.close()
        return path
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {

                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val path = convertMediaUriToPath(data?.data)
            var file = Uri.fromFile(File(path))
            val riversRef = storageReference.child("images/$currentUser/${file.lastPathSegment}")
            riversRef.putFile(file).addOnSuccessListener {
                listFiles()
            }
        }else if(resultCode == Activity.RESULT_OK && requestCode == CAMERA_CODE){
            val baos = ByteArrayOutputStream()
            val bitmap = data?.extras?.get("data") as Bitmap
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val data = baos.toByteArray()
            val timeStamp = SimpleDateFormat("yyMMdd_HHmmss").format(Date())
            val imageName = "$timeStamp.png"
            val riversRef = storageReference.child("images/$currentUser/${imageName}")
            riversRef.putBytes(data).addOnSuccessListener {
                listFiles()
            }


        }
    }

}


