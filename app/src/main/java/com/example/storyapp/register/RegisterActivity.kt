package com.example.storyapp.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.customview.PasswordEditText
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.login.LoginActivity

class RegisterActivity : AppCompatActivity(), PasswordEditText.OnPasswordChangedListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edRegisterPassword.passwordChangedListener = this

        setupView()
        setupAction()
        setupViewModel()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvRegist =
            ObjectAnimator.ofFloat(binding.tvRegisterMsg, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)
        val tlName = ObjectAnimator.ofFloat(binding.tlRegisterName, View.ALPHA, 1f).setDuration(500)
        val edName = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val tlEmail =
            ObjectAnimator.ofFloat(binding.tlRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val edEmail =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val tlPassword =
            ObjectAnimator.ofFloat(binding.tlRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val edPassword =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(500)



        AnimatorSet().apply {
            playSequentially(
                tvRegist,
                edName,
                tlName,
                edEmail,
                tlEmail,
                edPassword,
                tlPassword,
                signup
            )
            start()
        }
    }

    override fun onPasswordChanged(isValid: Boolean) {
        binding.btnRegister.isEnabled = isValid
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

    private fun setupAction() {
        binding.apply {
            btnRegister.setOnClickListener {
                if (edRegisterName.length() == 0 && edRegisterEmail.length() == 0 && edRegisterPassword.length() == 0) {
                    edRegisterName.error = getString(R.string.harus_diisi)
                    edRegisterEmail.error = getString(R.string.harus_diisi)
                    edRegisterPassword.setError(getString(R.string.harus_diisi), null)
                } else if (edRegisterName.length() != 0 && edRegisterEmail.length() != 0 && edRegisterPassword.length() != 0) {
                    if(edRegisterPassword.error == null) {
                        showLoading()
                        postText()
                        showToast()
                        moveActivity()
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
    }

    private fun showLoading() {
        viewModel = factory.create(RegisterViewModel::class.java)
        viewModel.isLoading.observe(this@RegisterActivity) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun moveActivity() {
        viewModel.registerResponse.observe(this@RegisterActivity) { response ->
            if (!response.error) {
                Toast.makeText(
                    applicationContext,
                    "Akun telah dibuat",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun showToast() {
        viewModel.toast.observe(this@RegisterActivity) {
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(
                    this@RegisterActivity, toastText, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun postText() {
        binding.apply {
            viewModel.regist(
                edRegisterName.text.toString(),
                edRegisterEmail.text.toString(),
                edRegisterPassword.text.toString()
            )
        }
    }
}