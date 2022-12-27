package com.example.hotpet.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hotpet.R
import com.example.hotpet.api.models.Conversation
import com.example.hotpet.api.models.Message
import com.example.hotpet.utils.Constants
import com.example.hotpet.utils.UserSession

class MessageAdapter(
    private var items: MutableList<Message>,
    private var conversation: Conversation
) :
    RecyclerView.Adapter<MessageAdapter.ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_message, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bindView(items[position], conversation)
    }

    override fun getItemCount(): Int = items.size

    class ConversationViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        // VIEW BINDING
        private val lastMessageTV: TextView = itemView.findViewById(R.id.descriptionTV)
        private val profilePictureIV: ImageView = itemView.findViewById(R.id.profilePictureIV)
        private val usernameTV: TextView = itemView.findViewById(R.id.usernameTV)

        fun bindView(message: Message, conversation: Conversation) {

            if (message.senderConversation.sender != null && conversation.sender != null && conversation.receiver != null) {

                if (message.senderConversation.sender._id == UserSession.getSession(itemView.context)._id) {
                    (itemView as LinearLayout).gravity = Gravity.END

                    usernameTV.text = conversation.sender.username
                    Glide.with(itemView.context)
                        .load(Constants.BASE_URL_IMAGES + conversation.sender.imageFilename)
                        .into(profilePictureIV)
                } else {
                    usernameTV.text = conversation.receiver.username
                    Glide.with(itemView.context)
                        .load(Constants.BASE_URL_IMAGES + conversation.receiver.imageFilename)
                        .into(profilePictureIV)
                }
            }

            itemView.setOnClickListener {

            }

            lastMessageTV.text = message.description

        }
    }
}