package com.example.wert

import android.R.*
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
import com.example.wert.models.AcaoInativa
import com.example.wert.utils.UpdateAcao
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_cadastrar_acao_ativa.*
import kotlinx.android.synthetic.main.activity_cadastrar_acao_inativa.*
import kotlinx.android.synthetic.main.activity_cadastrar_acao_status.*
import java.util.*


class CadastrarAcaoInativaActivity : AppCompatActivity() {
    private lateinit var adapterPrazo: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_acao_inativa)

        setClickedColorsListeners()
        setSpinnersOptions()
        setCheckConfirm()

        alert_cadastrar_acao_inativa.visibility = View.INVISIBLE
        label_ticker_cadastro_inativa.visibility = View.INVISIBLE
        label_nome_acao_cadastro_inativa.visibility = View.INVISIBLE
        label_alerta_compra_cadastro_inativa.visibility = View.INVISIBLE
        label_prazo_cadastro_inativa.visibility = View.INVISIBLE

        ib_voltar_cadastro_acao_inativa.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_left_to_right, R.anim.slide_right_to_left)
        }

        btn_confirmar_inativa.setOnClickListener {
            if (!checkWrongInputs()) {
                alert_cadastrar_acao_inativa.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val ticker = et_ticker_cadastrar_inativa.text.toString()
            val nomeAcao = et_nome_cadastrar_acao_inativa.text.toString()
            val prazo = spinner_prazo_cadastro_inativa.selectedItem.toString()
            val alertaCompra = et_alerta_compra_cadastrar_inativa.text.toString().toDouble()

            try {
                val databaseRef = FirebaseDatabase.getInstance().getReference("AcaoInativa") // Especificar uid quando houver sistema de login
                val key = databaseRef.push().key!!

                val novaAcao = AcaoInativa()
                novaAcao.key = key
                novaAcao.ticker = ticker
                novaAcao.nomeAcao = nomeAcao
                novaAcao.prazo = prazo
                novaAcao.alertaCompra = alertaCompra

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
        et_ticker_cadastrar_inativa.addTextChangedListener {
            label_ticker_cadastro_inativa.visibility = View.VISIBLE

            if(checkFieldsConfirm())
                btn_confirmar_inativa.setBackgroundColor(Color.parseColor("#5ba87b"))
            else
                btn_confirmar_inativa.setBackgroundColor(Color.parseColor("#BDBDBD"))
        }
        et_nome_cadastrar_acao_inativa.addTextChangedListener {
            label_nome_acao_cadastro_inativa.visibility = View.VISIBLE

            if(checkFieldsConfirm())
                btn_confirmar_inativa.setBackgroundColor(Color.parseColor("#5ba87b"))
            else
                btn_confirmar_inativa.setBackgroundColor(Color.parseColor("#BDBDBD"))
        }
        et_alerta_compra_cadastrar_inativa.addTextChangedListener {
            label_alerta_compra_cadastro_inativa.visibility = View.VISIBLE

            if(checkFieldsConfirm())
                btn_confirmar_inativa.setBackgroundColor(Color.parseColor("#5ba87b"))
            else
                btn_confirmar_inativa.setBackgroundColor(Color.parseColor("#BDBDBD"))
        }

        spinner_prazo_cadastro_inativa.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                if (selectedItem != "Prazo de investimento*") {
                    label_prazo_cadastro_inativa.visibility = View.VISIBLE

                    if (checkFieldsConfirm())
                        btn_confirmar_inativa.setBackgroundColor(Color.parseColor("#5ba87b"))
                    else
                        btn_confirmar_inativa.setBackgroundColor(Color.parseColor("#BDBDBD"))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun checkFieldsConfirm() : Boolean {
        if (!et_ticker_cadastrar_inativa.text.toString().isEmpty() &&
                !et_nome_cadastrar_acao_inativa.text.toString().isEmpty() &&
                !et_alerta_compra_cadastrar_inativa.text.toString().isEmpty() &&
                spinner_prazo_cadastro_inativa.selectedItem.toString() != "Prazo*")
            return true
        return false
    }

    private fun checkWrongInputs() : Boolean {
        var rightInput = true

        if (et_ticker_cadastrar_inativa.text.toString().isEmpty()){
            et_ticker_cadastrar_inativa.setBackgroundResource(R.drawable.rounded_wrong_input)
            et_ticker_cadastrar_inativa.setHintTextColor(Color.parseColor("#FB2424"))
            label_ticker_cadastro_inativa.visibility = View.INVISIBLE
            rightInput = false
        }
        if (et_nome_cadastrar_acao_inativa.text.toString().isEmpty()){
            et_nome_cadastrar_acao_inativa.setBackgroundResource(R.drawable.rounded_wrong_input)
            et_nome_cadastrar_acao_inativa.setHintTextColor(Color.parseColor("#FB2424"))
            label_nome_acao_cadastro_inativa.visibility = View.INVISIBLE
            rightInput = false
        }
        if (et_alerta_compra_cadastrar_inativa.text.toString().isEmpty()){
            et_alerta_compra_cadastrar_inativa.setBackgroundResource(R.drawable.rounded_wrong_input)
            et_alerta_compra_cadastrar_inativa.setHintTextColor(Color.parseColor("#FB2424"))
            label_alerta_compra_cadastro_inativa.visibility = View.INVISIBLE
            rightInput = false
        }
        if (spinner_prazo_cadastro_inativa.selectedItem.toString() == "Prazo*") {
            rl_spinner_prazo_cadastro_inativa.setBackgroundResource(R.drawable.rounded_wrong_input)
            label_prazo_cadastro_inativa.visibility = View.INVISIBLE
            rightInput = false
        }

        return rightInput
    }

    private fun setSpinnersOptions() {
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

        spinner_prazo_cadastro_inativa.adapter = adapterPrazo
        spinner_prazo_cadastro_inativa.setSelection(adapterPrazo.count)
    }

    private fun setDefaultColorInputs(){
        alert_cadastrar_acao_inativa.visibility = View.INVISIBLE
        et_ticker_cadastrar_inativa.setHintTextColor(Color.parseColor("#828282"))
        et_ticker_cadastrar_inativa.setBackgroundResource(R.drawable.rounded_fields)
        et_nome_cadastrar_acao_inativa.setHintTextColor(Color.parseColor("#828282"))
        et_nome_cadastrar_acao_inativa.setBackgroundResource(R.drawable.rounded_fields)
        et_alerta_compra_cadastrar_inativa.setHintTextColor(Color.parseColor("#828282"))
        et_alerta_compra_cadastrar_inativa.setBackgroundResource(R.drawable.rounded_fields)
        rl_spinner_prazo_cadastro_inativa.setBackgroundResource(R.drawable.rounded_fields)
    }

    private fun setClickedColorsListeners(){
        et_ticker_cadastrar_inativa.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP -> {
                        setDefaultColorInputs()
                        et_ticker_cadastrar_inativa.setBackgroundResource(R.drawable.rounded_clicked_fields)
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        et_nome_cadastrar_acao_inativa.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP -> {
                        setDefaultColorInputs()
                        et_nome_cadastrar_acao_inativa.setBackgroundResource(R.drawable.rounded_clicked_fields)
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        et_alerta_compra_cadastrar_inativa.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP -> {
                        setDefaultColorInputs()
                        et_alerta_compra_cadastrar_inativa.setBackgroundResource(R.drawable.rounded_clicked_fields)
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        spinner_prazo_cadastro_inativa.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP -> {
                        setDefaultColorInputs()
                        rl_spinner_prazo_cadastro_inativa.setBackgroundResource(R.drawable.rounded_clicked_fields)
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })
    }

}