package com.example.colorchecker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.colorchecker.databinding.AnalysisFragmentBinding
import com.example.colorchecker.model.PhotoViewModel
import kotlinx.coroutines.NonDisposableHandle.parent

class AnalysisFragment: Fragment() {

    private val model: PhotoViewModel by activityViewModels()

    private lateinit var imageResultLauncher: ActivityResultLauncher<Intent>
    private var _binding: AnalysisFragmentBinding? = null

    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handleCameraImage(result.data)
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
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            imageResultLauncher.launch(cameraIntent)
        }

        _binding!!.photoIV.setOnTouchListener { _, motionEvent->  onTouchPhotoIV(motionEvent = motionEvent)}
        return binding?.root
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

    private fun handleCameraImage(intent: Intent?) {
        val bitmap = intent?.extras?.get("data") as Bitmap
        model.updateImageBitmap(bitmap)

    }

    //TODO: Set it up so that it only rotates when in portrait mode
    private fun onTouchPhotoIV(motionEvent: MotionEvent) : Boolean {
        showColorPopup(motionEvent.x, motionEvent.y)
        return false
    }

    private fun showColorPopup(x: Float, y: Float) {

        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(x.toInt(),y.toInt(),0,0)
        val colorPopupView = _binding?.colorPopup?.colorRL
        colorPopupView?.layoutParams = layoutParams
        colorPopupView?.visibility = View.VISIBLE
    }
}