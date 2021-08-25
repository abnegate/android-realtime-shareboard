package io.appwrite.realboardtime.core

import com.google.gson.Gson
import io.appwrite.extensions.fromJson

val gson = Gson()

inline fun <reified T> String.fromJson() = try {
    gson.fromJson(this, T::class.java)
} catch (ex: Exception) {
    ex.printStackTrace()
    throw ex
}

inline fun <reified T> Any.cast() = try {
    gson.toJson(this).fromJson(T::class.java)
} catch (ex: Exception) {
    ex.printStackTrace()
    throw ex
}