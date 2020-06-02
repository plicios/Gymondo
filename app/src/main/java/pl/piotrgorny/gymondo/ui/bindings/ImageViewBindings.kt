package pl.piotrgorny.gymondo.ui.bindings

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import pl.piotrgorny.gymondo.R

@BindingAdapter("imageUrl")
fun ImageView.loadImageByUrl(url: String?) {
    if(url == null){
        this.setImageResource(R.drawable.ic_image)
        return
    }
    if(url == "error"){
        this.setImageResource(R.drawable.ic_error)
        return
    }
    Picasso.get()
        .load(url)
        .placeholder(R.drawable.ic_image)
        .error(R.drawable.ic_error)
        .into(this)
}