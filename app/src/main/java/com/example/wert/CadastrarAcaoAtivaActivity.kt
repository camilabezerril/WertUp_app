package com.example.wert

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.wert.models.AcaoAtiva
import com.example.wert.utils.UpdateAcao
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_cadastrar_acao_ativa.*
import kotlinx.android.synthetic.main.activity_cadastrar_acao_inativa.*
import kotlinx.android.synthetic.main.activity_cadastrar_acao_status.*
import java.text.SimpleDateFormat
import java.util.*


class CadastrarAcaoAtivaActivity : AppCompatActivity() {
    private lateinit var adapterPrazo: ArrayAdapter<String>
    private lateinit var adapterQtdComprada: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_acao_ativa)

        setClickedColorsListeners()
        setSpinnersOptions()
        setCheckConfirm()

        alert_cadastrar_acao_ativa.visibility = View.INVISIBLE
        label_ticker_cadastro_ativa.visibility = View.INVISIBLE
        label_nome_acao_cadastro_ativa.visibility = View.INVISIBLE
        label_preco_compra_cadastro_ativa.visibility = View.INVISIBLE
        label_alerta_venda_cadastro_ativa.visibility = View.INVISIBLE
        label_qtd_comprada_cadastro_ativa.visibility = View.INVISIBLE
        label_prazo_cadastro_ativa.visibility = View.INVISIBLE

        ib_voltar_cadastro_acao_ativa.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_left_to_right, R.anim.slide_right_to_left)
        }

        btn_confirmar_ativa.setOnClickListener {
            if (!checkWrongInputs()) {
                alert_cadastrar_acao_ativa.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val ticker = et_ticker_cadastrar_ativa.text.toString()
            val nomeAcao = et_nome_cadastrar_acao_ativa.text.toString()
            val precoCompra = et_preco_compra_cadastrar_ativa.text.toString().toDouble()
            val prazo = spinner_prazo_cadastro_ativa.selectedItem.toString()
            val qtdComprada = spinner_qtd_comprada_cadastro_ativa.selectedItem.toString().split(' ')[0].toInt()
            val alertaVenda = et_alerta_venda_cadastrar_ativa.text.toString().toDouble()
            val totalInvestido = (qtdComprada * precoCompra).toString().toDouble()
            val dataCompra = SimpleDateFormat.getDateInstance().format(Date())

            try {
                val databaseRef = FirebaseDatabase.getInstance().getReference("AcaoAtiva") // Especificar uid quando houver sistema de login
                val key = databaseRef.push().key!!

                val novaAcao = AcaoAtiva()
                novaAcao.key = key
                novaAcao.ticker = ticker
                novaAcao.nomeAcao = nomeAcao
                novaAcao.precoCompra = precoCompra
                novaAcao.prazo = prazo
                novaAcao.qtdComprada = qtdComprada
                novaAcao.alertaVenda = alertaVenda
                novaAcao.totalInvestido = totalInvestido
                novaAcao.dataCompra = dataCompra

                UpdateAcao.updateDynamicData(novaAcao)

                databaseRef.child(key).setValue(novaAcao)

                val intent = Intent(this, AcoesActivity::class.java)
                startActivity(intent)

            } catch (e: RuntimeException) {
                val toast = Toast.makeText(this, "Ticker inválido", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM, 0, 70)
                toast.show()

                return@setOnClickListener
            }
        }
    }
    
    private fun setCheckConfirm() {
        et_ticker_cadastrar_ativa.addTextChangedListener {
            label_ticker_cadastro_ativa.visibility = View.VISIBLE

            if(checkFieldsConfirm())
                btn_confirmar_ativa.setBackgroundColor(Color.parseColor("#5ba87b"))
            else
                btn_confirmar_ativa.setBackgroundColor(Color.parseColor("#BDBDBD"))
        }
        et_nome_cadastrar_acao_ativa.addTextChangedListener {
            label_nome_acao_cadastro_ativa.visibility = View.VISIBLE

            if(checkFieldsConfirm())
                btn_confirmar_ativa.setBackgroundColor(Color.parseColor("#5ba87b"))
            else
                btn_confirmar_ativa.setBackgroundColor(Color.parseColor("#BDBDBD"))
        }
        et_preco_compra_cadastrar_ativa.addTextChangedListener {
            label_preco_compra_cadastro_ativa.visibility = View.VISIBLE

            if(checkFieldsConfirm())
                btn_confirmar_ativa.setBackgroundColor(Color.parseColor("#5ba87b"))
            else
                btn_confirmar_ativa.setBackgroundColor(Color.parseColor("#BDBDBD"))
        }
        et_alerta_venda_cadastrar_ativa.addTextChangedListener {
            label_alerta_venda_cadastro_ativa.visibility = View.VISIBLE

            if(checkFieldsConfirm())
                btn_confirmar_ativa.setBackgroundColor(Color.parseColor("#5ba87b"))
            else
                btn_confirmar_ativa.setBackgroundColor(Color.parseColor("#BDBDBD"))
        }

        spinner_qtd_comprada_cadastro_ativa.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                if (selectedItem != "Quantidade comprada*") {
                    label_qtd_comprada_cadastro_ativa.visibility = View.VISIBLE

                    if (checkFieldsConfirm())
                        btn_confirmar_ativa.setBackgroundColor(Color.parseColor("#5ba87b"))
                    else
                        btn_confirmar_ativa.setBackgroundColor(Color.parseColor("#BDBDBD"))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        spinner_prazo_cadastro_ativa.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                if (selectedItem != "Prazo de investimento*") {
                    label_prazo_cadastro_ativa.visibility = View.VISIBLE

                    if (checkFieldsConfirm())
                        btn_confirmar_ativa.setBackgroundColor(Color.parseColor("#5ba87b"))
                    else
                        btn_confirmar_ativa.setBackgroundColor(Color.parseColor("#BDBDBD"))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
    }

    private fun checkFieldsConfirm() : Boolean{
        if (!et_ticker_cadastrar_ativa.text.toString().isEmpty() &&
                !et_nome_cadastrar_acao_ativa.text.toString().isEmpty() &&
                !et_preco_compra_cadastrar_ativa.text.toString().isEmpty() &&
                !et_alerta_venda_cadastrar_ativa.text.toString().isEmpty() &&
                spinner_qtd_comprada_cadastro_ativa.selectedItem.toString() != "Quantidade comprada*" &&
                spinner_prazo_cadastro_ativa.selectedItem.toString() != "Prazo de investimento*")
            return true
        return false
    }

    private fun checkWrongInputs() : Boolean {
        var rightInput = true

        if (et_ticker_cadastrar_ativa.text.toString().isEmpty()){
            et_ticker_cadastrar_ativa.setBackgroundResource(R.drawable.rounded_wrong_input)
            et_ticker_cadastrar_ativa.setHintTextColor(Color.parseColor("#FB2424"))
            label_ticker_cadastro_ativa.visibility = View.INVISIBLE
            rightInput = false
        }
        if (et_nome_cadastrar_acao_ativa.text.toString().isEmpty()){
            et_nome_cadastrar_acao_ativa.setBackgroundResource(R.drawable.rounded_wrong_input)
            et_nome_cadastrar_acao_ativa.setHintTextColor(Color.parseColor("#FB2424"))
            label_nome_acao_cadastro_ativa.visibility = View.INVISIBLE
            rightInput = false
        }
        if (et_preco_compra_cadastrar_ativa.text.toString().isEmpty()){
            et_preco_compra_cadastrar_ativa.setBackgroundResource(R.drawable.rounded_wrong_input)
            et_preco_compra_cadastrar_ativa.setHintTextColor(Color.parseColor("#FB2424"))
            label_preco_compra_cadastro_ativa.visibility = View.INVISIBLE
            rightInput = false
        }
        if (et_alerta_venda_cadastrar_ativa.text.toString().isEmpty()){
            et_alerta_venda_cadastrar_ativa.setBackgroundResource(R.drawable.rounded_wrong_input)
            et_alerta_venda_cadastrar_ativa.setHintTextColor(Color.parseColor("#FB2424"))
            label_alerta_venda_cadastro_ativa.visibility = View.INVISIBLE
            rightInput = false
        }
        if (spinner_qtd_comprada_cadastro_ativa.selectedItem.toString() == "Quantidade comprada*"){
            rl_spinner_qtd_comprada_cadastro_ativa.setBackgroundResource(R.drawable.rounded_wrong_input)
            label_qtd_comprada_cadastro_ativa.visibility = View.INVISIBLE
            rightInput = false
        }
        if (spinner_prazo_cadastro_ativa.selectedItem.toString() == "Prazo de investimento*") {
            rl_spinner_prazo_cadastro_ativa.setBackgroundResource(R.drawable.rounded_wrong_input)
            label_prazo_cadastro_ativa.visibility = View.INVISIBLE
            rightInput = false
        }

        return rightInput
    }

    private fun setSpinnersOptions(){
        // ------------------ ADAPTER PARA SPINNER PRAZO -------------- //

        adapterPrazo = object :
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v: View = super.getView(position, convertView, parent)
                if (position == count) {
                    (v.findViewById(android.R.id.text1) as TextView).text = ""
                    (v.findViewById(android.R.id.text1) as TextView).hint = getItem(count) //"Hint"
                }
                return v
            }

            override fun getCount(): Int {
                return super.getCount() - 1  // Não mostra último item, usado como hint
            }
        }

        adapterPrazo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        adapterPrazo.add("Curto Prazo")
        adapterPrazo.add("Longo Prazo")
        adapterPrazo.add("Prazo de investimento*")

        spinner_prazo_cadastro_ativa.adapter = adapterPrazo
        spinner_prazo_cadastro_ativa.setSelection(adapterPrazo.count)

        // ------------------ ADAPTER PARA SPINNER QTD COMPRADA -------------- //

        adapterQtdComprada = object :
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v: View = super.getView(position, convertView, parent)
                if (position == count) {
                    (v.findViewById(android.R.id.text1) as TextView).text = ""
                    (v.findViewById(android.R.id.text1) as TextView).hint = getItem(count) //"Hint"
                }
                return v
            }

            override fun getCount(): Int {
                return super.getCount() - 1  // Não mostra último item, usado como hint
            }
        }

        adapterQtdComprada.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        for (i in 1..50) {
            if (i != 1)
                adapterQtdComprada.add("$i unidades")
            else
                adapterQtdComprada.add("$i unidade")
        }

        adapterQtdComprada.add("Quantidade comprada*")

        spinner_qtd_comprada_cadastro_ativa.adapter = adapterQtdComprada
        spinner_qtd_comprada_cadastro_ativa.setSelection(adapterQtdComprada.count)
    }

    private fun setDefaultColorInputs() {
        alert_cadastrar_acao_ativa.visibility = View.INVISIBLE
        label_ticker_cadastro_ativa.setTextColor(Color.parseColor("#828282"))
        et_ticker_cadastrar_ativa.setHintTextColor(Color.parseColor("#828282"))
        et_ticker_cadastrar_ativa.setBackgroundResource(R.drawable.rounded_fields)
        label_nome_acao_cadastro_ativa.setTextColor(Color.parseColor("#828282"))
        et_nome_cadastrar_acao_ativa.setHintTextColor(Color.parseColor("#828282"))
        et_nome_cadastrar_acao_ativa.setBackgroundResource(R.drawable.rounded_fields)
        label_alerta_venda_cadastro_ativa.setTextColor(Color.parseColor("#828282"))
        et_alerta_venda_cadastrar_ativa.setHintTextColor(Color.parseColor("#828282"))
        et_alerta_venda_cadastrar_ativa.setBackgroundResource(R.drawable.rounded_fields)
        label_preco_compra_cadastro_ativa.setTextColor(Color.parseColor("#828282"))
        et_preco_compra_cadastrar_ativa.setHintTextColor(Color.parseColor("#828282"))
        et_preco_compra_cadastrar_ativa.setBackgroundResource(R.drawable.rounded_fields)
        label_prazo_cadastro_ativa.setTextColor(Color.parseColor("#828282"))
        rl_spinner_prazo_cadastro_ativa.setBackgroundResource(R.drawable.rounded_fields)
        label_qtd_comprada_cadastro_ativa.setTextColor(Color.parseColor("#828282"))
        rl_spinner_qtd_comprada_cadastro_ativa.setBackgroundResource(R.drawable.rounded_fields)
    }

    private fun setClickedColorsListeners(){
        et_ticker_cadastrar_ativa.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP -> {
                        setDefaultColorInputs()
                        et_ticker_cadastrar_ativa.setBackgroundResource(R.drawable.rounded_clicked_fields)
                        label_ticker_cadastro_ativa.setTextColor(Color.parseColor("#333333"))
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        et_nome_cadastrar_acao_ativa.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP -> {
                        setDefaultColorInputs()
                        et_nome_cadastrar_acao_ativa.setBackgroundResource(R.drawable.rounded_clicked_fields)
                        label_nome_acao_cadastro_ativa.setTextColor(Color.parseColor("#333333"))
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        et_preco_compra_cadastrar_ativa.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP -> {
                        setDefaultColorInputs()
                        et_preco_compra_cadastrar_ativa.setBackgroundResource(R.drawable.rounded_clicked_fields)
                        label_preco_compra_cadastro_ativa.setTextColor(Color.parseColor("#333333"))
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        et_alerta_venda_cadastrar_ativa.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP -> {
                        setDefaultColorInputs()
                        et_alerta_venda_cadastrar_ativa.setBackgroundResource(R.drawable.rounded_clicked_fields)
                        label_alerta_venda_cadastro_ativa.setTextColor(Color.parseColor("#333333"))
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        spinner_qtd_comprada_cadastro_ativa.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP -> {
                        setDefaultColorInputs()
                        rl_spinner_qtd_comprada_cadastro_ativa.setBackgroundResource(R.drawable.rounded_clicked_fields)
                        label_qtd_comprada_cadastro_ativa.setTextColor(Color.parseColor("#333333"))
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        spinner_prazo_cadastro_ativa.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP -> {
                        setDefaultColorInputs()
                        rl_spinner_prazo_cadastro_ativa.setBackgroundResource(R.drawable.rounded_clicked_fields)
                        label_prazo_cadastro_ativa.setTextColor(Color.parseColor("#333333"))
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })
    }
}