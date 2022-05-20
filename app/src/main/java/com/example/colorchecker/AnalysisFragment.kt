package com.example.colorchecker

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.colorchecker.databinding.AnalysisFragmentBinding
import com.example.colorchecker.model.PhotoViewModel

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
        val view = binding?.root
        return view
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

}