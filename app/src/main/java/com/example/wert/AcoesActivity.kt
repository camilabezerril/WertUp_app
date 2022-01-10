package com.example.wert

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.wert.models.AcaoAtiva
import com.example.wert.models.AcaoInativa
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_acoes.*
import kotlinx.android.synthetic.main.activity_acoes.bottom_nav_view
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.rv_item_acao.view.*
import java.text.NumberFormat
import java.util.*


class AcoesActivity : AppCompatActivity() {

    private val adapterRv = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acoes)

        bottom_nav_view.setOnNavigationItemSelectedListener(configureMenu())

        criarListaAcoes()
        rv_acoes.adapter = adapterRv

        ib_adicionar_acao_acoes.setOnClickListener {
            val intent = Intent(this, CadastrarAcaoStatusActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_up, R.anim.stay)
        }
    }

    private fun criarListaAcoes(){
        FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (acao in dataSnapshot.child("AcaoAtiva").children) { //Iterando em acao ativa
                    val acaoAtiva: AcaoAtiva? = acao.getValue(AcaoAtiva::class.java)

                    val pctg = (acaoAtiva?.valorAtual!! * 100 / acaoAtiva.valorAnterior) - 100

                    adapterRv.add(ItemAcao(acaoAtiva.nomeAcao, acaoAtiva.valorAtual, acaoAtiva.valorAnterior, pctg, "Ativa"))
                }

                for (acao in dataSnapshot.child("AcaoInativa").children) { //Iterando em acao inativa
                    val acaoInativa: AcaoInativa? = acao.getValue(AcaoInativa::class.java)

                    val pctg = (acaoInativa?.valorAtual!! * 100 / acaoInativa.valorAnterior) - 100

                    adapterRv.add(ItemAcao(acaoInativa.nomeAcao, acaoInativa.valorAtual, acaoInativa.valorAnterior, pctg, "Inativa"))
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    class ItemAcao(val nomeAcao: String, var valorAtual: Double, var valorAnterior: Double,
                   var pctg: Double, val statusAcao: String) : Item<GroupieViewHolder>() {

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            Log.e("AcoesActivity", "Setting view holder to $nomeAcao")

            if (statusAcao == "Inativa")
                viewHolder.itemView.tv_status_acao_item.setTextColor(Color.parseColor("#828282"))

            if (valorAtual > valorAnterior) {
                viewHolder.itemView.tv_pctg_acao_item.setTextColor(Color.parseColor("#6FCF97"))
                viewHolder.itemView.tv_pctg_acao_item.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.seta_up_simples_icon, 0)
            }

            Locale.setDefault(Locale("en", "US"))
            val percentageFormat = NumberFormat.getNumberInstance(Locale("pt","BR"))
            percentageFormat.maximumFractionDigits = 2

            viewHolder.itemView.tv_nome_acao_item.text = nomeAcao
            viewHolder.itemView.tv_dinheiro_valor_atual_item.text = NumberFormat.getCurrencyInstance().format(valorAtual)
            viewHolder.itemView.tv_dinheiro_valor_anterior_item.text = NumberFormat.getCurrencyInstance().format(valorAnterior)
            viewHolder.itemView.tv_pctg_acao_item.text = percentageFormat.format(pctg) + "%"
            viewHolder.itemView.tv_status_acao_item.text = statusAcao

        }

        override fun getLayout() = R.layout.rv_item_acao
    }

    private fun configureMenu(): BottomNavigationView.OnNavigationItemSelectedListener {
        bottom_nav_view.selectedItemId = R.id.menu_acoes

        return BottomNavigationView.OnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.menu_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                R.id.menu_acoes -> {
                    return@OnNavigationItemSelectedListener false
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