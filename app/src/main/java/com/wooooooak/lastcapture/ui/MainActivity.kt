package com.wooooooak.lastcapture.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.logger.Logger
import com.wooooooak.lastcapture.R
import com.wooooooak.lastcapture.databinding.ActivityMainBinding
import com.wooooooak.lastcapture.utilities.PermissionUtil

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        Logger.d("onCreate MainActivity")
        checkPermission()
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) {}

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        navController = Navigation.findNavController(
            this,
            R.id.navigation_host_fragment
        )

        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun checkPermission() {
        PermissionUtil(this).checkExtStoragePermission({
        }, {
            Snackbar.make(window.decorView.rootView, "권한이 거부되어 종료됩니다.", Snackbar.LENGTH_LONG).show()
        })
    }

}
