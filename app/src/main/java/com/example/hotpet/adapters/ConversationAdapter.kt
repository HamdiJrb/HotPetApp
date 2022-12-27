package com.example.hotpet.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hotpet.R
import com.example.hotpet.api.models.Conversation
import com.example.hotpet.api.viewModels.ChatViewModel
import com.example.hotpet.utils.Constants
import com.example.hotpet.view.activities.ChatActivity

class ConversationAdapter(private var items: ArrayList<Conversation>) :
    RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_conversation, parent, false)
        return ConversationViewHolder(view, deleteItem)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bindView(items[position])
    }

    override fun getItemCount(): Int = items.size

    private val deleteItem = fun(conversation: Conversation) {
        items.remove(conversation)
        this.notifyDataSetChanged()
    }

    class ConversationViewHolder(view: View, val deleteFunction: (Conversation) -> Unit) :
        RecyclerView.ViewHolder(view) {

        // VIEW BINDING
        private val profilePictureIV: ImageView = itemView.findViewById(R.id.profilePictureIV)
        private val conversationNameTV: TextView = itemView.findViewById(R.id.titleTV)
        private val lastMessageTV: TextView = itemView.findViewById(R.id.descriptionTV)
        private val deleteConversationButton: TextView =
            itemView.findViewById(R.id.deleteConversationButton)

        fun bindView(conversation: Conversation) {

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("conversation", conversation)
                itemView.context.startActivity(intent)
            }

            if (conversation.receiver != null) {
                Glide.with(itemView.context)
                    .load(Constants.BASE_URL_IMAGES + conversation.receiver.imageFilename)
                    .into(profilePictureIV)

                conversationNameTV.text = conversation.receiver.username
            }

            lastMessageTV.text = conversation.lastMessage

            deleteConversationButton.setOnClickListener {
                val chatViewModel =
                    ViewModelProvider(itemView.findViewTreeViewModelStoreOwner()!!)[ChatViewModel::class.java]
                chatViewModel.deleteConversation(conversation._id!!)
                chatViewModel.loading.observe(itemView.findViewTreeLifecycleOwner()!!) {
                    deleteFunction(conversation)
                }
            }
        }
    }
}