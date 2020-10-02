package com.udacoding.uploadstoragefirebase

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.udacoding.uploadstoragefirebase.utils.FilePath
import kotlinx.android.synthetic.main.activity_upload.*
import kotlinx.android.synthetic.main.dialog_choose_image.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.math.log
import kotlin.random.Random

class UploadActivity : AppCompatActivity() {

    private var dialog: Dialog? = null
    private val CAMERA_CODE = 1
    private val GALLERY_CODE = 2

    private var image_path: String? = null

    private var mStorageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        mStorageRef = FirebaseStorage.getInstance().reference

        imageView.setOnClickListener {
            showDialog()
        }

        button.setOnClickListener {
            upload(image_path.toString())
        }

    }

    private fun upload(path: String){

        progressBar.visibility = View.VISIBLE

        val file = Uri.fromFile(File(path))
        val meta = File(path)
        val storageRef = mStorageRef?.child("images/${meta.name}")

        storageRef?.putFile(file)
            ?.addOnSuccessListener {
                Toast.makeText(this, "File berhasil di upload", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                finish()
            }?.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        dialog?.dismiss()

        if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {

            resultCamera(data)

        } else if (requestCode == GALLERY_CODE && resultCode == Activity.RESULT_OK) {

            resultGallery(data)

        }
    }

    private fun resultGallery(data: Intent?) {

        val image_bitmap = selectFromGalleryResult(data)
        imageView.setImageBitmap(image_bitmap)

    }

    private fun selectFromGalleryResult(data: Intent?): Bitmap {
        var bm: Bitmap? = null
        if (data!=null){
            try {
                image_path = data.data?.let { FilePath.getPath(this, it) }

                bm = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, data.data)
            } catch (e: IOException){
                e.printStackTrace()
            }
        }

        return bm!!
    }

    private fun resultCamera(data: Intent?) {

        val image = data?.extras?.get("data")
        val random = Random.nextInt(0, 999999)
        val name_file = "Camera$random"

        image_path = persistImage(image as Bitmap, name_file)

        imageView.setImageBitmap(BitmapFactory.decodeFile(image_path))

    }

    private fun persistImage(bitmap: Bitmap, name: String): String {

        val filesDir = filesDir
        val imageFile = File(filesDir, "${name}.png")

        val image_path = imageFile.path

        val os: OutputStream?
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
            os.flush()
            os.close()
        } catch (e: Exception){
            Log.e("TAG", "persistImage: ${e.message.toString()} ", e )
        }

        return image_path
    }


    private fun showDialog() {
        val window = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_choose_image, null)
        window.setView(view)

        view.buttonCamera.setOnClickListener {

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_CODE)

        }

        view.buttonGallery.setOnClickListener {

            val mimeType = arrayOf("image/jpg", "image/jpeg", "image/gif")

            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivityForResult(Intent.createChooser(intent, "Choose image"), GALLERY_CODE)

        }

        dialog = window.create()
        dialog?.show()
    }
}