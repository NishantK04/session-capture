package com.nishant.oralvisapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nishant.oralvisapp.data.AppDatabase
import com.nishant.oralvisapp.data.ImageWithSession
import com.nishant.oralvisapp.data.OralvisRepository
import com.nishant.oralvisapp.databinding.FragmentSearchBinding
import com.nishant.oralvisapp.viewmodel.ImageViewModel
import com.nishant.oralvisapp.viewmodel.ImageViewModelFactory

class SearchFragment : Fragment() {


    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ImageAdapter

    private val imageViewModel: ImageViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        val repo = OralvisRepository(db.sessionDao(), db.imageDao())
        ImageViewModelFactory(repo)
    }

    private var currentObserverQuery: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        // 🔹 Adapter with delete callback
        adapter = ImageAdapter(mutableListOf()) { imageWithSession ->
            MaterialAlertDialogBuilder(requireContext(), R.style.ShapeAppearanceOverlay_OralVis_RoundedDialog)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Yes") { _, _ ->
                    val file = java.io.File(imageWithSession.imagePath)
                    if (file.exists()) file.delete()

                    imageViewModel.getAllImages().observe(viewLifecycleOwner) { allImages ->
                        val imageEntity = allImages.find { it.imagePath == imageWithSession.imagePath }
                        imageEntity?.let { imageViewModel.deleteImage(it) }
                    }

                    val currentList = adapter.getImageList().toMutableList()
                    currentList.remove(imageWithSession)
                    updateRecyclerView(currentList)
                }
                .setNegativeButton("No", null)
                .show()
        }

        binding.recyclerViewSearchImages.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerViewSearchImages.adapter = adapter

        // 🔹 Initially show progress bar
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerViewSearchImages.visibility = View.GONE
        binding.ivEmptyState.visibility = View.GONE

        // 🔹 Load all images
        imageViewModel.getAllImagesWithSession().observe(viewLifecycleOwner) { list ->
            binding.progressBar.visibility = View.GONE
            binding.recyclerViewSearchImages.visibility = if (list.isNotEmpty()) View.VISIBLE else View.GONE
            updateRecyclerView(list)
        }

        // 🔹 Search filter
        binding.etSearchSession.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query == currentObserverQuery) return
                currentObserverQuery = query

                // Show progress while filtering
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerViewSearchImages.visibility = View.GONE
                binding.ivEmptyState.visibility = View.GONE

                val liveData = if (query.isEmpty()) {
                    imageViewModel.getAllImagesWithSession()
                } else {
                    imageViewModel.getImagesWithSessionPrefix(query)
                }

                liveData.observe(viewLifecycleOwner) { list ->
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewSearchImages.visibility = if (list.isNotEmpty()) View.VISIBLE else View.GONE
                    updateRecyclerView(list)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return binding.root
    }

    // 🔹 Function to update RecyclerView & empty state
    fun updateRecyclerView(list: List<ImageWithSession>) {
        adapter.updateData(list)
        binding.ivEmptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
