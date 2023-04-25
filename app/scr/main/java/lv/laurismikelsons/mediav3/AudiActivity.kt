package lv.laurismikelsons.mediav3
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.IOException
import java.util.*

class AudiActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu_2,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_camera ->{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_images ->{
                val intent = Intent(this,
                    MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_delete ->{
                delete()
            }
        }
        return super.onOptionsItemSelected(item)
    }
///////////////////
    private lateinit var fileName: File
    private lateinit var outputDirectory: File

    private var recordButton: RecordButton? = null
    private var recorder: MediaRecorder? = null

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC) //
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) //
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) //
            setOutputFile(fileName)
            try {
                prepare()
            } catch (e: IOException) {
                Log.e(TAG, "prepare() failed")
            }
            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    internal inner class RecordButton(ctx: Context) : androidx.appcompat.widget.AppCompatButton(ctx) {

        private var mStartRecording = true

        @RequiresApi(Build.VERSION_CODES.O)
        var clicker: OnClickListener = OnClickListener {
            onRecord(mStartRecording)
            text = when (mStartRecording) {
                true -> "Stop recording"
                false -> "Start recording"
            }
            mStartRecording = !mStartRecording
        }

        init {
            text = "Start recording"
            setOnClickListener(clicker)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        outputDirectory = getOutputDirectory()!!
        fileName = File(outputDirectory, SimpleDateFormat(AudiActivity.FILE_NAME_FORMAT, Locale.getDefault()).format(System.currentTimeMillis())+".3.gp")

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        recordButton = RecordButton(this)
        val ll = LinearLayout(this).apply {
            addView(recordButton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0f))
            //addView()
        }
        setContentView(ll)

    }
/////////////////////////////////
    private fun getOutputDirectory():File? {
        val mediaDir = externalMediaDirs.firstOrNull()?.let{
            File(it, resources.getString(R.string.app_name) + "_AUDIO").apply {mkdirs()}}

        return if(mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }///////////////////
/////////////////////////////////////////
    private fun deleteDirectory(directory: File) {
        for (file in directory.listFiles()) {
            if (!file.isDirectory) {
                file.delete()
            }
        }
    }
    private fun delete() {
        val directory = File("/storage/emulated/0/Android/media/lv.laurisMikelsons.mediav3/MediaV3_AUDIO/")
        deleteDirectory(directory)
    }


    companion object{
        private const val TAG = "Audio"
        private const val FILE_NAME_FORMAT = "yy-MM-dd-HH-mm-ss"
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}