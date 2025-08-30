package com.nishant.oralvisapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nishant.oralvisapp.activities.ImageViewerActivity
import com.nishant.oralvisapp.data.ImageWithSession

class ImageAdapter(
    private var imageList: MutableList<ImageWithSession>,
    private val onRemove: (ImageWithSession) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {


    fun getImageList(): List<ImageWithSession> = imageList

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)
        val tvSessionId: TextView = itemView.findViewById(R.id.tvSessionId)
        val tvNameAge: TextView = itemView.findViewById(R.id.tvNameAge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun getItemCount() = imageList.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = imageList[position]


        Glide.with(holder.imageView.context)
            .load(item.imagePath)
            .centerCrop()
            .placeholder(android.R.color.darker_gray)
            .into(holder.imageView)


        holder.tvSessionId.text = "Session: ${item.sessionId}"
        holder.tvNameAge.text = "${item.name}, ${item.age}"

        // Handle remove
        holder.btnRemove.setOnClickListener { onRemove(item) }

        // Open image in app
        holder.imageView.setOnClickListener {
            val context = holder.imageView.context
            val intent = android.content.Intent(context, ImageViewerActivity::class.java)
            intent.putExtra("imagePath", item.imagePath)
            context.startActivity(intent)
        }
    }



    fun updateData(newList: List<ImageWithSession>) {
        val diffCallback = ImageDiffCallback(imageList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        imageList.clear()
        imageList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }


    class ImageDiffCallback(
        private val oldList: List<ImageWithSession>,
        private val newList: List<ImageWithSession>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

            return oldList[oldItemPosition].imagePath == newList[newItemPosition].imagePath
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
