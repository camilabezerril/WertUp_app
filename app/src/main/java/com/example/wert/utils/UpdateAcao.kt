package com.example.wert.utils

import com.example.wert.apiCalls.AcaoData
import com.example.wert.models.AcaoAtiva
import com.example.wert.models.AcaoInativa
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat


object UpdateAcao {
    private var weekly: HashMap<String, Double>? = null
    private var monthly: HashMap<String, Double>? = null
    private var months: HashMap<String, Double>? = null

    fun updateDynamicData(acao: AcaoAtiva) {
        val overview = AcaoData.getOverview(acao.ticker)
        val timeSeriesDaily = AcaoData.getTimeSeriesDaily(acao.ticker)

        // verifica se ticker existe (essa verificação só deve ocorrer no momento da criação da ação
        // por isso há a segunda checagem
        if(!timeSeriesDaily.has("Time Series (Daily)") && acao.weekly == null)
            throw RuntimeException()

        try {
            acao.segmento = overview.getString("Sector")

            val dias: JSONObject = timeSeriesDaily.getJSONObject("Time Series (Daily)")
            val diasKeys = dias.keys()

            var key = diasKeys.next()
            acao.valorAtual = (dias[key] as JSONObject).getString("4. close").toDouble()

            key = diasKeys.next()
            acao.valorAnterior = (dias[key] as JSONObject).getString("4. close").toDouble()

            acao.perdaGanhoR = acao.qtdComprada * acao.valorAtual - acao.totalInvestido
            acao.saldoAtual = acao.totalInvestido + acao.perdaGanhoR
            acao.perdaGanhoP = acao.saldoAtual * 100 / acao.totalInvestido - 100

            updateSparkline(acao)

            // Atualizar firebase com novas informações
            FirebaseDatabase.getInstance().getReference("AcaoAtiva").child(acao.key).setValue(acao)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun updateDynamicData(acao: AcaoInativa) {
        val overview = AcaoData.getOverview(acao.ticker)
        val timeSeriesDaily = AcaoData.getTimeSeriesDaily(acao.ticker)

        // verifica se ticker existe (essa verificação só deve ocorrer no momento da criação da ação
        // por isso há a segunda checagem
        if(!timeSeriesDaily.has("Time Series (Daily)") && acao.weekly == null)
            throw RuntimeException()

        try {
            acao.segmento = overview.getString("Sector")

            var bd = BigDecimal(overview.getString("MarketCapitalization"))
            acao.valorMercado = bd.toString()

            bd = BigDecimal(overview.getString("SharesOutstanding"))
            acao.nAcoes = bd.toString()

            val dias = timeSeriesDaily.getJSONObject("Time Series (Daily)")
            val diasKeys = dias.keys()

            var key = diasKeys.next()
            acao.valorAtual = (dias[key] as JSONObject).getString("4. close").toDouble()

            key = diasKeys.next()
            acao.valorAnterior = (dias[key] as JSONObject).getString("4. close").toDouble()

            updateSparkline(acao)

            // Atualizar firebase com novas informações
            FirebaseDatabase.getInstance().getReference("AcaoInativa").child(acao.key).setValue(acao)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun updateSparkline(acao: AcaoInativa) {
        val timeSeriesDaily = AcaoData.getTimeSeriesDaily(acao.ticker)
        getInfoSpark(timeSeriesDaily)
        acao.weekly = weekly
        acao.monthly = monthly
        acao.months = months
    }

    private fun updateSparkline(acao: AcaoAtiva) {
        val timeSeriesDaily = AcaoData.getTimeSeriesDaily(acao.ticker)
        getInfoSpark(timeSeriesDaily)
        acao.weekly = weekly
        acao.monthly = monthly
        acao.months = months
    }

    private fun getInfoSpark(timeSeriesDaily: JSONObject) {
        weekly = hashMapOf()
        monthly = hashMapOf()
        months = hashMapOf()

        try {
            val dias = timeSeriesDaily.getJSONObject("Time Series (Daily)")

            var key: String?

            // *********** WEEK ********** //
            var diasKeys: Iterator<String?> = dias.keys()
            var i = 0
            while (diasKeys.hasNext() && i != 6) {
                key = diasKeys.next()
                weekly!![key!!] = (dias[key] as JSONObject).getString("4. close").toDouble()
                i++
            }

            // *********** MONTH ************ //
            i = 0
            diasKeys = dias.keys()
            while (diasKeys.hasNext() && i != 29) {
                key = diasKeys.next()
                monthly!![key!!] = (dias[key] as JSONObject).getString("4. close").toDouble()
                i++
            }

            // *********** 3 Meses ************ //
            i = 0
            diasKeys = dias.keys()
            while (diasKeys.hasNext() && i != 89) {
                key = diasKeys.next()
                months!![key!!] = (dias[key] as JSONObject).getString("4. close").toDouble()
                i++
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}