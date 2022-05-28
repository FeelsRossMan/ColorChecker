package com.example.colorchecker.model

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhotoViewModel : ViewModel() {
    private var _imageBitmap = MutableLiveData<Bitmap>()
    val imageBitmap: LiveData<Bitmap>
        get() = _imageBitmap

    fun updateImageBitmap(bitmap: Bitmap) {
        _imageBitmap.value = bitmap
    }

    fun getColorStringOnClick(x: Float, y: Float) : String? {
        val color = _imageBitmap.value?.getPixel(x.toInt(), y.toInt())

        Log.d("PhotoViewModel", "Location: ($x, $y), Color: ($color)")
        return color?.let { colorIntToString(it) }
    }

    private fun colorIntToString(colorInt: Int): String {
        return String.format("#%06X", 0xFFFFFF and colorInt)
    }

    fun getColorCodeOnClick(x: Float, y: Float): Int? {
        return _imageBitmap.value?.getPixel(x.toInt(), y.toInt())
    }
}