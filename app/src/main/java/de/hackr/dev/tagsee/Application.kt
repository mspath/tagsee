package de.hackr.dev.tagsee

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// NOTE areas of improvement:
//  string resources and localization
//  dark theme
//  checking of network status and error handling
//  tests

// FIXME known bugs:
//  filter strategy can get out of sync with the value of the dropdown
//  textfield for new tags keeps previous value even if added since
@HiltAndroidApp
class Application : Application()