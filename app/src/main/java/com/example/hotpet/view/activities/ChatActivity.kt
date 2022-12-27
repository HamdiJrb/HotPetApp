package com.example.hotpet.view.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hotpet.R
import com.example.hotpet.adapters.MessageAdapter
import com.example.hotpet.api.ChatService
import com.example.hotpet.api.models.Conversation
import com.example.hotpet.api.models.Message
import com.example.hotpet.api.models.User
import com.example.hotpet.api.viewModels.ChatViewModel
import com.example.hotpet.utils.Constants
import com.example.hotpet.utils.UserSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    // VARIABLES
    private lateinit var chatViewModel: ChatViewModel
    private var messagesList: MutableList<Message> = arrayListOf()
    private lateinit var currentConversation: Conversation
    private lateinit var messageAdapter: MessageAdapter

    // VIEWS
    private var chatRV: RecyclerView? = null
    private var sendIB: ImageButton? = null
    private var messageET: EditText? = null
    private var profilePictureIV: ImageView? = null
    private var usernameTV: TextView? = null
    private var aboutTV: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        // VIEW BINDING
        chatRV = findViewById(R.id.chatRV)
        sendIB = findViewById(R.id.sendIB)
        messageET = findViewById(R.id.messageET)
        profilePictureIV = findViewById(R.id.profilePictureIV)
        usernameTV = findViewById(R.id.usernameTV)
        aboutTV = findViewById(R.id.aboutTV)

        currentConversation = intent.getSerializableExtra("conversation") as Conversation

        if (currentConversation.receiver != null ) {
            val currentUser: User = currentConversation.receiver!!
            val linearLayoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            chatRV!!.layoutManager = linearLayoutManager

            initConversation()
            startUpdates()

            sendIB!!.setOnClickListener {
                chatViewModel.sendMessage(
                    ChatService.MessageBody(
                        messageET!!.text.toString(),
                        UserSession.getSession(this@ChatActivity)._id!!,
                        currentUser._id!!
                    )
                )
                chatViewModel.state.observe(this) {
                    if (it) {
                        messageET!!.setText("")
                        getData()
                    }
                }
            }
        }
    }

    private fun initConversation() {
        if (currentConversation.receiver != null ) {

            Glide.with(baseContext)
                .load(Constants.BASE_URL_IMAGES + currentConversation.receiver!!.imageFilename)
                .into(profilePictureIV!!)

            usernameTV!!.text = currentConversation.receiver!!.username
            aboutTV!!.text = currentConversation.receiver!!.about
        }
    }

    private fun getData() {
        chatViewModel.getMyMessages(currentConversation._id!!)
        chatViewModel.messageList.observe(this) {
            messagesList = it as MutableList<Message>
            messageAdapter = MessageAdapter(messagesList, currentConversation)
            chatRV!!.adapter = messageAdapter
            chatRV!!.scrollToPosition(messagesList.size - 1)
        }
    }

    private val scope = MainScope()
    private var job: Job? = null

    private fun startUpdates() {
        job = scope.launch {
            while (true) {
                getData()
                delay(5000)
            }
        }
    }

    private fun stopUpdates() {
        job?.cancel()
        job = null
    }

    override fun finish() {
        super.finish()
        stopUpdates()
    }
}