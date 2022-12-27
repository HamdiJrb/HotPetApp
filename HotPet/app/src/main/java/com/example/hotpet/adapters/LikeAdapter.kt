package com.example.hotpet.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hotpet.R
import com.example.hotpet.api.models.Like
import com.example.hotpet.api.models.User
import com.example.hotpet.api.viewModels.LikeViewModel
import com.example.hotpet.utils.Constants
import com.example.hotpet.view.activities.ProfileActivity

class LikeAdapter(private var items: ArrayList<Like>, private var userSession: User) :
    RecyclerView.Adapter<LikeAdapter.LikeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_like, parent, false)
        return LikeViewHolder(view, userSession, deleteItem)
    }

    override fun onBindViewHolder(holder: LikeViewHolder, position: Int) {
        holder.bindView(items[position])
    }

    override fun getItemCount(): Int = items.size

    private val deleteItem = fun(like: Like) {
        items.remove(like)
        this.notifyDataSetChanged()
    }

    class LikeViewHolder(view: View, val userSession: User, val deleteFunction: (Like) -> Unit) :
        RecyclerView.ViewHolder(view) {

        // VIEW BINDING
        private val profilePictureIV: ImageView = itemView.findViewById(R.id.profilePictureIV)
        private val usernameTV: TextView = itemView.findViewById(R.id.usernameTV)
        private val aboutTV: TextView = itemView.findViewById(R.id.aboutTV)
        private val deleteLikeButton: Button = itemView.findViewById(R.id.deleteLikeButton)

        fun bindView(like: Like) {

            val user: User? = if (like.liked?._id == userSession._id) {
                like.liker
            } else {
                like.liked
            }

            if (user != null) {
                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, ProfileActivity::class.java)
                    intent.putExtra("user", user)
                    intent.putExtra("like", like)
                    itemView.context.startActivity(intent)
                }

                Glide.with(itemView.context)
                    .load(Constants.BASE_URL_IMAGES + user.imageFilename)
                    .into(profilePictureIV)

                usernameTV.text = user.username
                aboutTV.text = user.about
            }

            deleteLikeButton.setOnClickListener {
                val likeViewModel =
                    ViewModelProvider(itemView.findViewTreeViewModelStoreOwner()!!)[LikeViewModel::class.java]
                likeViewModel.delete(like._id!!)
                likeViewModel.likeList.observe(itemView.findViewTreeLifecycleOwner()!!) {
                    deleteFunction(like)
                }
            }
        }
    }
}