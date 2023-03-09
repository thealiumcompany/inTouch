package com.thealiumcompany.intouch

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class ColorBinding : BaseObservable() {

    @get:Bindable
    var primaryColor: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.primaryColor)
        }

    @get:Bindable
    var secondaryColor: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.secondaryColor)
        }

    @get:Bindable
    var tertiaryColor: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.tertiaryColor)
        }

    @get:Bindable
    var textPrimaryColor: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.textPrimaryColor)
        }

    @get:Bindable
    var textSecondaryColor: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.textSecondaryColor)
        }
}