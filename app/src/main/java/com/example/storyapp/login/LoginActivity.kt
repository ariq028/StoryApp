package com.example.storyapp.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.customview.PasswordEditText
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.main.MainActivity
import com.example.storyapp.model.UserModel
import com.example.storyapp.model.UserPreference
import com.example.storyapp.register.RegisterActivity
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class LoginActivity : AppCompatActivity(), PasswordEditText.OnPasswordChangedListener {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edLoginPassword.passwordChangedListener = this

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvLogin = ObjectAnimator.ofFloat(binding.tvLoginMsg, View.ALPHA, 1f).setDuration(500)
        val tvregist = ObjectAnimator.ofFloat(binding.tvNoacc, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)
        val tlEmail = ObjectAnimator.ofFloat(binding.tlLoginEmail, View.ALPHA, 1f).setDuration(500)
        val edEmail = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val tlPassword =
            ObjectAnimator.ofFloat(binding.tlLoginPassword, View.ALPHA, 1f).setDuration(500)
        val edPassword =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(login, signup, tvregist)
        }


        AnimatorSet().apply {
            playSequentially(tvLogin, edEmail, tlEmail, edPassword, tlPassword, together)
            start()
        }
    }

    override fun onPasswordChanged(isValid: Boolean) {
        binding.btnLogin.isEnabled = isValid
    }


    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
    }

    private fun setupAction() {
        binding.apply {
            btnLogin.setOnClickListener {
                if (edLoginEmail.length() == 0 && edLoginPassword.length() == 0) {
                    edLoginEmail.error = getString(R.string.harus_diisi)
                    edLoginPassword.setError(getString(R.string.harus_diisi), null)
                } else if (edLoginEmail.length() != 0 && edLoginPassword.length() != 0) {
                    if(edLoginPassword.error == null){
                        showLoading()
                        postText()
                        showToast()
                        loginViewModel.login()
                        moveActivity()
                    }
                }
            }
            btnRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun showLoading() {
        loginViewModel = factory.create(LoginViewModel::class.java)
        loginViewModel.isLoading.observe(this@LoginActivity) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun postText() {
        binding.apply {
            loginViewModel.log(
                edLoginEmail.text.toString(),
                edLoginPassword.text.toString()
            )
        }

        loginViewModel.loginResponse.observe(this@LoginActivity) { response ->
            val userPreference = UserPreference.getInstance(dataStore)
            val token = runBlocking { userPreference.getToken() }
            if (!token.isNullOrEmpty()) {
                saveUser(UserModel(response.loginResult?.name.toString(), token, true))
                Log.d("LoginActivity", "Token: $token")
            } else {
                saveUser(
                    UserModel(
                        response.loginResult?.name.toString(),
                        AUTH_KEY + (response.loginResult?.token.toString()),
                        true
                    )
                )
            }
        }
    }

    private fun showToast() {
        loginViewModel.toastText.observe(this@LoginActivity) {
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(
                    this@LoginActivity, toastText, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveUser(user: UserModel) {
        loginViewModel.saveUser(user)
    }


    private fun moveActivity() {
        loginViewModel.loginResponse.observe(this@LoginActivity) { response ->
            if (!response.error) {
                Toast.makeText(
                    applicationContext,
                    "Anda berhasil login",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Email atau password salah",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        private const val AUTH_KEY = "Bearer "
    }

}