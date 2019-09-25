package com.ph.phome

import android.content.Context
import android.text.TextUtils
import androidx.preference.PreferenceGroup
import android.util.AttributeSet
import androidx.preference.CheckBoxPreference


class RadioPreference : CheckBoxPreference {
    private var mRadioGroup: String? = null

    constructor (context: Context) : this(context, null)

    constructor (context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RadioPreference, 0, 0)
        mRadioGroup = a.getString(R.styleable.RadioPreference_radioGroup)
        a.recycle()
        widgetLayoutResource = R.layout.radio_preference_widget
    }

    fun getRadioGroup(): String? {
        return mRadioGroup
    }

    fun setRadioGroup(radioGroup: String) {
        mRadioGroup = radioGroup
    }

    fun clearOtherRadioPreferences(preferenceGroup: PreferenceGroup) {
        val count = preferenceGroup.preferenceCount
        for (i in 0 until count) {
            val p = preferenceGroup.getPreference(i) as? RadioPreference ?: continue
            if (!TextUtils.equals(getRadioGroup(), p.getRadioGroup())) {
                continue
            }
            if (TextUtils.equals(key, p.key)) {
                continue
            }
            p.isChecked = false
        }
    }
}