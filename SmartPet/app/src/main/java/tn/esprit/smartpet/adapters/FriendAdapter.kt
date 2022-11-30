package tn.esprit.smartpet.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tn.esprit.smartpet.R
import tn.esprit.smartpet.model.Friend
import tn.esprit.smartpet.model.MESSAGE
import tn.esprit.smartpet.model.NAME
import tn.esprit.smartpet.model.PICTURE
import tn.esprit.smartpet.view.DiscussionActivity

class FriendAdapter (val friendsList: MutableList<Friend>) : RecyclerView.Adapter<FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.friend_single_item, parent, false)

        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {

        val name = friendsList[position].friendName
        val message = friendsList[position].friendMessage

        holder.friendPic.setImageResource(friendsList[position].friendPic)
        holder.friendName.text = name
        holder.friendMessage.text = message

        // Se diriger vers l'activité detailActivity : Discussion

        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, DiscussionActivity::class.java)
            intent.apply {
                putExtra(PICTURE, friendsList[position].friendPic)
                putExtra(NAME, name)
                putExtra(MESSAGE, message)
            }
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount() = friendsList.size

}