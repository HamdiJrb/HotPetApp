package tn.esprit.smartpet.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import tn.esprit.smartpet.R
import tn.esprit.smartpet.model.MESSAGE
import tn.esprit.smartpet.model.NAME
import tn.esprit.smartpet.model.PICTURE

class DiscussionActivity : AppCompatActivity() {

    lateinit var friendPic : ImageView
    lateinit var friendName : TextView
    lateinit var friendMessage : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discussion)

        friendPic = findViewById(R.id.friendPic)
        friendName = findViewById(R.id.friendName)
        friendMessage = findViewById(R.id.friendMessage)


        val name = intent.getStringExtra(NAME)
        val message = intent.getStringExtra(MESSAGE)

        friendPic.setImageResource(intent.getIntExtra(PICTURE, 0))
        friendName.text = "Name : $name"
        friendMessage.text = "Message : $message"

    }
}