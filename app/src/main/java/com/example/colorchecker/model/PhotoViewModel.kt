package com.example.colorchecker.model

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class PhotoViewModel : ViewModel() {
    private var _imageBitmap = MutableLiveData<Bitmap>()
    val imageBitmap: LiveData<Bitmap>
        get() = _imageBitmap


    fun newImageBitmap(newBitmap: Bitmap, ivWidth: Int, rotate: Int = ExifInterface.ORIENTATION_NORMAL) {
//        val bitmap = newBitmap.cropBitmapToSize(ivWidth, ivHeight)
        var bitmap = newBitmap
        Log.d(LOG_TAG, "$rotate")
        when (rotate) {
            ExifInterface.ORIENTATION_ROTATE_90 -> bitmap = bitmap.rotate(90F)!!
            ExifInterface.ORIENTATION_ROTATE_180 -> bitmap = bitmap.rotate(180F)!!
            ExifInterface.ORIENTATION_ROTATE_270 -> bitmap = bitmap.rotate(270F)!!
            else -> _imageBitmap.value = bitmap
        }
        val widthRatio:Float = bitmap.width.toFloat()/ivWidth
        val newHeight = (bitmap.height/widthRatio).toInt()


        _imageBitmap.value = Bitmap.createScaledBitmap(bitmap, ivWidth, newHeight, true)
        Log.d(LOG_TAG, "Created bitmap dimensions: width: $ivWidth height: $newHeight")
    }

//    fun updateImageBitmap(bitmap: Bitmap) {
//        _imageBitmap.value = bitmap
//    }

    fun getColorStringOnClick(x: Float, y: Float): String {
        val color = getColorCodeOnClick(x, y)
        Log.d(LOG_TAG, "Location: ($x, $y), Color: ($color)")

        return colorIntToString(color)
    }

    private fun colorIntToString(colorInt: Int): String {
        return String.format("#%06X", 0xFFFFFF and colorInt)
    }

    fun getColorCodeOnClick(x: Float, y: Float): Int {
        var returnColor = 0
        try {
            returnColor = _imageBitmap.value?.getPixel(x.toInt(), y.toInt())!!
        } catch (err: IllegalArgumentException) {
            Log.e(LOG_TAG, err.toString())
        }
        return returnColor
    }

    private fun Bitmap.rotate(degrees: Float): Bitmap? {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    fun isNull(): Boolean {
        return (_imageBitmap.value == null)
    }



    //    private fun Bitmap.cropBitmapToSize(width: Int, height: Int) : Bitmap{
//        return Bitmap.createBitmap(this, 0, 0, width, height)
//    }
    companion object {
        private const val LOG_TAG = "PhotoViewModel"
    }
}