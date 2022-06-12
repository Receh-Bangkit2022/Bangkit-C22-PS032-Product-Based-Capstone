package com.capstone.receh

import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

fun setFullscreen(window:Window){
    WindowCompat.setDecorFitsSystemWindows(window,false)}

fun LightStatusBar(window:Window,isLight:Boolean=true){
    val wic= WindowInsetsControllerCompat(window,window.decorView)
    wic.isAppearanceLightStatusBars=isLight
}

fun DarkStatusBar(window: Window){
    val wic= WindowInsetsControllerCompat(window,window.decorView)
    wic.isAppearanceLightStatusBars=false
}
