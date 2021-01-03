package com.example.inzynierka.ui.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private var photoPath: String? = null
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val images = listOf<Photo>(
            Photo("Image1", R.drawable.image1),
            Photo("Image2", R.drawable.image2),
        )
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_photos)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = PhotoAdapter(requireContext(), images);


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
                if (grantResults.size > 0 && grantResults[0] ==
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
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            val path = convertMediaUriToPath(data?.data)
            Log.d("DataFromGallery", path!!)
            photoPath = path!!

        }
    }

}


