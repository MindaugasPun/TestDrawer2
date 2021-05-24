package com.example.testdrawer2.reminder_data

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.testdrawer2.R
import com.example.testdrawer2.databinding.ItemReminderBinding
import java.io.File

lateinit var binding: ItemReminderBinding

class ReminderAdapter(val clickListener: ReminderClickListener, val context: Context, var reminders: MutableList<Reminder>?) :
    ListAdapter<Reminder, ReminderAdapter.ViewHolder>(ReminderDiffCallback()) {

    class ViewHolder(val binding: ItemReminderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reminder: Reminder, clickListener: ReminderClickListener) {
            binding.reminder = reminder
            binding.clickListener = clickListener
        }
        val image = binding.imgIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
        val reminder = reminders?.get(position)

        //SET IMAGE ONCE EVERYTHING HAS BEEN RENDERED
        holder.image.post {
            if (File(getFileById(reminder?.reminderId!!)).exists()){
                setPicFromFile(holder.binding.imgIcon, getFileById(reminder?.reminderId!!))
            }
        }

    }

    class ReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem.reminderId == newItem.reminderId
        }

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem == newItem
        }
    }


    class ReminderClickListener(val clickListener: (reminder: Reminder) -> Unit) {
        fun onClick(reminder: Reminder): Reminder {
            clickListener(reminder)
            return reminder
        }
    }

    fun setPicFromFile(imageView: ImageView, path: String) {
        // Get the dimensions of the View
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(path)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(path, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }
    fun getFileById(id: Int): String {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image: File = File(storageDir, "Reminder_$id.jpg")
        return image.toString()
    }
}