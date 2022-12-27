package com.example.hotpet.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotpet.R
import com.example.hotpet.adapters.LikeAdapter
import com.example.hotpet.api.models.Like
import com.example.hotpet.api.models.User
import com.example.hotpet.api.viewModels.LikeViewModel
import com.example.hotpet.utils.UserSession
import com.facebook.shimmer.ShimmerFrameLayout

class MatchesFragment : Fragment() {

    // VARIABLES
    private lateinit var likeViewModel: LikeViewModel
    private lateinit var userSession: User

    // VIEWS
    private var matchesRV: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_matches, container, false)

        likeViewModel = ViewModelProvider(this)[LikeViewModel::class.java]
        userSession = UserSession.getSession(requireContext())

        // VIEW BINDING
        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout)
        matchesRV = view.findViewById(R.id.matchesRV)

        shimmerFrameLayout!!.startShimmer()
        matchesRV!!.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false
        )

        return view
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        likeViewModel.getMy(userSession._id!!)
        likeViewModel.likeList.observe(viewLifecycleOwner) {
            val likeList: ArrayList<Like> = arrayListOf()
            for (like in it) if (like.liked?._id == userSession._id) likeList.add(like)
            matchesRV!!.adapter = LikeAdapter(likeList, userSession)
            shimmerFrameLayout!!.stopShimmer()
            shimmerFrameLayout!!.visibility = View.GONE
        }
    }
}