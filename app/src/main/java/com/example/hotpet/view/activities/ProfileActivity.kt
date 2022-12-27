package com.example.hotpet.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.hotpet.R
import com.example.hotpet.api.ChatService
import com.example.hotpet.api.LikeService
import com.example.hotpet.api.models.Like
import com.example.hotpet.api.models.User
import com.example.hotpet.api.viewModels.ChatViewModel
import com.example.hotpet.api.viewModels.LikeViewModel
import com.example.hotpet.utils.AlertMaker
import com.example.hotpet.utils.Constants
import com.example.hotpet.utils.DateUtils.Companion.getAge
import com.example.hotpet.utils.UserSession

class ProfileActivity : AppCompatActivity() {

    // VARIABLES
    private lateinit var likeViewModel: LikeViewModel
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var currentUser: User
    private lateinit var currentLike: Like
    private lateinit var userSession: User
    private var openConversation = false

    // VIEWS
    private var aboutTV: TextView? = null
    private var usernameTV: TextView? = null
    private var ageTV: TextView? = null
    private var genderTV: TextView? = null
    private var categoryTV: TextView? = null
    private var blockButton: Button? = null
    private var chatButton: Button? = null
    private var profilePictureIV: ImageView? = null

    // LIFECYCLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        likeViewModel = ViewModelProvider(this)[LikeViewModel::class.java]
        userSession = UserSession.getSession(this)

        // VIEW BINDING
        usernameTV = findViewById(R.id.usernameTV)
        aboutTV = findViewById(R.id.aboutTV)
        ageTV = findViewById(R.id.ageTV)
        genderTV = findViewById(R.id.genderTV)
        categoryTV = findViewById(R.id.categoryTV)
        blockButton = findViewById(R.id.blockButton)
        chatButton = findViewById(R.id.chatButton)
        profilePictureIV = findViewById(R.id.profilePictureIV)

        currentUser = intent.getSerializableExtra("user") as User
        currentLike = intent.getSerializableExtra("like") as Like

        setupObservers()
        initProfile()
    }
    // METHODS

    private fun setupObservers() {
        likeViewModel.method.observe(this) {
            if (it == "delete") {
                finish()
            } else if (it == "add") {
                AlertMaker.makeActionAlert(
                    this,
                    "Match !",
                    "You successfully matched with " + currentLike.liker?.username + " a new conversation has been created."
                ) {
                    chatViewModel.createNewConversation(
                        ChatService.ConversationBody(
                            UserSession.getSession(this@ProfileActivity)._id!!,
                            currentUser._id!!
                        )
                    )
                    chatViewModel.createNewConversation(
                        ChatService.ConversationBody(
                            currentUser._id!!,
                            UserSession.getSession(this@ProfileActivity)._id!!
                        )
                    )

                    currentLike.isMatch = true
                    finish()
                }
            }
        }

        chatViewModel.conversation.observe(this) {
            if (openConversation) {
                openConversation = false
                val intent = Intent(this@ProfileActivity, ChatActivity::class.java)
                intent.putExtra("conversation", it)
                startActivity(intent)
            }
        }
    }

    private fun initProfile() {

        usernameTV!!.text = getString(R.string.username_profile, currentUser.username)
        aboutTV!!.text = getString(R.string.about_profile, userSession.about)
        ageTV!!.text = getString(R.string.age_profile, getAge(currentUser.birthdate).toString())
        genderTV!!.text = getString(R.string.gender_profile, currentUser.gender)
        categoryTV!!.text = getString(R.string.category_profile, currentUser.category)

        Glide.with(this).load(Constants.BASE_URL_IMAGES + currentUser.imageFilename)
            .into(profilePictureIV!!)

        setupButtons()
    }

    private fun setupButtons() {
        blockButton!!.setOnClickListener {
            likeViewModel.delete(currentLike._id!!)
        }

        if (!currentLike.isMatch) {
            chatButton!!.text = getString(R.string.like)
            chatButton!!.setCompoundDrawables(
                let { null },
                let { null },
                let { null },
                let { null }
            )
            chatButton!!.setOnClickListener {
                likeViewModel.add(
                    LikeService.LikeBody(
                        currentLike.liker!!._id!!, currentLike.liked!!._id!!, true
                    )
                )
            }
        } else {
            chatButton!!.setOnClickListener {
                openConversation = true
                chatViewModel.createNewConversation(
                    ChatService.ConversationBody(
                        UserSession.getSession(this@ProfileActivity)._id!!,
                        currentUser._id!!
                    )
                )
            }
        }
    }
}