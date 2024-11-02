package com.exercise.recipes

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.exercise.recipes.compose.DisplayRecipeDetails
import com.exercise.recipes.network.model.Recipe
import com.exercise.recipes.utils.decryptRecipe
import com.exercise.recipes.utils.getSecretKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

class RecipeDetailsFragment : Fragment() {

    private val args: RecipeDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                var decryptedRecipe by remember { mutableStateOf<Recipe?>(null) }
                val encryptedRecipe = args.encryptedRecipe

                // Prompt biometric authentication when the composable is loaded
                LaunchedEffect(Unit) {
                    promptBiometricForDecryption(encryptedRecipe) { isAuthenticated, decryptedData ->
                        if (isAuthenticated && decryptedData != null) {
                            decryptedRecipe = decryptedData
                        } else if (!isAuthenticated) {
                            lifecycleScope.launch {
                                Toast.makeText(
                                    requireContext(),
                                    "Authentication failed. Unable to decrypt recipe.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                // Display recipe details or loading/authentication message
                if (decryptedRecipe != null) {
                    DisplayRecipeDetails(decryptedRecipe!!)
                } else {
                    Text(
                        text = "Authenticating...",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

    private fun promptBiometricForDecryption(
        encryptedRecipe: EncryptedRecipe,
        onResult: (Boolean, Recipe?) -> Unit
    ) {
        val cipher = getInitializedCipherForDecryption(encryptedRecipe.iv) ?: return

        val biometricPrompt = BiometricPrompt(
            requireActivity(),
            Executors.newSingleThreadExecutor(),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    result.cryptoObject?.cipher?.let { authenticatedCipher ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            try {
                                val decryptedRecipe = decryptRecipe(encryptedRecipe, authenticatedCipher)
                                withContext(Dispatchers.Main) {
                                    onResult(true, decryptedRecipe)
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Decryption failed: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    onResult(false, null)
                                }
                            }
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Authentication failed. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                        onResult(false, null)
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Authentication error: $errString",
                            Toast.LENGTH_SHORT
                        ).show()
                        onResult(false, null)
                    }
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Authenticate to decrypt the recipe")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }

    private fun getInitializedCipherForDecryption(iv: ByteArray): Cipher? {
        return try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            val secretKey = getSecretKey()
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
            cipher
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}



