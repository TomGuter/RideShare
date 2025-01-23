package com.example.shareride.model

import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val id: String,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val pictureUrl: String = ""
) {

    companion object {

        private const val ID_KEY = "id"
        private const val FIRST_NAME_KEY = "firstName"
        private const val LAST_NAME_KEY = "lastName"
        private const val EMAIL_KEY = "email"
        private const val PHONE_KEY = "phone"
        private const val PICTURE_URL_KEY = "pictureUrl"

        fun fromJSON(json: Map<String, Any>): User {
            val id = json[ID_KEY] as? String ?: ""
            val firstName = json[FIRST_NAME_KEY] as? String ?: ""
            val lastName = json[LAST_NAME_KEY] as? String ?: ""
            val email = json[EMAIL_KEY] as? String ?: ""
            val phone = json[PHONE_KEY] as? String ?: ""
            val pictureUrl = json[PICTURE_URL_KEY] as? String ?: ""

            return User(
                id = id,
                firstName = firstName,
                lastName = lastName,
                email = email,
                phone = phone,
                pictureUrl = pictureUrl
            )
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                FIRST_NAME_KEY to firstName,
                LAST_NAME_KEY to lastName,
                EMAIL_KEY to email,
                PHONE_KEY to phone,
                PICTURE_URL_KEY to pictureUrl
            )
        }
}