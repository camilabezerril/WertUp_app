package com.example.wert

import android.R.*
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_cadastrar_acao_ativa.*
import kotlinx.android.synthetic.main.activity_cadastrar_acao_inativa.*
import kotlinx.android.synthetic.main.activity_cadastrar_acao_status.*


class CadastrarAcaoStatusActivity : AppCompatActivity() {
    private lateinit var adapterStatus: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_acao_status)

        label_et_status.visibility = View.INVISIBLE
        setSpinnerOptions()

        btn_avancar_status.setOnClickListener {
            if(!checkFields())
                return@setOnClickListener


            if (spinner_status_cadastro.selectedItem.toString() == "Ativa") {
                val intent = Intent(this, CadastrarAcaoAtivaActivity::class.java)
                val bndlanimation = ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_enter, R.anim.slide_out).toBundle()
                startActivity(intent, bndlanimation)
            } else {
                val intent = Intent(this, CadastrarAcaoInativaActivity::class.java)
                val bndlanimation = ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_enter, R.anim.slide_out).toBundle()
                startActivity(intent, bndlanimation)
            }
        }

        ib_voltar_cadastro_acao_status.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.stay, R.anim.slide_down)
        }

        spinner_status_cadastro.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP -> {
                        setDefaultColorInputs()
                        label_et_status.setTextColor(Color.parseColor("#333333"))
                        rl_spinner_status_cadastro_status.setBackgroundResource(R.drawable.rounded_clicked_fields)
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        spinner_status_cadastro.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                if (selectedItem != "Status*") {
                    label_et_status.visibility = View.VISIBLE
                    btn_avancar_status.setBackgroundColor(Color.parseColor("#5ba87b"))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setDefaultColorInputs(){
        rl_spinner_status_cadastro_status.setBackgroundResource(R.drawable.rounded_fields)
    }

    private fun checkFields() : Boolean {
        if (spinner_status_cadastro.selectedItem.toString() == "Status*"){
            rl_spinner_status_cadastro_status.setBackgroundResource(R.drawable.rounded_wrong_input)

            return false
        }
        return true
    }

    private fun setSpinnerOptions(){
        adapterStatus = object :
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

        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterStatus.add("Ativa")
        adapterStatus.add("Inativa")
        adapterStatus.add("Status*")

        spinner_status_cadastro.adapter = adapterStatus
        spinner_status_cadastro.setSelection(adapterStatus.count)
    }
}
