package com.example.applearningcode.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.applearningcode.R
import com.example.applearningcode.databinding.ActivityInicioSesionBinding
import com.example.applearningcode.viewModel.InicioSesionViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth

class InicioSesionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioSesionBinding
    private lateinit var  viewModel1: InicioSesionViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioSesionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel1 = ViewModelProvider(this).get(InicioSesionViewModel::class.java)

        viewModel1.usuarioAutenticado.observe(this, Observer { usuario ->
            if (usuario !=null){
                //meter la sigiente activity
            }else{
                //mensaje usuario no auntenticado
            }
        })

        binding.btnIniciarSesionGoogle.setOnClickListener {
            viewModel1.iniciarSesionGoogle(this)
        }
        binding.btnIniciarSesion.setOnClickListener {
            val userOrCorreo = binding.txtUsuario.text.toString()
            val password = binding.txtConstrasenia.text.toString()
            if (userOrCorreo.isNotEmpty() && password.isNotEmpty()) {
                viewModel1.iniciarSesionCorreo(userOrCorreo, password)
            }
        }

        viewModel1.sesionIniciada.observe(this, Observer { sesionIniciada ->
            if (sesionIniciada){
                //redirigir a pantalla prinicipal
                Toast.makeText(this, "Inicio sesión exitoso", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Log.d("Fallo inicio", "Ha fallado el inicio de sesion")
            }
        })
    }
}