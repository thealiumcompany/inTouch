package com.thealiumcompany.intouch

data class Card(
    var primaryColor: String? = null,
    var secondaryColor: String? = null,
    var tertiaryColor: String? = null,
    var primaryTextColor: String? = null,
    var secondaryTextColor: String? = null,

    var website: String? = null,
    var name: String? = null,
    var position: String? = null,
    var phoneNo1: String? = null,
    var phoneNo2: String? = null,
    var email1: String? = null,
    var email2: String? = null,
    var streetAddress: String? = null,
    var cityTown: String? = null,
    var cardStyle: String? = null
)
