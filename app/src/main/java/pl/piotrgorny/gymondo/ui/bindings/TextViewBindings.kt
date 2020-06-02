package pl.piotrgorny.gymondo.ui.bindings

import android.text.Html
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("html")
fun TextView.setTextFromHtml(html: String){
    this.text = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
}