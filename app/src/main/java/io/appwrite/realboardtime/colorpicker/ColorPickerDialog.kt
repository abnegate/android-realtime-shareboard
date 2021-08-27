package io.appwrite.realboardtime.colorpicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import io.appwrite.realboardtime.R

class ColorPickerDialog private constructor(
    val context: Context,
    val title: String,
    val positiveButton: String,
    val negativeButton: String,
    val colorListener: ((Int, String) -> Unit)?,
    val dismissListener: (() -> Unit)?,
    val defaultColor: String?,
    val colorSwatch: ColorSwatch,
    val colors: List<String>? = null,
    var isTickColorPerCard: Boolean = false
) {

    class Builder(val context: Context) {

        private var title = context.getString(R.string.pick_color)
        private var positiveButton = context.getString(R.string.ok)
        private var negativeButton = context.getString(R.string.cancel)

        private var colorListener: ((Int, String) -> Unit)? = null
        private var dismissListener: (() -> Unit)? = null

        private var defaultColor: String? = null
        private var colorSwatch: ColorSwatch = ColorSwatch._300
        private var colors: List<String>? = null

        private var isTickColorPerCard: Boolean = false

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setTitle(@StringRes title: Int): Builder {
            this.title = context.getString(title)
            return this
        }

        fun setPositiveButton(text: String): Builder {
            this.positiveButton = text
            return this
        }

        fun setPositiveButton(@StringRes text: Int): Builder {
            this.positiveButton = context.getString(text)
            return this
        }

        fun setNegativeButton(text: String): Builder {
            this.negativeButton = text
            return this
        }

        fun setNegativeButton(@StringRes text: Int): Builder {
            this.negativeButton = context.getString(text)
            return this
        }

        fun setColorSwatch(colorSwatch: ColorSwatch): Builder {
            this.colorSwatch = colorSwatch
            return this
        }

        fun setColorListener(listener: (Int, String) -> Unit): Builder {
            this.colorListener = listener
            return this
        }

        fun setDismissListener(listener: () -> Unit): Builder {
            this.dismissListener = listener
            return this
        }

        fun setColors(colors: List<String>): Builder {
            this.colors = colors
            return this
        }

        fun setColors(colors: Array<String>): Builder {
            this.colors = colors.toList()
            return this
        }

        fun setColorRes(colors: List<Int>): Builder {
            this.colors = colors.map { ColorsTool.formatColor(it) }
            return this
        }

        fun setColorRes(colors: IntArray): Builder {
            this.colors = colors.map { ColorsTool.formatColor(it) }
            return this
        }

        fun build(): ColorPickerDialog {
            return ColorPickerDialog(
                context = context,
                title = title,
                positiveButton = positiveButton,
                negativeButton = negativeButton,
                colorListener = colorListener,
                dismissListener = dismissListener,
                defaultColor = defaultColor,
                colorSwatch = colorSwatch,
                colors = colors,
                isTickColorPerCard = isTickColorPerCard
            )
        }
    }

    fun show() {
        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setNegativeButton(negativeButton, null)

        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_color_picker, null) as View
        dialog.setView(dialogView)

        val colorList = colors ?: ColorsTool.getColors(context, colorSwatch.value)
        val adapter = ColorPickerAdapter(colorList)
        adapter.setTickColorPerCard(isTickColorPerCard)
        if (!defaultColor.isNullOrBlank()) {
            adapter.setDefaultColor(defaultColor)
        }

        val recycler = dialogView.findViewById<RecyclerView>(R.id.colorRecycler)
        recycler.setHasFixedSize(true)
        recycler.adapter = adapter

        dialog.setPositiveButton(positiveButton) { _, _ ->
            val color = adapter.getSelectedColor()
            if (color.isNotBlank()) {
                colorListener?.invoke(ColorsTool.parseColor(color), color)
            }
        }

        dismissListener?.let { listener ->
            dialog.setOnDismissListener {
                listener.invoke()
            }
        }

        val alertDialog = dialog.create()

        alertDialog.show()
    }
}
