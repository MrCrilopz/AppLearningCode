package com.example.applearningcode.viewModel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.applearningcode.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("usuarios")
    private val registro_Exitoso = MutableLiveData<Boolean>()
    val registroExitoso : LiveData<Boolean>
        get() = registro_Exitoso
    fun registrarUsuario( correo: String,nombre: String,  usuario: String, password: String){
        auth.createUserWithEmailAndPassword(correo,password)
            .addOnCompleteListener { registrationTask ->
                if (registrationTask.isSuccessful){
                    val userId = auth.currentUser?.uid

                    userId?.let {
                        val user =User(correo, nombre, it, usuario)
                        saveUserFirebase(user)
                    }
                }else{
                    //manejar errores durante el registro
                    registro_Exitoso.value=false
                    val authException = registrationTask.exception
                }
            }
    }

    private fun saveUserFirebase(user: User){
        database.child(user.uid).setValue(user)
            .addOnCompleteListener { databaseTask ->
                if (databaseTask.isSuccessful){
                    registro_Exitoso.value=true
                    Log.e("Guardardatos", "Se han guardado los datos en firebase")

                }else{
                    registro_Exitoso.value=false
                    val databaseException = databaseTask.exception
                }
            }
    }
}