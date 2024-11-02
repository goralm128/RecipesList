package com.exercise.recipes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EncryptedRecipe(
    val id: String,
    val iv: ByteArray, // Initialization vector for decryption
    val encryptedData: ByteArray // Encrypted recipe data as bytes
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedRecipe

        if (id != other.id) return false
        if (!iv.contentEquals(other.iv)) return false
        if (!encryptedData.contentEquals(other.encryptedData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + iv.contentHashCode()
        result = 31 * result + encryptedData.contentHashCode()
        return result
    }
}

