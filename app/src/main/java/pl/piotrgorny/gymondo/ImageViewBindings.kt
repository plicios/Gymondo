package pl.piotrgorny.gymondo

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("imageUrl")
fun ImageView.loadImageByUrl(url: String?) {
    if(url == null){
        Picasso.get()
            .load(R.drawable.ic_image)
            .into(this)
        return
    }
    Picasso.get()
        .load(url)
        .placeholder(R.drawable.ic_image)
        .error(R.drawable.ic_error)
        .into(this)
}