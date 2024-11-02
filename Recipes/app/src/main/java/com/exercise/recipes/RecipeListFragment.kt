package com.exercise.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.exercise.recipes.compose.RecipeListScreen
import com.exercise.recipes.network.model.Recipe
import com.exercise.recipes.utils.encryptRecipe
import com.exercise.recipes.utils.getSecretKey
import java.util.concurrent.Executors
import javax.crypto.Cipher
import javax.crypto.SecretKey

class RecipeListFragment : Fragment() {

    private val viewModel: RecipeListViewModel by viewModels {
        RecipeViewModelFactory(RecipeComponentsController.repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    RecipeListScreen(
                        viewModel = viewModel,
                        onRecipeClick = { recipe ->
                            promptBiometricAndNavigate(recipe)
                        }
                    )
                }
            }
        }
    }

    private fun promptBiometricAndNavigate(recipe: Recipe) {
        val cipher = getInitializedCipherForEncryption() ?: return // Ensure cipher is properly initialized

        val biometricPrompt = BiometricPrompt(
            this,
            Executors.newSingleThreadExecutor(),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    result.cryptoObject?.cipher?.let { authenticatedCipher ->
                        activity?.runOnUiThread {
                            try {
                                val encryptedRecipe = encryptRecipe(recipe, authenticatedCipher)
                                val action = RecipeListFragmentDirections.actionRecipeListFragmentToRecipeDetailFragment(encryptedRecipe)
                                findNavController().navigate(action)
                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), "Error encrypting recipe: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Authenticate to encrypt and view the recipe details")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }

    private fun getInitializedCipherForEncryption(): Cipher? {
        return try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            val secretKey: SecretKey = getSecretKey()
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            cipher
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}