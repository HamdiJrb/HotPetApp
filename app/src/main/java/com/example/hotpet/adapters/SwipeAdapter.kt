package com.example.hotpet.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.example.hotpet.R
import com.example.hotpet.api.models.User
import com.example.hotpet.utils.Constants
import com.example.hotpet.utils.Converter
import com.example.hotpet.utils.DateUtils

class SwipeAdapter(context: Context, resource: Int, private val objects: List<User>) :
    ArrayAdapter<User>(context, resource, objects) {

    override fun getItemId(position: Int): Long {
        val user: User = objects[position]
        println(Converter.convert(user._id!!))
        return Converter.convert(user._id)
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.single_swipe, parent, false)!!
        }

        val user: User = getItem(position)!!

        val usernameTV = convertView.findViewById<TextView>(R.id.usernameTV)
        val aboutTV = convertView.findViewById<TextView>(R.id.aboutTV)
        val ageTV = convertView.findViewById<TextView>(R.id.ageTV)
        val profilePictureIV = convertView.findViewById<ImageView>(R.id.profilePictureIV)
        val previousImageButton = convertView.findViewById<ImageButton>(R.id.previousImageButton)
        val nextImageButton = convertView.findViewById<ImageButton>(R.id.nextImageButton)

        usernameTV.text = user.username
        aboutTV.text = user.about
        ageTV.text = DateUtils.getAge(user.birthdate).toString()
        profilePictureIV.scaleType = ImageView.ScaleType.CENTER_CROP

        if (user.images.count() > 1) {
            val images = arrayListOf<String>()
            val currentImage = user.imageFilename
            var index = 0

            images.add(currentImage)
            Glide.with(context).load(Constants.BASE_URL_IMAGES + currentImage)
                .into(profilePictureIV)

            for (image in user.images) {
                if (image != currentImage) {
                    images.add(image)
                }
            }

            disablePreviousButton(context, previousImageButton)

            previousImageButton.setOnClickListener {
                index--

                Glide.with(context).load(Constants.BASE_URL_IMAGES + images[index])
                    .into(profilePictureIV)

                enableNextButton(context, nextImageButton)

                if (index == 0) {
                    disablePreviousButton(context, previousImageButton)
                } else {
                    enablePreviousButton(context, previousImageButton)
                }
            }

            nextImageButton.setOnClickListener {
                index++

                Glide.with(context).load(Constants.BASE_URL_IMAGES + images[index])
                    .into(profilePictureIV)

                enablePreviousButton(context, previousImageButton)
                if (index == images.count() - 1) {
                    disableNextButton(context, nextImageButton)
                } else {
                    enableNextButton(context, nextImageButton)
                }
            }
        } else {
            disableNextButton(context, nextImageButton)
            disablePreviousButton(context, previousImageButton)
            Glide.with(context).load(Constants.BASE_URL_IMAGES + user.imageFilename)
                .into(profilePictureIV)

        }

        return convertView
    }

    private fun disablePreviousButton(context: Context, button: ImageButton) {
        button.isEnabled = false
        button.setBackgroundColor(context.getColor(R.color.transparent))
        button.setImageDrawable(null)
    }

    private fun enablePreviousButton(context: Context, button: ImageButton) {
        button.isEnabled = true
        button.background = AppCompatResources.getDrawable(context, R.drawable.gradient_left)
        button.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_arrow_back))
    }

    private fun disableNextButton(context: Context, button: ImageButton) {
        button.isEnabled = false
        button.setBackgroundColor(context.getColor(R.color.transparent))
        button.setImageDrawable(null)
    }

    private fun enableNextButton(context: Context, button: ImageButton) {
        button.isEnabled = true
        button.background = AppCompatResources.getDrawable(context, R.drawable.gradient_right)
        button.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_arrow_next))
    }
}