package com.example.colorchecker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.colorchecker.databinding.AnalysisFragmentBinding
import com.example.colorchecker.model.PhotoViewModel
import java.io.File

private const val FILE_NAME = "photo.jpg"
private lateinit var photoFile: File

class AnalysisFragment: Fragment() {

    private val model: PhotoViewModel by activityViewModels()

    private lateinit var imageResultLauncher: ActivityResultLauncher<Intent>
    private var _binding: AnalysisFragmentBinding? = null

    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handleCameraImage()
            }
        }
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
            val fileProvider = FileProvider.getUriForFile(this.requireContext(), "com.example.colorchecker.fileprovider", photoFile)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            imageResultLauncher.launch(cameraIntent)
        }
        _binding!!.photoIV.setOnTouchListener { _, motionEvent->  onTouchPhotoIV(motionEvent = motionEvent)}
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleCameraImage() {
        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
        model.updateImageBitmap(bitmap, requireContext().display!!.rotation)
        hideColorPopup()

    }

    private fun onTouchPhotoIV(motionEvent: MotionEvent) : Boolean {
        showColorPopup(motionEvent.x, motionEvent.y)
        return false
    }

    private fun showColorPopup(x: Float, y: Float) {

        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(x.toInt(),y.toInt(),0,0)
        val colorPopupView = _binding?.colorPopupRL
        colorPopupView?.findViewById<TextView>(R.id.color_popup_ID)?.text = model.getColorStringOnClick(x,y)
        model.getColorCodeOnClick(x,y)?.let {
            colorPopupView?.findViewById<ImageView>(R.id.color_popup_IV)?.setBackgroundColor(it)
        }
        colorPopupView?.layoutParams = layoutParams
        colorPopupView?.visibility = View.VISIBLE
    }

    private fun hideColorPopup() {
        _binding?.colorPopupRL?.visibility = View.INVISIBLE
    }
}