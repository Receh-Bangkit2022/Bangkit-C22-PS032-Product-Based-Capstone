package com.capstone.receh.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.capstone.receh.*
import com.capstone.receh.R
import com.capstone.receh.databinding.ActivityMainBinding
import com.capstone.receh.fragment.home.HomeFragment
import com.capstone.receh.model.UserModel
import com.capstone.receh.model.UserPreference
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var name : String
    private lateinit var email : String
    private lateinit var pass : String
    private lateinit var userID : String
    private lateinit var phone: String
    private lateinit var balance : String
    lateinit var auth: FirebaseAuth
    var databaseReference :  DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullscreen(window)
        LightStatusBar(window)
        statuscolor()
        setupViewModel()
        mainViewModel = ViewModelProvider(this).get(mainViewModel::class.java)
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://receh-app-v1-default-rtdb.asia-southeast1.firebasedatabase.app/")
        databaseReference = database?.reference!!.child("Users")
        loadProfile()

    }
    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) {

            if (it.isLogin) {
             it.name
            } else {
                    startActivity(Intent(this, ActivityWelcome::class.java))
                finish()
    }}}

    private fun statuscolor() {
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> {
                    LightStatusBar(window)
                }
                R.id.navigation_transaksi -> {
                    LightStatusBar(window)
                }
                R.id.navigation_akun -> {
                    DarkStatusBar(window)
                }
            }
        }
    }

    fun notif(){
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)

        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(this, USER_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.notifikasi)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.notifikasi))
            .setContentTitle(resources.getString(R.string.content_title))
            .setContentText(resources.getString(R.string.content_text))
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = CHANNEL_NAME
            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
            val notification = mBuilder.build()
            mNotificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun loadProfile() {
        val user = auth.currentUser
        val recehId = "Receh-"+ user?.uid
        val userreference = databaseReference?.child(recehId)

        userreference?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                name = snapshot.child("name").value.toString()
                Log.d("Nama", name)
                email = snapshot.child("email").value.toString()
                 pass = snapshot.child("password").value.toString()
                phone = snapshot.child("phone").value.toString()
                userID = snapshot.child("id").value.toString()
                balance = snapshot.child("balance").value.toString()
                Log.d("Nama", balance)
                val fragment = HomeFragment()
                val args = Bundle()
                args.putString("Balance", balance)
                fragment.arguments = args
                mainViewModel.getUser().observe(this@MainActivity) {
                    val saldolama = it.balance
                    if (saldolama != balance) {
                        notif()
                    }
                }
                mainViewModel.saveUser(UserModel(name,
                    email,
                    pass,
                    phone,
                    userID,
                    balance,
                    true))
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    companion object{
        const val TAG = ""
        const val USER_ID ="user-id"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "Receh"
    }
}