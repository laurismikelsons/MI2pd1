package lv.laurismikelsons.mediav3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import lv.laurismikelsons.mediav3.databinding.ActivityMainBinding
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Camera elements
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null

    // File element
    private lateinit var outputDirectory: File

    // View element
    private lateinit var btnTakePhoto:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnTakePhoto = findViewById(R.id.fotoButton)
        btnTakePhoto.setOnClickListener{takePhoto()}

        if (allPermissionGranted()){
            startCamera()
        }else{
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        outputDirectory = getOutputDirectory()!!
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_images ->{
                val intent = Intent(this,GalleryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_audio ->{
                val intent = Intent(this,TestAudio::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener( {
            preview = Preview.Builder().build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)}
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            }catch (e: Exception){
                Log.e(TAG, "nevarēja palaist kameru", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto(){
        var imageCapture = imageCapture ?: return
        val photoFile = File(outputDirectory, SimpleDateFormat(FILE_NAME_FORMAT, Locale.getDefault()).format(System.currentTimeMillis())+".jpg")
        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(outputOption, ContextCompat.getMainExecutor(this), object :ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                val msg = "Fotogrāfija saglāta"
                Toast.makeText(baseContext, "$msg $savedUri", Toast.LENGTH_SHORT).show()
                Log.e(TAG,msg)
            } override fun onError(exception: ImageCaptureException) {
                Log.e(TAG, "Neizdevās foto: $(exception.message)", exception) }
        })
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all{
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (allPermissionGranted()){
                startCamera()
            }else{
                Toast.makeText(this, "NAV PIEŠĶIRTAS NEPIECIEŠAMĀS TIESĪBAS", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun getOutputDirectory():File? {
        val mediaDir = externalMediaDirs.firstOrNull()?.let{
            File(it, resources.getString(R.string.app_name) + "_IMG").apply {mkdirs()}}

            return if(mediaDir != null && mediaDir.exists())
                mediaDir else filesDir
    }

    companion object{
        private const val TAG = "CameraX"
        private const val FILE_NAME_FORMAT = "yy-MM-dd-HH-mm-ss"
        private const val REQUEST_CODE_PERMISSIONS = 123
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    }

}