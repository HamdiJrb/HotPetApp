package com.example.hotpet.view.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.hotpet.R
import com.example.hotpet.adapters.SwipeAdapter
import com.example.hotpet.api.ApiService
import com.example.hotpet.api.ChatService
import com.example.hotpet.api.LikeService
import com.example.hotpet.api.UserService
import com.example.hotpet.api.models.Like
import com.example.hotpet.api.models.User
import com.example.hotpet.api.viewModels.ChatViewModel
import com.example.hotpet.api.viewModels.LikeViewModel
import com.example.hotpet.api.viewModels.UserViewModel
import com.example.hotpet.utils.AlertMaker
import com.example.hotpet.utils.DateUtils
import com.example.hotpet.utils.UserSession
import com.facebook.shimmer.ShimmerFrameLayout
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import com.lorentzos.flingswipe.SwipeFlingAdapterView.onFlingListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketException
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class DiscoverFragment : Fragment() {

    // VARIABLES
    private lateinit var userViewModel: UserViewModel
    private lateinit var likeViewModel: LikeViewModel
    private lateinit var currentUser: User
    private var locationIsGranted: Boolean = false
    private var flingContainer: SwipeFlingAdapterView? = null
    private var userAdapter: SwipeAdapter? = null
    private var userList: ArrayList<User> = ArrayList()
    private var locationManager: LocationManager? = null
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    // VIEWS
    private var likeIB: ImageButton? = null
    private var dislikeIB: ImageButton? = null
    private var noMoreCardsTV: TextView? = null
    private var singleSwipeShimmer: ShimmerFrameLayout? = null
    private var givePermissionButton: Button? = null
    //private var testImageButton: ImageButton? = null

    // LIFECYCLE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_matching, container, false)

        likeViewModel = ViewModelProvider(this)[LikeViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        currentUser = UserSession.getSession(requireContext())

        // VIEW BINDING
        flingContainer = view.findViewById(R.id.flingContainer)
        likeIB = view.findViewById(R.id.likeIB)
        dislikeIB = view.findViewById(R.id.dislikeIB)
        noMoreCardsTV = view.findViewById(R.id.noMoreCardsTV)
        singleSwipeShimmer = view.findViewById(R.id.singleSwipeShimmer)
        givePermissionButton = view.findViewById(R.id.givePermissionButton)
        //testImageButton = view.findViewById(R.id.testImageButton)

        // ACTIONS
        likeIB!!.setOnClickListener { flingContainer!!.topCardListener.selectRight() }
        dislikeIB!!.setOnClickListener { flingContainer!!.topCardListener.selectLeft() }
        givePermissionButton!!.setOnClickListener {
            val intent = Intent()
            intent.data = Uri.parse("package:" + requireContext().packageName)
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            requireContext().startActivity(intent)
        }

        locationIsGranted =
            requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!locationIsGranted) promptLocationPermission()
        setupSwipeCard()
        setupObservers()

        //testImageButton!!.setOnClickListener { }

        currentLatitude = currentUser.latitude
        currentLongitude = currentUser.longitude

        return view
    }

    override fun onResume() {
        super.onResume()
        if (context != null) {
            currentUser = UserSession.getSession(requireContext())
            if (!locationIsGranted || currentLatitude == null || currentLongitude == null) {
                println(" no location $currentLatitude + $currentLongitude")
                getLocation()
            } else {
                println("$currentLatitude + $currentLongitude")
                getLocation()
                loadUsers()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        userList.clear()
        userAdapter!!.notifyDataSetChanged()
    }

    private fun promptLocationPermission() {
        noMoreCardsTV!!.visibility = View.VISIBLE
        givePermissionButton!!.visibility = View.VISIBLE
        noMoreCardsTV!!.text = getString(R.string.give_location_permission_access)
        if (!requireActivity().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            // LOCATION PERMISSIONS
            requireActivity().registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                when {
                    permissions.getOrDefault(
                        Manifest.permission.ACCESS_FINE_LOCATION, false
                    ) -> {
                        getLocation()
                    }
                    else -> {}
                }
            }.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private val locationListener: LocationListener = LocationListener { location ->
        currentLatitude = location.latitude
        currentLongitude = location.longitude
        println("Location : $currentLatitude / $currentLongitude")
        userViewModel.updateLocation(
            UserService.UpdateLocationBody(currentUser.email, currentLatitude!!, currentLongitude!!)
        )
        loadUsers()
    }

    private fun loadUsers() {
        if (locationManager != null) locationManager!!.removeUpdates(locationListener)
        userViewModel.getAll()
    }

    private fun getLocation() {
        givePermissionButton!!.visibility = View.GONE

        try {
            locationManager =
                requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                locationListener
            )
        } catch (ex: SecurityException) {
            println("Security Exception, no location available")
        }
    }

    private fun setupSwipeCard() {
        userAdapter = SwipeAdapter(requireContext(), R.layout.single_swipe, userList)
        flingContainer!!.adapter = userAdapter
        userAdapter!!.notifyDataSetChanged()
        flingContainer!!.setFlingListener(object : onFlingListener {

            override fun onRightCardExit(dataObject: Any) {
                val user = userList[0]
                userList.removeAt(0)
                userAdapter!!.notifyDataSetChanged()
                checkRowItem()
                addLike(true, user)
            }

            override fun onLeftCardExit(dataObject: Any) {
                val user = userList[0]
                userList.removeAt(0)
                userAdapter!!.notifyDataSetChanged()
                checkRowItem()
                addLike(false, user)
            }

            override fun removeFirstObjectInAdapter() {}

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {}

            override fun onScroll(scrollProgressPercent: Float) {
                val view = flingContainer!!.selectedView
                view.findViewById<View>(R.id.item_swipe_right_indicator).alpha =
                    if (scrollProgressPercent < 0) -scrollProgressPercent else 0F
                view.findViewById<View>(R.id.item_swipe_left_indicator).alpha =
                    if (scrollProgressPercent > 0) scrollProgressPercent else 0F
            }
        })
    }

    private fun checkRowItem() {
        if (userList.isEmpty()) {
            noMoreCardsTV!!.text = getString(R.string.no_more_cards)
            noMoreCardsTV!!.visibility = View.VISIBLE
            likeIB!!.isEnabled = false
            dislikeIB!!.isEnabled = false
        } else {
            noMoreCardsTV!!.visibility = View.GONE
        }
    }

    private fun addLike(isRight: Boolean, user: User) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val response = ApiService.likeService().add(
                    LikeService.LikeBody(
                        user._id!!,
                        currentUser._id!!,
                        isRight
                    )
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val like = response.body()!!

                        if (like.isMatch) {
                            AlertMaker.makeActionAlert(
                                requireContext(),
                                "Match !",
                                "You successfully matched with " +
                                        like.liker?.username +
                                        " a new conversation has been created."
                            ) {
                                val chatViewModel =
                                    ViewModelProvider(this@DiscoverFragment)[ChatViewModel::class.java]
                                chatViewModel.createNewConversation(
                                    ChatService.ConversationBody(
                                        currentUser._id!!,
                                        like.liker?._id!!
                                    )
                                )
                                chatViewModel.createNewConversation(
                                    ChatService.ConversationBody(
                                        like.liker?._id!!,
                                        currentUser._id!!
                                    )
                                )
                            }
                        }
                        likeViewModel.like.removeObservers(viewLifecycleOwner)
                    }
                }
            }
        } catch (e: IllegalStateException) {
            println("IllegalStateException")
        } catch (e: SocketException) {
            println("SERVER CRASH")
        }
    }

    private fun setupObservers() {
        var users: List<User> = arrayListOf()
        userViewModel.userList.observe(viewLifecycleOwner) {
            users = it.toList()
            likeViewModel.getMy(currentUser._id!!)
        }
        likeViewModel.likeList.observe(viewLifecycleOwner) { likes ->
            println("Users check started ------------------------------")

            userList.clear()
            for (user in users) if (checkFilters(user, currentUser, likes)) userList.add(user)
            userAdapter!!.notifyDataSetChanged()

            println("Users check ended --------------------------------")
        }
        userViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            toggleShimmer(isLoading)
        }
        likeViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            toggleShimmer(isLoading)
        }
    }

    private fun toggleShimmer(enable: Boolean) {
        if (enable) {
            singleSwipeShimmer!!.startShimmer()
            singleSwipeShimmer!!.visibility = View.VISIBLE
            noMoreCardsTV!!.visibility = View.GONE
        } else {
            singleSwipeShimmer!!.stopShimmer()
            singleSwipeShimmer!!.visibility = View.GONE
            noMoreCardsTV!!.visibility = View.VISIBLE
        }
    }

    private fun checkFilters(user: User, currentUser: User, likes: List<Like>): Boolean {
        if (user._id == currentUser._id) {
            // User is you
            println(user.username + " - User is you")
            return false
        } else {
            if ((user.category != currentUser.category) || (user.gender == currentUser.gender)) {
                // Category or gender is not valid
                println(user.username + " - Category or gender is not valid")
                return false
            } else {
                if (user.latitude == null || user.longitude == null) {
                    // User has no location
                    println(user.username + " - User has no location")
                    return false
                } else {
                    val distance = calculateDistance(
                        user.latitude, user.longitude, currentLatitude!!, currentLongitude!!
                    )
                    if (distance > currentUser.preferredDistance * 1000) {
                        // User is far away
                        println(user.username + " - User is far away / Distance = $distance")
                        return false
                    } else {
                        if (DateUtils.getAge(user.birthdate) !in currentUser.preferredAgeMin..currentUser.preferredAgeMax) {
                            // Age is not in range
                            println(user.username + " - Age is not in range")
                            return false
                        } else {
                            for (like in likes) {
                                if ((like.liker?._id == currentUser._id) && (like.liked?._id == user._id)) {
                                    // User is liked by you
                                    println(user.username + " - User is liked by you")
                                    return false
                                }
                                if ((like.liker?._id == user._id) && (like.liked?._id == currentUser._id) && (like.isMatch)) {
                                    // User is likes you && matched
                                    println(user.username + " - User is likes you && matched")
                                    return false
                                }
                            }
                        }
                    }
                }
            }
        }
        println(user.username + " - User Accepted")
        return true
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371e3
        val u1 = lat1 * Math.PI / 180 // in radians
        val u2 = lat2 * Math.PI / 180
        val d1 = (lat2 - lat1) * Math.PI / 180
        val d2 = (lon2 - lon1) * Math.PI / 180

        val a = sin(d1 / 2) * sin(d1 / 2) +
                cos(u1) * cos(u2) *
                sin(d2 / 2) * sin(d2 / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val d = r * c // in metres
        return (d)
    }

}