/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.ph.phome

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.transition.Scene
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

abstract class TvSettingsActivity : Activity() {

    var mResumed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {

            val root = findViewById<View>(android.R.id.content) as ViewGroup
            root.viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        root.viewTreeObserver.removeOnPreDrawListener(this)
                        val scene = Scene(root)
                        scene.setEnterAction {
                            val fragment = createSettingsFragment()
                            if (fragment != null) {
                                fragmentManager.beginTransaction()
                                    .add(
                                        android.R.id.content, fragment,
                                        SETTINGS_FRAGMENT_TAG
                                    )
                                    .commitNow()
                            }
                        }

                        val slide = Slide(Gravity.END)
//                        slide.setSlideFraction(
//                            resources.getDimension(R.dimen.lb_settings_pane_width) / root.width
//                        )
                        TransitionManager.go(scene, slide)

                        // Skip the current draw, there's nothing in it
                        return false
                    }
                })
        }
    }

    override fun finish() {
        val fragment = fragmentManager.findFragmentByTag(SETTINGS_FRAGMENT_TAG)
        if (mResumed && fragment != null) {
            val root = findViewById<View>(android.R.id.content) as ViewGroup
            val scene = Scene(root)
            scene.setEnterAction {
                fragmentManager.beginTransaction()
                    .remove(fragment)
                    .commitNow()
            }
            val slide = Slide(Gravity.END)
//            slide.setSlideFraction(
//                resources.getDimension(R.dimen.lb_settings_pane_width) / root.width
//            )
            slide.addListener(object : Transition.TransitionListener {
                override fun onTransitionStart(transition: Transition) {
                    window.setDimAmount(0f)
                }

                override fun onTransitionEnd(transition: Transition) {
                    transition.removeListener(this)
                    super@TvSettingsActivity.finish()
                }

                override fun onTransitionCancel(transition: Transition) {}

                override fun onTransitionPause(transition: Transition) {}

                override fun onTransitionResume(transition: Transition) {}
            })
            TransitionManager.go(scene, slide)
        } else {
            super.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        mResumed = true
    }

    override fun onPause() {
        mResumed = false
        super.onPause()
    }

    protected abstract fun createSettingsFragment(): Fragment?

    companion object {

        private val SETTINGS_FRAGMENT_TAG = "com.android.tv.settings.MainSettings.SETTINGS_FRAGMENT"
    }
}
