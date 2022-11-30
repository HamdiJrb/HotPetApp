package tn.esprit.smartpet.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tn.esprit.smartpet.R
import tn.esprit.smartpet.adapters.FriendAdapter
import tn.esprit.smartpet.databinding.FragmentChatBinding
import tn.esprit.smartpet.model.Friend
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment() {

    lateinit var recylcerFriend: RecyclerView
    lateinit var recylcerFriendAdapter: FriendAdapter
    var friendList : MutableList<Friend> = ArrayList()
    var displayList : MutableList<Friend> = ArrayList()
    lateinit var searchView: SearchView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val rootView = inflater.inflate(R.layout.fragment_chat, container, false)

        recylcerFriend = rootView.findViewById(R.id.recyclerFriend)


        friendList.add(Friend(friendPic = R.drawable.b_bella, friendName = "Bella", friendMessage = "Hi how are you ?"))
        friendList.add(Friend(friendPic = R.drawable.b_lisa, friendName = "Lisa", friendMessage = "Wanna hang out ?"))
        friendList.add(Friend(friendPic = R.drawable.b_lora, friendName = "Lora", friendMessage = "Beautiful"))
        friendList.add(Friend(friendPic = R.drawable.b_luna, friendName = "Luna", friendMessage = "Nice pi"))
        friendList.add(Friend(friendPic = R.drawable.b_sophie, friendName = "Sophie", friendMessage = "Call me later!"))
        friendList.add(Friend(friendPic = R.drawable.b_bella, friendName = "Bella", friendMessage = "Hi how are you ?"))
        friendList.add(Friend(friendPic = R.drawable.b_lisa, friendName = "Lisa", friendMessage = "Wanna hang out ?"))
        friendList.add(Friend(friendPic = R.drawable.b_lora, friendName = "Lora", friendMessage = "Beautiful"))
        friendList.add(Friend(friendPic = R.drawable.b_luna, friendName = "Luna", friendMessage = "Nice pi"))
        friendList.add(Friend(friendPic = R.drawable.b_sophie, friendName = "Sophie", friendMessage = "Call me later!"))

        displayList.addAll(friendList)

        recylcerFriendAdapter = FriendAdapter(displayList)
        recylcerFriend.adapter = recylcerFriendAdapter


        /*searchView.rootView.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    displayList.clear()
                    var search = newText.toLowerCase(Locale.getDefault())

                    for (friend in friendList){
                        if (friend.toString().toLowerCase(Locale.getDefault()).contains(search)){
                            displayList.add(friend)
                        }
                        recylcerFriend.adapter!!.notifyDataSetChanged()
                    }
                }else{
                    displayList.clear()
                    displayList.addAll(friendList)
                    recylcerFriend.adapter!!.notifyDataSetChanged()
                }
                return true
            }
        })*/

        recylcerFriend.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL ,false)

        return rootView
    }



}