package com.quicknotes

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// STARTUP PATH — STEP 1: the Application class.
// Created once when the app process starts, before any screen. Registered in
// AndroidManifest.xml via android:name=".QuickNotesApp".
// It has no UI. Its only job here is @HiltAndroidApp, which boots dependency
// injection, roughly like setting up a NestJS root module at bootstrap.
// Next step: MainActivity.kt
@HiltAndroidApp
class QuickNotesApp : Application()
