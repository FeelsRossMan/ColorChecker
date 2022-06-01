package com.example.colorchecker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.colorchecker.databinding.AnalysisFragmentBinding
import com.example.colorchecker.model.PhotoViewModel
import java.io.File


private const val FILE_NAME = "photo.jpg"
private lateinit var photoFile: File


class AnalysisFragment : Fragment() {

    private val model: PhotoViewModel by activityViewModels()

    private lateinit var imageResultLauncher: ActivityResultLauncher<Intent>
    private var _binding: AnalysisFragmentBinding? = null

    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    handleCameraImage(photoFile)
                }
            }
        //
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AnalysisFragmentBinding.inflate(inflater, container, false)
        _binding!!.openCameraFAB.setOnClickListener {
            photoFile = getPhotoFile()
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val fileProvider = FileProvider.getUriForFile(
                this.requireContext(),
                "com.example.colorchecker.fileprovider",
                photoFile
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            imageResultLauncher.launch(cameraIntent)
        }
        _binding!!.photoIV.setOnTouchListener { _, motionEvent -> onTouchPhotoIV(motionEvent = motionEvent) }



        return binding?.root
    }

    private fun getPhotoFile(): File {
        val storageDirectory = this.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(FILE_NAME, ".jpg", storageDirectory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        model.imageBitmap.observe(viewLifecycleOwner) { t: Bitmap? ->
            _binding?.photoIV?.setImageBitmap(t)
        }

    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.title = setTitle()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleCameraImage(photoPath: File) {
        val exifInterface = ExifInterface(photoPath.absolutePath)
        val bitmap = BitmapFactory.decodeFile(photoPath.absolutePath)
        val imageView = _binding?.photoIV
        imageView?.let {
            val x = imageView.measuredWidth
            Log.d(LOG_TAG, "New bitmap with values width: $x")
            model.newImageBitmap(
                bitmap,
                x,
                exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            )
        }
        (activity as AppCompatActivity).supportActionBar?.title = setTitle()

        hideColorPopup()
    }

    private fun onTouchPhotoIV(motionEvent: MotionEvent): Boolean {
        showColorPopup(motionEvent.x, motionEvent.y)
        return false
    }

    private fun showColorPopup(x: Float, y: Float) {

        if (model.isNull()) {
            Log.d(LOG_TAG, "showColorPopup called with null bitmap")
            return
        }

        val colorPopupView = _binding?.colorPopupLL

        colorPopupView?.findViewById<TextView>(R.id.color_popup_ID)?.text =
            model.getColorStringOnClick(x, y)
        model.getColorCodeOnClick(x, y).let {
            colorPopupView?.findViewById<LinearLayout>(R.id.color_popup_LL)?.setBackgroundColor(it)
            Log.d(LOG_TAG, "$it")
            if (it < -10000000) {
                colorPopupView?.findViewById<TextView>(R.id.color_popup_ID)?.setTextColor(ContextCompat.getColor(
                    this.requireContext(), R.color.white))
            } else {
                colorPopupView?.findViewById<TextView>(R.id.color_popup_ID)?.setTextColor(ContextCompat.getColor(
                    this.requireContext(), R.color.black))
            }
        }

        var xVal = x
        var yVal = y


        if (checkPopupLocationForWidth(x)) xVal -= colorPopupView?.width!!
        if (checkPopupLocationForHeight(y)) yVal -= colorPopupView?.height!!

        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        with(layoutParams) {
            setMargins(xVal.toInt(), yVal.toInt(), 0, 0)
        }



        colorPopupView?.layoutParams = layoutParams
        colorPopupView?.visibility = View.VISIBLE
    }

    private fun hideColorPopup() {
        _binding?.colorPopupLL?.visibility = View.INVISIBLE
    }

    private fun checkPopupLocationForWidth(x: Float) : Boolean {
        val linearLayout = _binding!!.photoRL
        val colorPopup = _binding!!.colorPopupLL
        val xMax = linearLayout.width
        return colorPopup.width + x >= xMax
    }
    private fun checkPopupLocationForHeight(y: Float) : Boolean {
        val linearLayout = _binding!!.photoRL
        val colorPopup = _binding!!.colorPopupLL
        val yMax = linearLayout.height
        return colorPopup.height + y >= yMax
    }
    private fun setTitle(): CharSequence {
        if (!model.isNull()) return "Click a pixel to determine the color"
        return "Take a photo to get started"
    }

    companion object {
        private const val LOG_TAG = "AnalysisFragment"
    }
}