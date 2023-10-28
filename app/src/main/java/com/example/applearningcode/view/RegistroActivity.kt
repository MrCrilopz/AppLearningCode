package com.example.applearningcode.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.applearningcode.R
import com.example.applearningcode.databinding.ActivityInicioSesionBinding
import com.example.applearningcode.databinding.ActivityRegistroBinding
import com.example.applearningcode.model.User
import com.example.applearningcode.viewModel.InicioSesionViewModel
import com.example.applearningcode.viewModel.RegisterViewModel

class RegistroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding
    private lateinit var  viewModel: RegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        val contrasenia = binding.txtPassword
        val repetirContrasenia = binding.txtrepeatPassword

        val passwordWatcher = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = contrasenia.text.toString()
                val repeatPassword = repetirContrasenia.text.toString()

                if (password == repeatPassword){
                    repetirContrasenia.error = null
                }else{
                    repetirContrasenia.error = "Las contraseñas no coinciden"
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        contrasenia.addTextChangedListener(passwordWatcher)
        repetirContrasenia.addTextChangedListener(passwordWatcher)

        binding.btnRegistrar.setOnClickListener {
            val email = binding.txtEmail.text.toString()
            val name = binding.txtName.text.toString()
            val usuario = binding.txtUser.text.toString()
            val contrasenia = binding.txtPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && usuario.isNotEmpty() && contrasenia.isNotEmpty()){


                viewModel.registrarUsuario(email,name,usuario,contrasenia)
            }
        }

        viewModel.registroExitoso.observe(this, Observer { registroExitoso ->
            if (registroExitoso){
                //redirigir a pantalla prinicipal
                finish()
            }else{
                Log.d("Fallo registro", "Ha fallado el registro")
            }
        })
    }
}