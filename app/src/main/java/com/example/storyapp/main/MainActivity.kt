package com.example.storyapp.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.addstory.AddStoryActivity
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.detailstory.DetailActivity
import com.example.storyapp.login.LoginActivity
import com.example.storyapp.maps.MapsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels { factory }
    private var token = ""
    private var name = ""
    private lateinit var factory: ViewModelFactory
    private lateinit var adapterStory: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        adapterSettings()
        setupAction()
        userSettings()

    }

    private fun setupAction() {
        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
        showToast()
    }

    private fun setupViewModel() {

        factory = ViewModelFactory.getInstance(this)

        viewModel.getUser().observe(this) { dataSession ->
            if (dataSession.token.isEmpty()) {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                token = dataSession.token
                name = dataSession.name

                viewModel.saveUser(dataSession)

                viewModel.isLoading.observe(this) {
                    showLoading()
                }
            }
        }
    }

    private fun userSettings() {
        showLoading()
        viewModel.getUser().observe(this@MainActivity) {
            token = it.token
            if (!it.isLogin) {
                intentActivity()
            } else {
                pagingData()
            }
        }
        showToast()
    }

    private fun adapterSettings() {
        adapterStory = StoryAdapter()
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = adapterStory.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapterStory.retry()
                })
        }
    }

    private fun pagingData() {
        viewModel.getListStories.observe(this@MainActivity) { pagingData ->
            adapterStory.submitData(lifecycle, pagingData)
        }
    }

    private fun intentActivity() {
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                Log.d(TAG, "logout option clicked")
                viewModel.logout()
                true
            }
            R.id.language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.btn_maps -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                true
            }
            R.id.tvUsername -> {
                val moveIntent = Intent(this@MainActivity, DetailActivity::class.java)
                startActivity(moveIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showToast() {
        viewModel.toast.observe(this@MainActivity) {
            it.getContentIfNotHandled()?.let { toast ->
                Toast.makeText(
                    this@MainActivity, toast, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showLoading() {
        viewModel.isLoading.observe(this@MainActivity) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}