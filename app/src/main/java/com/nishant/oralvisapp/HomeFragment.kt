package com.nishant.oralvisapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nishant.oralvisapp.databinding.FragmentHomeBinding
import java.io.File

import android.Manifest

import android.content.pm.PackageManager

import androidx.core.content.FileProvider
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private lateinit var binding: FragmentHomeBinding

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) openSystemCamera()
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                val intent = Intent(requireContext(), GalleryActivity::class.java)
                intent.putExtra("imagePath", photoFile.absolutePath)
                startActivity(intent)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnStartSession.setOnClickListener { checkCameraPermission() }

        return binding.root
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> openSystemCamera()
            else -> requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openSystemCamera() {
        val baseDir = File(
            requireContext().getExternalFilesDir(null)?.absolutePath
                ?.replace("Android/data/${requireContext().packageName}/files", "Android/media/${requireContext().packageName}"),
            "Sessions/TempSession"
        )
        if (!baseDir.exists()) baseDir.mkdirs()

        photoFile = File(
            baseDir, "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
        )

        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )

        takePictureLauncher.launch(photoUri)
    }
}
