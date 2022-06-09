package com.capstone.receh

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

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
