package io.appwrite.realboardtime.colorpicker

import android.content.Context
import android.graphics.Color
import androidx.core.graphics.ColorUtils.calculateLuminance
import io.appwrite.realboardtime.core.readAssetFile
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

object ColorsTool {

    lateinit var colorMap: Map<String, MutableList<String>>

    fun getColors(context: Context, brightness: String? = null): List<String> {
        if (!ColorsTool::colorMap.isInitialized) {
            colorMap = loadColors(context)
        }
        if (brightness == null) {
            return colorMap.values.flatten()
        }
        return colorMap[brightness] ?: Collections.emptyList()
    }

    private fun loadColors(context: Context): Map<String, MutableList<String>> {
        val colorJson = context.readAssetFile("material-colors.json")
        val colorMain = JSONObject(colorJson)
        val colorMap = mutableMapOf<String, MutableList<String>>()
        for (colorName in colorMain.keys()) {
            val jsonObject = colorMain.getJSONObject(colorName)
            for (colorCode in jsonObject.keys()) {
                val colorHex = jsonObject.getString(colorCode)
                var shades = colorMap[colorCode]
                if (shades == null) {
                    shades = ArrayList()
                    colorMap[colorCode] = shades
                }
                shades.add(colorHex)
            }
        }
        return colorMap
    }

    fun parseColor(color: String): Int {
        return if (color.isBlank()) {
            0
        } else {
            Color.parseColor(color)
        }
    }

    fun formatColor(color: Int): String {
        return String.format("#%06x", color and 0xffffff)
    }

    fun String.isDarkColor(): Boolean = parseColor(this).isDarkColor()

    fun Int.isDarkColor() = calculateLuminance(this) <= 0.4
}
