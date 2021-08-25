package io.appwrite.realboardtime.core

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

fun Activity.hideSoftKeyBoard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    if (imm?.isAcceptingText == true) {
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}