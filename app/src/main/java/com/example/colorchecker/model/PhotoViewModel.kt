package com.example.colorchecker.model

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhotoViewModel: ViewModel() {
    private var _imageBitmap = MutableLiveData<Bitmap>()
    val imageBitmap: LiveData<Bitmap>
        get() = _imageBitmap

    fun updateImageBitmap (bitmap: Bitmap) {
        _imageBitmap.value = bitmap
    }

}