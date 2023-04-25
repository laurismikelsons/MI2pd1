package lv.laurismikelsons.mediav3

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import lv.laurismikelsons.mediav3.databinding.ImageItemBinding
import java.io.File

class ImageViewPagerAdapter(private val imageUriList: List<File>) :
    RecyclerView.Adapter<ImageViewPagerAdapter.ViewPagerViewHolder>() {

    inner class ViewPagerViewHolder(private val binding: ImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(imageUri: File) {
            val photoUri = Uri.fromFile(imageUri)
            Glide.with(binding.root.context)
                .load(photoUri)
                .error(R.drawable.ic_launcher_background)
                .into(binding.ivImage)
        }
    }

    override fun getItemCount(): Int = imageUriList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val binding = ImageItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.setData(imageUriList[position])
    }
}