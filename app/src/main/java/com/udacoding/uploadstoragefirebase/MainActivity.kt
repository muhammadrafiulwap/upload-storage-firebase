package com.udacoding.uploadstoragefirebase

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mStorageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPermission()

        mStorageRef = FirebaseStorage.getInstance().reference

        showData()

        floatingActionButton.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }

    }

    private fun showData(){
        val storageImage = mStorageRef?.child("images/")
        storageImage?.listAll()
            ?.addOnSuccessListener {
                recyclerView.adapter = DataAdapter(it.items)
                progressBar.visibility = View.GONE
            }?.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 1
            )
        }
    }

    override fun onResume() {
        super.onResume()
        showData()
    }
}