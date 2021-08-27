package io.appwrite.realboardtime.core

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

fun Context.readAssetFile(fileName: String): String {
    return assets.open(fileName).bufferedReader().use {
        it.readText()
    }
}

fun Activity.hideSoftKeyBoard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    if (imm?.isAcceptingText == true) {
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}