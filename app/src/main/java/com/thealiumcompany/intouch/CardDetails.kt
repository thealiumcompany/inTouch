package com.thealiumcompany.intouch

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class CardDetails : BaseObservable() {

    @get:Bindable
    var website: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.website)
        }

    @get:Bindable
    var name: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @get:Bindable
    var position: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.position)
        }

    @get:Bindable
    var phone1: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.phone1)
        }

    @get:Bindable
    var phone2: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.phone2)
        }

    @get:Bindable
    var email1: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.email1)
        }

    @get:Bindable
    var email2: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.email2)
        }

    @get:Bindable
    var streetAddress: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.streetAddress)
        }

    @get:Bindable
    var cityTown: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.cityTown)
        }
}
