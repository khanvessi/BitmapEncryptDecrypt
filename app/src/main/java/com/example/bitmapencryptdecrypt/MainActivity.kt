package com.example.bitmapencryptdecrypt

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.example.bitmapencryptdecrypt.databinding.ActivityMainBinding
import androidx.core.app.ActivityCompat.requestPermissions

import android.os.Build
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Base64
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val STORAGE_PERMISSION_REQUEST_CODE: Int = 1
    private val REQUEST_WRITE_PERMISSION: Int = 1
    var sImage: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            btnEncrypt.setOnClickListener(View.OnClickListener {
                requestPermission()
            })

            btnDecrypt.setOnClickListener(View.OnClickListener {
                val bytes = Base64.decode(sImage, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                binding.imageView.setImageBitmap(bitmap)
            })
        }

    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), REQUEST_WRITE_PERMISSION
            )
        } else {
            //openFilePicker()
            selectImage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage()
        } else {
            //DENIED
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            val uri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

            val stream = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            val bytes = stream.toByteArray()

            sImage = Base64.encodeToString(bytes, Base64.DEFAULT)

            binding.textView.text = sImage


        }

    }

    private fun selectImage() {
        binding.textView.text = ""
        binding.imageView.setImageBitmap(null)
        val intent = Intent(Intent.ACTION_PICK)

        intent.setType("image/*")

        startActivityForResult(Intent.createChooser(intent, "Select Image"), 100)
    }
}