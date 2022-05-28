package com.example.colorchecker.model

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.view.Surface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhotoViewModel : ViewModel() {
    private var _imageBitmap = MutableLiveData<Bitmap>()
    val imageBitmap: LiveData<Bitmap>
        get() = _imageBitmap

    fun updateImageBitmap(bitmap: Bitmap, rotation: Int) {
        when (rotation) {
            Surface.ROTATION_0 -> _imageBitmap.value = bitmap.rotate(90F)
            Surface.ROTATION_180 -> _imageBitmap.value = bitmap.rotate(-90F)
            else -> _imageBitmap.value = bitmap

        }

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
    private fun Bitmap.rotate(degrees: Float): Bitmap? {
        val matrix = Matrix().apply { postRotate(degrees)}
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }
}