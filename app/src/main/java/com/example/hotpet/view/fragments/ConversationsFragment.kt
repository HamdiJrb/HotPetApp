package com.example.hotpet.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotpet.R
import com.example.hotpet.adapters.ConversationAdapter
import com.example.hotpet.api.models.Conversation
import com.example.hotpet.api.models.User
import com.example.hotpet.api.viewModels.ChatViewModel
import com.example.hotpet.utils.UserSession
import com.facebook.shimmer.ShimmerFrameLayout

class ConversationsFragment : Fragment() {

    // VARIABLES
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var userSession: User
    private var conversationAdapter: ConversationAdapter? = null
    private var conversationList: ArrayList<Conversation> = arrayListOf()
    private var conversationListAux: List<Conversation> = arrayListOf()

    // VIEWS
    private var searchView: SearchView? = null
    private var conversationsRV: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_conversations, container, false)

        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        userSession = UserSession.getSession(requireContext())

        // VIEW BINDING
        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout)
        conversationsRV = view.findViewById(R.id.conversationsRV)
        searchView = view.findViewById(R.id.searchView)

        searchView!!.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                conversationList.clear()
                if (newText != "") {
                    for (conversation in conversationListAux) {
                        println(newText)
                        if (conversation.receiver != null) {
                            if (conversation.receiver.username.lowercase()
                                    .startsWith(newText!!.lowercase())
                            ) {
                                conversationList.add(conversation)
                            }
                        }
                    }
                } else {
                    for (conversation in conversationListAux) {
                        conversationList.add(conversation)
                    }
                }
                conversationAdapter!!.notifyDataSetChanged()
                return true
            }
        })

        shimmerFrameLayout!!.startShimmer()
        conversationsRV!!.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        chatViewModel.getMyConversations(userSession._id!!)
        chatViewModel.conversationList.observe(viewLifecycleOwner) {
            conversationList = it as ArrayList<Conversation>
            conversationListAux = it.toList()
            conversationAdapter = ConversationAdapter(conversationList)
            conversationsRV!!.adapter = conversationAdapter
            shimmerFrameLayout!!.stopShimmer()
            shimmerFrameLayout!!.visibility = View.GONE
        }

        return view
    }
}