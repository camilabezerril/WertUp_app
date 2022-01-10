package com.example.wert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wert.models.AcaoAtiva
import com.example.wert.utils.VpAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_home.*
import java.text.NumberFormat
import java.util.*


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottom_nav_view.setOnNavigationItemSelectedListener(configureMenu())

        getValues()
    }

    private fun getValues(){
        val inv: MutableMap<String, Float> = HashMap()
        val roi: MutableMap<String, Float> = HashMap()

        var saldoInvestido = 0.0
        var saldoRoi = 0.0
        var emAlta = 0.0
        var emQueda = 0.0
        var lucroPctg = 0.0

        FirebaseDatabase.getInstance().reference.child("AcaoAtiva").addListenerForSingleValueEvent(
            object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (acao in dataSnapshot.children) { //Iterando em acao ativa
                        val novaAcao = acao.getValue(AcaoAtiva::class.java)

                        if (inv.containsKey(novaAcao!!.segmento))
                            inv[novaAcao.segmento] = inv[novaAcao.segmento]!! + novaAcao.totalInvestido.toFloat()
                        else
                            inv[novaAcao.segmento] = novaAcao.totalInvestido.toFloat()

                        if (roi.containsKey(novaAcao.segmento)) {
                            roi[novaAcao.segmento] = roi[novaAcao.segmento]!! + novaAcao.saldoAtual.toFloat()
                        } else {
                            roi[novaAcao.segmento] = novaAcao.saldoAtual.toFloat()
                        }

                        saldoInvestido += novaAcao.totalInvestido
                        saldoRoi += novaAcao.saldoAtual

                        if (novaAcao.perdaGanhoP > 0) {
                            emAlta += novaAcao.perdaGanhoR
                        } else {
                            emQueda += novaAcao.perdaGanhoR
                        }
                    }

                    val lucro = saldoRoi - saldoInvestido

                    if (saldoRoi != 0.0)
                        lucroPctg = lucro * 100/saldoRoi

                    setValuesOnView(saldoInvestido, lucro, lucroPctg, saldoRoi, emAlta, emQueda, roi, inv)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun setValuesOnView(saldoInvestido: Double, lucro: Double, lucroPctg: Double, saldoRoi: Double,
                                emAlta: Double, emQueda: Double, roi: MutableMap<String, Float>,
                                inv: MutableMap<String, Float>) {

        Locale.setDefault(Locale("en", "US"))
        val percentageFormat = NumberFormat.getNumberInstance(Locale("pt","BR"))
        percentageFormat.maximumFractionDigits = 2

        tv_dinheiro_lucro_home.text = NumberFormat.getCurrencyInstance().format(lucro).toString()
        tv_dinheiro_saldo_home.text = NumberFormat.getCurrencyInstance().format(saldoInvestido).toString()
        tv_dinheiro_saldo_roi_home.text = NumberFormat.getCurrencyInstance().format(saldoRoi).toString()
        tv_dinheiro_acoes_em_alta_home.text = NumberFormat.getCurrencyInstance().format(emAlta).toString()
        tv_dinheiro_acoes_em_baixa_home.text = NumberFormat.getCurrencyInstance().format(emQueda).toString()
        tv_pctg_lucro_home.text = percentageFormat.format(lucroPctg).toString() + "%"

        val titulos = listOf("Saldo(ROI) por segmento", "Investimento por segmento")

        val adapter = VpAdapter(titulos, listOf(roi, inv))
        vp_graficos_home.adapter = adapter
    }

    private fun configureMenu(): BottomNavigationView.OnNavigationItemSelectedListener {
        bottom_nav_view.selectedItemId = R.id.menu_home

        return BottomNavigationView.OnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.menu_home -> {
                    return@OnNavigationItemSelectedListener false
                }
                R.id.menu_acoes -> {
                    val intent = Intent(this, AcoesActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                R.id.menu_alertas -> {
                    //val intent = Intent(this, AlertasActivity::class.java)
                    //startActivity(intent)
                }
            }
            false
        }
    }

}