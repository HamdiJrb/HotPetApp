package com.example.hotpet.view.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.hotpet.R
import com.example.hotpet.adapters.ImageAdapter
import com.example.hotpet.api.UserService
import com.example.hotpet.api.models.User
import com.example.hotpet.api.viewModels.UserViewModel
import com.example.hotpet.utils.Constants
import com.example.hotpet.utils.DateUtils.Companion.getAge
import com.example.hotpet.utils.URIPathHelper
import com.example.hotpet.utils.UserSession
import com.example.hotpet.utils.createPartFromString
import com.example.hotpet.view.activities.EditProfileActivity
import com.example.hotpet.view.activities.LoginActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class ProfileFragment : Fragment() {

    // VARIABLES
    private lateinit var bottomDialog: BottomSheetDialog
    private lateinit var userViewModel: UserViewModel
    private lateinit var userSession: User
    private lateinit var imageAdapter: ImageAdapter
    private var pictureUri: Uri? = null
    private val galleryResultCode = 2

    // VIEWS
    private var aboutTV: TextView? = null
    private var usernameTV: TextView? = null
    private var ageTV: TextView? = null
    private var genderTV: TextView? = null
    private var categoryTV: TextView? = null
    private var logoutButton: Button? = null
    private var editProfileButton: Button? = null
    private var profilePictureIV: ImageView? = null
    private var imagesRV: RecyclerView? = null
    private var addImageButton: Button? = null

    // LIFECYCLE

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userSession = UserSession.getSession(requireContext())

        // VIEW BINDING
        usernameTV = view.findViewById(R.id.usernameTV)
        aboutTV = view.findViewById(R.id.aboutTV)
        ageTV = view.findViewById(R.id.ageTV)
        genderTV = view.findViewById(R.id.genderTV)
        categoryTV = view.findViewById(R.id.categoryTV)
        logoutButton = view.findViewById(R.id.logoutButton)
        editProfileButton = view.findViewById(R.id.editProfileButton)
        profilePictureIV = view.findViewById(R.id.profilePictureIV)
        imagesRV = view.findViewById(R.id.imagesRV)
        addImageButton = view.findViewById(R.id.addImageButton)

        bottomDialog = BottomSheetDialog(requireContext())

        userViewModel.user.observe(viewLifecycleOwner) {
            userSession = it
            initProfile()
        }

        userViewModel.method.observe(viewLifecycleOwner) {
            if (it.equals("deleteImage")) {
                Toast.makeText(context, "Picture deleted !", Toast.LENGTH_SHORT).show()
                userSession.images.remove(userViewModel.image.value)
                userViewModel.getById(userSession._id!!)
                imageAdapter.notifyDataSetChanged()
                bottomDialog.dismiss()
            } else if (it.equals("updateProfileImage")) {
                Toast.makeText(context, "Profile picture updated !", Toast.LENGTH_SHORT).show()
                userViewModel.getById(userSession._id!!)
                bottomDialog.dismiss()
            } else if (it.equals("addImage")) {
                if (!userSession.images.contains(userViewModel.image.value!!)) {
                    userSession.images.add(userViewModel.image.value!!)
                }
                imageAdapter.notifyDataSetChanged()
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        userViewModel.getById(userSession._id!!)
    }

    // METHODS

    private fun initProfile() {

        usernameTV!!.text = getString(R.string.username_profile, userSession.username)
        aboutTV!!.text = getString(R.string.about_profile, userSession.about)
        ageTV!!.text =
            getString(R.string.age_profile, getAge(userSession.birthdate).toString())
        genderTV!!.text = getString(R.string.gender_profile, userSession.gender)
        categoryTV!!.text = getString(R.string.category_profile, userSession.category)

        Glide.with(this)
            .asBitmap()
            .load(Constants.BASE_URL_IMAGES + userSession.imageFilename)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    profilePictureIV!!.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })

        if (context != null) {
            imagesRV!!.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            imageAdapter = ImageAdapter(userSession.images, showMenu)
            imagesRV!!.adapter = imageAdapter
        }

        setupButtons()
    }

    private val showMenu = fun(image: String) {
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)

        val setAsProfileButton = view.findViewById<Button>(R.id.setAsProfileButton)
        setAsProfileButton.setOnClickListener {
            userViewModel.updateProfileImage(
                UserService.UpdateImageBody(
                    UserSession.getSession(requireContext()).email,
                    image
                )
            )
        }

        val deleteImageButton = view.findViewById<Button>(R.id.deleteImageButton)
        deleteImageButton.setOnClickListener {
            userViewModel.deleteImage(
                UserService.DeleteImageBody(
                    UserSession.getSession(requireContext()).email,
                    image
                )
            )
        }

        val closeButton = view.findViewById<Button>(R.id.closeButton)
        closeButton.setOnClickListener { bottomDialog.dismiss() }

        bottomDialog.setContentView(view)
        bottomDialog.show()
    }

    private fun setupButtons() {
        logoutButton!!.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle(getString(R.string.logout))
            builder.setMessage(R.string.logout_message)
            builder.setPositiveButton("Yes") { _, _ ->
                logout()
            }
            builder.setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            builder.create().show()
        }

        editProfileButton!!.setOnClickListener {
            startActivity(Intent(context, EditProfileActivity::class.java))
        }

        addImageButton!!.setOnClickListener {
            if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                requireContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {
                openSystemGallery()
            } else {
                @Suppress("DEPRECATION")
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    123
                )
            }
        }
    }

    private fun openSystemGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")

        try {
            @Suppress("DEPRECATION")
            startActivityForResult(intent, galleryResultCode)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "No Gallery APP installed",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == galleryResultCode) {
            pictureUri = data?.data ?: return
            @Suppress("DEPRECATION")
            val pictureBitmap = MediaStore.Images.Media.getBitmap(
                requireContext().contentResolver,
                Uri.parse(pictureUri.toString())
            )

            var multipartImage: MultipartBody.Part? = null
            if (pictureUri != null) {
                val pathFromUri = pictureUri?.let { URIPathHelper().getPath(requireContext(), it) }
                val file = File(pathFromUri!!)
                val requestFile: RequestBody = file.asRequestBody("image/jpg".toMediaType())
                multipartImage = MultipartBody.Part.createFormData("image", file.name, requestFile)
            } else {
                println("Image null")
            }

            val map: MutableMap<String, RequestBody> = mutableMapOf()
            map["email"] = createPartFromString(userSession.email)
            userViewModel.addImage(map, multipartImage)
        }

        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun logout() {
        UserSession.removeSession(requireContext())
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}