package com.ph.phome

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

class ScaleAnimator {
    companion object {
        fun scaleUp(view: View){
            val valueAnimator = ValueAnimator.ofFloat(1.0f, 1.08f)
            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Float
                view.scaleX = value
                view.scaleY = value
                view.elevation = value
            }
            valueAnimator.interpolator = AccelerateDecelerateInterpolator()
            valueAnimator.start()
        }

        fun scaleDown(view: View){
            val valueAnimator = ValueAnimator.ofFloat(1.08f, 1.0f)
            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Float
                view.scaleX = value
                view.scaleY = value
                view.elevation = value
            }
            valueAnimator.interpolator = AccelerateDecelerateInterpolator()
            valueAnimator.start()
        }
    }
}