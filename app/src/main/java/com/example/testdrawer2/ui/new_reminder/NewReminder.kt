package com.example.testdrawer2.ui.new_reminder

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.testdrawer2.R
import com.example.testdrawer2.databinding.NewReminderFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule


@Suppress("DEPRECATION")
class NewReminder : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = NewReminderFragmentBinding.inflate(inflater, container, false)
        val viewModel: NewReminderViewModel by viewModels {
            NewReminderViewModelFactory(
                requireContext()
            )
        }
        binding.lifecycleOwner = viewLifecycleOwner
        val args = NewReminderArgs.fromBundle(requireArguments())


        //IF ITEM PRESSED - FILL OUT FIELDS
        if (args.Id != 0) {
            viewModel.getReminder(args.Id)
            viewModel.reminder.observe(viewLifecycleOwner, Observer() { reminder ->
                binding.edtTitle.setText(reminder.title)
                binding.edtDay.setText(reminder.day.toString())
                binding.edtMonth.setText(reminder.month.toString())
                binding.edtYear.setText(reminder.year.toString())
                binding.edtDescription.setText(reminder.description)

                setPicFromFile(binding.imgNewReminder, getFileById(args.Id))
            })
        }

        //SAVE
        binding.btnSave.setOnClickListener {
            if (args.Id == 0) {
                viewModel.addReminder(
                    Id = args.Id,
                    binding.edtTitle.text.toString(),
                    binding.edtYear.text.toString(),
                    binding.edtMonth.text.toString(),
                    binding.edtDay.text.toString(),
                    binding.edtDescription.text.toString(),
                )
//                Log.d("SAVE:", "observed?")
//                viewModel.insert.observe(viewLifecycleOwner, {
//                    Log.d("SAVE:", it.toString())
//                })
                viewModel.insert.observe(viewLifecycleOwner, { insert ->
                    val newId = insert.toString()
                    Log.d("SAVE:", "observed?")
                    Log.d("SAVE:", newId)
                    if (::currentPhotoPath.isInitialized) {
                        createReminderImage(currentPhotoPath, newId)
                    }
                    view?.findNavController()?.navigate(R.id.action_newReminder_to_nav_home)
                })

            } else {
                viewModel.updateReminder(
                    Id = args.Id,
                    binding.edtTitle.text.toString(),
                    binding.edtYear.text.toString(),
                    binding.edtMonth.text.toString(),
                    binding.edtDay.text.toString(),
                    binding.edtDescription.text.toString(),
                )
                //CREATE img for reminder when editing and go back
                if (::currentPhotoPath.isInitialized) {
                    if (File(getFileById(args.Id)).exists()){
                        File(getFileById(args.Id)).delete()
                    }
                    createReminderImage(currentPhotoPath, args.Id.toString())
                }
                view?.findNavController()?.navigate(R.id.action_newReminder_to_nav_home)
            }
        }


        //CLEAR fields OR DELETE object
        binding.btnDelete.setOnClickListener {
            if (args.Id == 0) {
                binding.edtTitle.setText("")
                binding.edtYear.setText("")
                binding.edtMonth.setText("")
                binding.edtDay.setText("")
                binding.edtDescription.setText("")

            } else {
                if (File(getFileById(args.Id)).exists()){
                    File(getFileById(args.Id)).delete()
                }
                viewModel.deleteReminder()
                view?.findNavController()?.navigate(R.id.action_newReminder_to_nav_home)
            }
        }

        //PHOTO
        binding.btnPhoto.setOnClickListener {
            if (haveStoragePermission()) {
//                var tempImageUri = requestCapture()
//                loadImgUri(tempImageUri)
                dispatchTakePictureIntent()
            } else {
                requestStoragePermission()
            }
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::currentPhotoPath.isInitialized) {
            File(currentPhotoPath).delete()
        }
    }

    /**------------------------------------------------------------------------------------------*/
    val REQUEST_IMAGE_CAPTURE = 1
    private val WRITE_EXTERNAL_STORAGE_REQUEST = 2

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                setPic(view?.findViewById(R.id.img_new_reminder)!!)
            }
        }
    }

    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image: File = File(storageDir, "TempImage" + ".jpg")
        return image.apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            Log.d("SAVE:", currentPhotoPath)
        }
    }
    private fun getFileById(id: Int): String {
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image: File = File(storageDir, "Reminder_$id.jpg")
        return image.toString()
    }
    private fun createReminderImage(tempFile: String, newFile: String) {
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        File(tempFile).copyTo(File(storageDir, "Reminder_$newFile.jpg"))
    }

    private fun dispatchTakePictureIntent() {
        val context: Context = requireContext()//default - activity
        val packageManager = context.packageManager

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context, "com.example.testdrawer2.fileprovider", it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun setPic(imageView: ImageView) {
        // Get the dimensions of the View
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }
    private fun setPicFromFile(imageView: ImageView, path: String) {
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

    private fun haveStoragePermission(): Boolean {
        // Devices with version Q+ can read and write external
        // files that belong to the app without a permission.
        // Devices with lower versions need to ask for permission
        // to write these files.
        // If you want to access images that other apps created,
        // Q+ devices would also need to request the permission
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return ActivityCompat.checkSelfPermission(
                requireActivity().baseContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    private fun requestStoragePermission() {
        if (!haveStoragePermission()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_REQUEST
            )
        }
    }

    private fun showPermissionRequestRationale() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.storage_permission_rationale_title))
            .setMessage(getString(R.string.storage_permission_rationale))
            .setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    requestStoragePermission()
                })
            .setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                if (shouldShowRationale) {
                    showPermissionRequestRationale()
                } else {
                    Snackbar.make(
                        this.requireView(),
                        R.string.storage_permission_not_granted,
                        Snackbar.LENGTH_LONG
                    ).setAction(
                        getString(R.string.open_settings)
                    ) {
                        showSettings()
                    }.show()
                }
            }
        }
    }

    private fun showSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${activity?.packageName}")
        ).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            startActivity(intent)
        }
    }

}