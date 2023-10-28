package com.example.applearningcode.viewModel

import android.content.Intent
import android.nfc.Tag
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.applearningcode.model.User
import com.example.applearningcode.view.InicioSesionActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InicioSesionViewModel: ViewModel() {
    private val userAutenticado = MutableLiveData<FirebaseUser?>()
    val usuarioAutenticado: LiveData<FirebaseUser?>
        get() = userAutenticado
    private val database : DatabaseReference = FirebaseDatabase.getInstance().getReference("usuarios")
    private val auth = FirebaseAuth.getInstance()
    private val sesion_iniciada = MutableLiveData<Boolean>()
    val sesionIniciada : LiveData<Boolean>
        get() = sesion_iniciada

    fun iniciarSesionCorreo(emailOrUsername: String, password: String){
        auth.signInWithEmailAndPassword(emailOrUsername,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    userAutenticado.value = auth.currentUser
                    sesion_iniciada.value=true
                    Log.d("Inicio", "Se encontro el usuario y se inicio sesion con correo")
                }else{
                    database.orderByChild("user").equalTo(emailOrUsername).limitToFirst(1).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(dataSnapshot: DataSnapshot){
                            if (dataSnapshot.exists()){
                                for(userSnapshot in dataSnapshot.children){
                                    val user = userSnapshot.getValue(User::class.java)
                                    user?.let {
                                           userAutenticado.value = auth.currentUser

                                            return
                                    }
                                }
                            }else {
                                Log.d("FALLO", "Usuario no encontrado")
                                sesion_iniciada.value=false
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError){

                        }
                    })
                    sesion_iniciada.value=true
                    Log.d("Inicio", "Se encontro el usuario y se inicio sesion con usuario")
                }
            }
    }
    fun iniciarSesionGoogle(activity: AppCompatActivity){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("612581093243-m213d0nuu60go1vpuevt83ebe6sf4bbb.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(activity, gso)
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
        googleSignInClient.signOut()
    }

    fun handleGoogleSignInResult(data: Intent?){
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try{
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        }catch (e: ApiException){
            Log.e("Iniciar Google", "Iniciar con google ha fallado",e)
        }
    }

    fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    userAutenticado.value = user
                } else {
                    userAutenticado.value = null
                    Log.w(
                        "Fallo Autenticación",
                        "Iniciar con credencial ha fallado",
                        task.exception
                    )
                }


            }
    }

    companion object{
        private const val TAG ="InicioSesionViewModel"
        const val RC_SIGN_IN =9001
    }
}