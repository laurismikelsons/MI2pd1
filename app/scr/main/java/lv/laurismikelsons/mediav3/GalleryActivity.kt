package lv.laurismikelsons.mediav3

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import lv.laurismikelsons.mediav3.databinding.ActivityGalleryBinding
import java.io.File
import kotlin.properties.Delegates

class GalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGalleryBinding
    private lateinit var imageViewPagerAdapter: ImageViewPagerAdapter
    var num by Delegates.notNull<Int>()
    private var imageUriList = mutableListOf<File>()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //populating the list with some image urls
        var gpath: String = Environment.getExternalStorageDirectory().absolutePath
        var spath = "Android/media/lv.laurismikelsons.mediav3/MediaV3_IMG"
        var fullpath = File(gpath + File.separator + spath)
        Log.w("fullpath", "" + fullpath)
        imageReaderNew(fullpath)

        //initializing the adapter
        imageViewPagerAdapter = ImageViewPagerAdapter(imageUriList)

        num = imageUriList.size

        setUpViewPager()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu_3,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_camera ->{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_audio ->{
                val intent = Intent(this, AudiActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_delete ->{
                val intent = Intent(this, GalleryActivity::class.java)
                delete()
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun imageReaderNew(root: File) {
        val listAllFiles = root.listFiles()
        if (listAllFiles != null && listAllFiles.isNotEmpty()) {
            for (currentFile in listAllFiles) {
                if (currentFile.name.endsWith(".jpg")) {
                    imageUriList.add(File(currentFile.absolutePath))
                }
            }
        }
    }

    private fun setUpViewPager() {
        binding.viewPager.adapter = imageViewPagerAdapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        val currentPageIndex = num - 1
        binding.viewPager.currentItem = currentPageIndex

        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.imageNumberTV.text = "${position + 1} / $num"
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        // unregistering the onPageChangedCallback
        binding.viewPager.unregisterOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {}
        )
    }
    private fun deleteDirectory(directory: File) {
        for (file in directory.listFiles()) {
            if (!file.isDirectory) {
                file.delete()
            }
        }
    }
    private fun delete() {
        val directory = File("/storage/emulated/0/Android/media/lv.laurisMikelsons.mediav3/MediaV3_IMG/")
        deleteDirectory(directory)
    }
}