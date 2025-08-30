package com.nishant.oralvisapp

import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nishant.oralvisapp.data.AppDatabase
import com.nishant.oralvisapp.data.ImageEntity
import com.nishant.oralvisapp.data.ImageWithSession
import com.nishant.oralvisapp.data.OralvisRepository
import com.nishant.oralvisapp.data.SessionEntity
import com.nishant.oralvisapp.databinding.ActivityGalleryBinding
import com.nishant.oralvisapp.viewmodel.ImageViewModel
import com.nishant.oralvisapp.viewmodel.ImageViewModelFactory
import com.nishant.oralvisapp.viewmodel.SessionViewModel
import com.nishant.oralvisapp.viewmodel.SessionViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class GalleryActivity : AppCompatActivity() {

    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var imageViewModel: ImageViewModel
    private lateinit var binding: ActivityGalleryBinding
    private lateinit var imageAdapter: ImageAdapter
    private val imageList = mutableListOf<ImageWithSession>()  // ✅ store full model

    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    // system camera launcher
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Add new image (details empty for now, filled after save)
            imageList.add(ImageWithSession(photoFile.absolutePath, "", "", ""))
            imageAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = systemBarsInsets.bottom + 16 // add extra space
            }
            insets
        }


        // Setup toolbar back button
        binding.galleryToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val db = AppDatabase.getDatabase(this)
        val repository = OralvisRepository(db.sessionDao(), db.imageDao())

        sessionViewModel = ViewModelProvider(
            this,
            SessionViewModelFactory(repository)
        )[SessionViewModel::class.java]

        imageViewModel = ViewModelProvider(
            this,
            ImageViewModelFactory(repository)
        )[ImageViewModel::class.java]

        // Get first image from intent (from MainActivity)
        intent.getStringExtra("imagePath")?.let { path ->
            imageList.add(ImageWithSession(path, "", "", "")) // temp, details filled later
        }

        // Setup RecyclerView
        imageAdapter = ImageAdapter(imageList) { item ->
            // remove image when ❌ pressed
            imageList.remove(item)
            File(item.imagePath).delete()
            imageAdapter.notifyDataSetChanged()
        }
        binding.recyclerViewImages.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerViewImages.adapter = imageAdapter

        // ➕ Add new image
        binding.btnAddImage.setOnClickListener {
            openSystemCamera()
        }

        // 🛑 Stop session
        binding.btnStopSession.setOnClickListener {
            showMetadataDialog()
        }
    }

    private fun openSystemCamera() {
        val baseDir = File(
            getExternalFilesDir(null)?.absolutePath
                ?.replace("Android/data/${packageName}/files", "Android/media/${packageName}"),
            "Sessions/TempSession"
        )

        if (!baseDir.exists()) {
            baseDir.mkdirs()
        }

        // ✅ File name with timestamp
        photoFile = File(
            baseDir,
            "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
        )

        // ✅ Get Uri for system camera
        photoUri = androidx.core.content.FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            photoFile
        )

        takePictureLauncher.launch(photoUri)
    }

    private fun showMetadataDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_metadata, null)
        val sessionIdEt = dialogView.findViewById<EditText>(R.id.etSessionId)
        val nameEt = dialogView.findViewById<EditText>(R.id.etName)
        val ageEt = dialogView.findViewById<EditText>(R.id.etAge)

        val dialog = MaterialAlertDialogBuilder(this, R.style.ShapeAppearanceOverlay_OralVis_RoundedDialog)
            .setTitle("End Session")
            .setView(dialogView)
            .setPositiveButton("Save", null) // set later for custom validation
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val sid = sessionIdEt.text.toString().trim()
                val uname = nameEt.text.toString().trim()
                val uage = ageEt.text.toString().trim()

                // ✅ Validation
                when {
                    sid.isEmpty() -> {
                        sessionIdEt.error = "Session ID cannot be blank"
                        sessionIdEt.requestFocus()
                    }
                    uname.isEmpty() -> {
                        nameEt.error = "Name cannot be blank"
                        nameEt.requestFocus()
                    }
                    uage.isEmpty() -> {
                        ageEt.error = "Age cannot be blank"
                        ageEt.requestFocus()
                    }
                    !uage.matches(Regex("^\\d+\$")) -> { // only digits allowed
                        ageEt.error = "Age must be numeric"
                        ageEt.requestFocus()
                    }
                    else -> {
                        // ✅ All valid, save session
                        val sessionEntity = SessionEntity(
                            sessionId = sid,
                            name = uname,
                            age = uage
                        )

                        sessionViewModel.insertSession(sessionEntity) { newSessionId ->
                            val baseDir = File(
                                getExternalFilesDir(null)?.absolutePath
                                    ?.replace("Android/data/${packageName}/files", "Android/media/${packageName}"),
                                "Sessions"
                            )
                            val tempDir = File(baseDir, "TempSession")
                            val finalDir = File(baseDir, sid)

                            if (!finalDir.exists()) finalDir.mkdirs()

                            tempDir.listFiles()?.forEach { file ->
                                val newFile = File(finalDir, file.name)
                                val moved = file.renameTo(newFile)
                                if (!moved) {
                                    file.copyTo(newFile, overwrite = true)
                                    file.delete()
                                }
                                if (newFile.exists()) {
                                    val imageEntity = ImageEntity(
                                        imagePath = newFile.absolutePath,
                                        sessionOwnerId = newSessionId.toInt()
                                    )
                                    imageViewModel.insertImage(imageEntity)
                                }
                            }

                            val updatedList = imageList.map {
                                it.copy(sessionId = sid, name = uname, age = uage)
                            }
                            imageAdapter.updateData(updatedList)
                            tempDir.deleteRecursively()
                        }

                        // ✅ Show Toast after saving
                        Toast.makeText(this, "Session saved successfully", Toast.LENGTH_SHORT).show()

                        dialog.dismiss()
                        finish()
                    }
                }
            }
        }

        dialog.show()
    }

}
