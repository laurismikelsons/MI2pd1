package lv.laurismikelsons.mediav3

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.IOException
import java.util.*

class TestAudio : AppCompatActivity() {

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
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_delete ->{
                delete()
                val intent = Intent(this, TestAudio::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private lateinit var outputDirectory: File
    private lateinit var fileName: String
    private lateinit var outputFilePath: String

    private lateinit var recordButton: Button
    private lateinit var recordingsList: ListView

    private var mediaRecorder: MediaRecorder? = null
    private var recordingsListItems = mutableListOf<String>()

    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_audio)
        analytics = Firebase.analytics

        analytics.logEvent("costum_event", savedInstanceState)

        outputDirectory = getOutputDirectory()!!
        recordButton = findViewById(R.id.record_button)
        recordingsList = findViewById(R.id.recordings_list)

        var gpath: String = Environment.getExternalStorageDirectory().absolutePath
        var spath = "Android/media/lv.laurismikelsons.mediav3/MediaV3_AUDIO"
        var fullpath = File(gpath + File.separator + spath)
        fileReaderNew(fullpath)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, recordingsListItems)
        recordingsList.adapter = adapter

        // Set up the record button
        recordButton.setOnClickListener {
            if (mediaRecorder != null) {
                stopRecording()
            } else {
                startRecording()
            }
        }
    }

    private fun fileReaderNew(root: File) {
        val listAllFiles = root.listFiles()
        if (listAllFiles != null && listAllFiles.isNotEmpty()) {
            for (currentFile in listAllFiles) {
                if (currentFile.name.endsWith(".3gp")) {
                    Log.e("fullpath", "" + currentFile.name)
                    recordingsListItems.add(0, currentFile.name)
                }
            }
        }
    }

    private fun startRecording() {
        fileName = SimpleDateFormat(FILE_NAME_FORMAT, Locale.getDefault()).format(System.currentTimeMillis())+".3gp"
        outputFilePath = File(outputDirectory, fileName).absolutePath
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFilePath)
            try {
                prepare()
                recordButton.text = "Stop Recording"
            } catch (e: IOException) {
                Log.e(TAG, "prepare() failed")
            }
            start()
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply{
            stop()
            release()
        }
        mediaRecorder = null

        recordingsListItems.add(0, fileName)
        (recordingsList.adapter as ArrayAdapter<*>).notifyDataSetChanged()

        recordButton.text = "Record"
    }

    private fun getOutputDirectory():File? {
        val mediaDir = externalMediaDirs.firstOrNull()?.let{
            File(it, resources.getString(R.string.app_name) + "_AUDIO").apply {mkdirs()}}

        return if(mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

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
    }
}