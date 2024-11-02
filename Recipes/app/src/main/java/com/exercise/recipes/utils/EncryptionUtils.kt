package com.exercise.recipes.utils


import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.exercise.recipes.EncryptedRecipe
import com.exercise.recipes.network.model.Recipe
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

private const val KEY_ALIAS = "RecipeKey"
private const val ANDROID_KEYSTORE = "AndroidKeyStore"


fun getSecretKey(): SecretKey {
    val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    return if (keyStore.containsAlias(KEY_ALIAS)) {
        keyStore.getKey(KEY_ALIAS, null) as SecretKey
    } else {
        generateKey()
    }
}

private fun generateKey(): SecretKey {
    val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
    val keyGenParameterSpec = KeyGenParameterSpec.Builder(
        KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        .setUserAuthenticationRequired(true) // Requires user authentication for each use
        .setInvalidatedByBiometricEnrollment(true) // Invalidate key if biometrics change
        .build()

    keyGenerator.init(keyGenParameterSpec)
    return keyGenerator.generateKey()
}

fun encryptRecipe(recipe: Recipe, cipher: Cipher): EncryptedRecipe {
    val encryptedData = cipher.doFinal(Gson().toJson(recipe).toByteArray(Charsets.UTF_8))
    return EncryptedRecipe(recipe.id, cipher.iv, encryptedData)
}

suspend fun decryptRecipe(encryptedRecipe: EncryptedRecipe, cipher: Cipher): Recipe {
    return withContext(Dispatchers.IO) {
        try {
            val decryptedData = cipher.doFinal(encryptedRecipe.encryptedData)
            val decryptedJson = String(decryptedData, Charsets.UTF_8)
            Gson().fromJson(decryptedJson, Recipe::class.java)
        } catch (e: Exception) {
            throw SecurityException("Decryption failed", e)
        }
    }
}
