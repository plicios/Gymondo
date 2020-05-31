package pl.piotrgorny.gymondo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import pl.piotrgorny.gymondo.databinding.ItemImageBinding

class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    var items = listOf<String>()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ImageViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_image, parent, false
        )
    )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.binding.imageUrl = items[position]
    }

    class ImageViewHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root)
}