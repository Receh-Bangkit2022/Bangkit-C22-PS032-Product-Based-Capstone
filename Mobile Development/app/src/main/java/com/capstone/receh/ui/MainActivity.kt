package com.capstone.receh.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.capstone.receh.*
import com.capstone.receh.R
import com.capstone.receh.databinding.ActivityMainBinding
import com.capstone.receh.fragment.home.HomeFragment
import com.capstone.receh.model.UserModel
import com.capstone.receh.model.UserPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    lateinit var auth: FirebaseAuth
    var databaseReference :  DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullscreen(window)
        statuscolor()
        setupViewModel()
        mainViewModel = ViewModelProvider(this).get(mainViewModel::class.java)
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_transaksi, R.id.navigation_akun))
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

    private fun loadProfile() {

        val user = auth.currentUser
        val recehId = "Receh-"+ user?.uid
        val userreference = databaseReference?.child(recehId)

        userreference?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value.toString()
                Log.d("ISI NAMANYAAAAAAAAAAA", "$name")
                val email = snapshot.child("email").value.toString()
                val pass = snapshot.child("password").value.toString()
                val phone = snapshot.child("phone").value.toString()
                val userID = snapshot.child("id").value.toString()
                val balance = snapshot.child("balance").value.toString()

                val fragment = HomeFragment()
                val args = Bundle()
                args.putString("Balance", balance)
                fragment.arguments = args

                mainViewModel.saveUser(UserModel(name,email,pass,phone,userID,balance,true))
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}