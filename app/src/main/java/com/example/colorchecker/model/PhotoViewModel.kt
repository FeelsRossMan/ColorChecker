package com.example.colorchecker.model

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhotoViewModel : ViewModel() {
    private var _imageBitmap = MutableLiveData<Bitmap>()
    val imageBitmap: LiveData<Bitmap>
        get() = _imageBitmap

    fun updateImageBitmap(bitmap: Bitmap) {
        _imageBitmap.value = bitmap.rotate()
    }

    private fun Bitmap.rotate(): Bitmap? {
        val matrix = Matrix().apply { postRotate(90.0F) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    fun clickedImage(x: Float, y: Float) {

        Log.d("PhotoViewModel", "Location: ($x, $y")

        _imageBitmap.value?.setPixel(x.toInt(), y.toInt(), Color.argb(0,255,0,0))
        _imageBitmap.value = _imageBitmap.value?.let { Bitmap.createBitmap(it) }
    }
}