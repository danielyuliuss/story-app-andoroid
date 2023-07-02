package com.danielys.storyapp.view.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.danielys.storyapp.R
import com.danielys.storyapp.databinding.ActivityRegisterBinding
import com.danielys.storyapp.view.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private val registerViewModel by viewModels<RegisterViewModel>()
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerViewModel.status.observe(this) { status ->
            if (status == "success") {
                Toast.makeText(
                    this,
                    applicationContext.getString(R.string.register_success),
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    applicationContext.getString(R.string.register_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.buttonSignUp.setOnClickListener {
            val editTextEmail = binding.editTextEmail
            val editTextPassword = binding.editTextPassword
            val editTextName = binding.editTextName

            if (editTextEmail.error == null
                && editTextPassword.error == null
                && editTextName.text.toString() != ""
                && editTextEmail.text.toString() != ""
                && editTextPassword.text.toString() != ""
            ) {

                registerViewModel.register(
                    editTextName.text.toString(),
                    editTextEmail.text.toString(),
                    editTextPassword.text.toString()
                )

            } else {
                Toast.makeText(
                    this,
                    applicationContext.getString(R.string.insertinputwarning),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        registerViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        registerViewModel.errorMessage.observe(this) { errorMessage ->
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