package io.appwrite.realboardtime.colorpicker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import io.appwrite.realboardtime.R
import io.appwrite.realboardtime.colorpicker.ColorsTool.isDarkColor

class ColorPickerAdapter(private val colors: List<String>) :
    RecyclerView.Adapter<ColorPickerAdapter.MaterialColorViewHolder>() {

    private var isDarkColor = false
    private var color = ""
    private var isTickColorPerCard = false

    init {
        val darkColors = colors.count { it.isDarkColor() }
        isDarkColor = (darkColors * 2) >= colors.size
    }

    fun setDefaultColor(color: String) {
        this.color = color
    }

    fun setTickColorPerCard(tickColorPerCard: Boolean) {
        this.isTickColorPerCard = tickColorPerCard
    }

    fun getSelectedColor() = color

    fun getItem(position: Int) = colors[position]

    override fun getItemCount() = colors.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialColorViewHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_color_picker,
            parent,
            false
        )
        return MaterialColorViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: MaterialColorViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setBackgroundColor(cardView: CardView, hexColor: String) {
        cardView.setCardBackgroundColor(Color.parseColor(hexColor))
    }

    inner class MaterialColorViewHolder(private val rootView: View) :
        RecyclerView.ViewHolder(rootView) {

        private val colorView = rootView.findViewById<CardView>(R.id.colorView)
        private val checkIcon = rootView.findViewById<ImageView>(R.id.checkIcon)

        init {
            rootView.setOnClickListener {
                val newIndex = it.tag as Int
                val color = getItem(newIndex)

                val oldIndex = colors.indexOf(this@ColorPickerAdapter.color)
                this@ColorPickerAdapter.color = color

                notifyItemChanged(oldIndex)
                notifyItemChanged(newIndex)
            }
        }

        fun bind(position: Int) {
            val color = getItem(position)

            rootView.tag = position

            setBackgroundColor(colorView, color)

            val isChecked = color == this@ColorPickerAdapter.color
            checkIcon.visibility = if (isChecked) View.VISIBLE else View.GONE

            var darkColor = isDarkColor
            if (isTickColorPerCard) {
                darkColor = color.isDarkColor()
            }

            checkIcon.setColorFilter(if (darkColor) Color.WHITE else Color.BLACK)
        }
    }
}
