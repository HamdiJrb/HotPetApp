package tn.esprit.smartpet.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tn.esprit.smartpet.R

class FriendViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    val friendPic : ImageView
    val friendName : TextView
    val friendMessage : TextView = itemView.findViewById<TextView>(R.id.friendMessage)

    init {
        friendPic = itemView.findViewById<ImageView>(R.id.friendPic)
        friendName = itemView.findViewById<TextView>(R.id.friendName)
    }

}