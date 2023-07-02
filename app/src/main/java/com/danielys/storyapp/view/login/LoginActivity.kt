package com.danielys.storyapp.view.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.danielys.storyapp.R
import com.danielys.storyapp.data.UserPreferences
import com.danielys.storyapp.databinding.ActivityLoginBinding
import com.danielys.storyapp.view.ViewModelFactoryPreferences
import com.danielys.storyapp.view.main.MainActivity
import com.danielys.storyapp.view.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreferences.getInstance(dataStore)
        val loginViewModel = ViewModelProvider(this, ViewModelFactoryPreferences(pref)).get(
            LoginViewModel::class.java
        )

        loginViewModel.dataLogin.observe(this) { loginResponse ->
            if (loginResponse.error == true) {
                Toast.makeText(
                    this,
                    applicationContext.getString(R.string.login_failed),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    applicationContext.getString(R.string.login_success),
                    Toast.LENGTH_SHORT
                ).show()

                loginViewModel.setName(loginResponse.loginResult?.name as String)
                loginViewModel.setUserId(loginResponse.loginResult.userId as String)
                loginViewModel.setToken(loginResponse.loginResult.token as String)

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("token", loginResponse.loginResult.token)
                startActivity(intent)

            }
        }

        binding.buttonLogin.setOnClickListener {
            val editTextEmail = binding.editTextEmail
            val editTextPassword = binding.editTextPassword

            if (editTextEmail.error == null && editTextPassword.error == null && editTextPassword.text.toString() != "" && editTextEmail.text.toString() != "") {
                loginViewModel.login(
                    binding.editTextEmail.text.toString(),
                    binding.editTextPassword.text.toString()
                )
            } else {
                Toast.makeText(
                    this,
                    applicationContext.getString(R.string.insertinputwarning),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        loginViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, "Error : $errorMessage", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}