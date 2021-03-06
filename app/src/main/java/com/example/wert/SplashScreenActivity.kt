package com.example.wert

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wert.models.AcaoAtiva
import com.example.wert.models.AcaoInativa
import com.example.wert.utils.UpdateAcao
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.RuntimeException


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (acao in dataSnapshot.child("AcaoAtiva").children) { //Iterando em acao ativa
                    val acaoAtiva: AcaoAtiva? = acao.getValue(AcaoAtiva::class.java)

                    try {
                        UpdateAcao.updateDynamicData(acaoAtiva!!)
                    } catch (e: RuntimeException){
                        Log.e("SplashScreen","Algo deu errado com a atualização dos dados das ações ativas")
                    }
                }

                for (acao in dataSnapshot.child("AcaoInativa").children) { //Iterando em acao inativa
                    val acaoInativa: AcaoInativa? = acao.getValue(AcaoInativa::class.java)

                    try {
                        UpdateAcao.updateDynamicData(acaoInativa!!)
                    } catch (e: RuntimeException){
                        Log.e("SplashScreen","Algo deu errado com a atualização dos dados das ações inativas")
                    }
                }

                showHomeActivity()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun showHomeActivity(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}