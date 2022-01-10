package com.example.wert.apiCalls

import com.example.wert.BuildConfig
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection

object AcaoData {
    fun getOverview(ticker: String): JSONObject {
        val url = "https://www.alphavantage.co/query?function=OVERVIEW&symbol=$ticker"
        return ConnectionAlpha.getJSON(url)
    }

    fun getTimeSeriesDaily(ticker: String): JSONObject {
        val url = ("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=$ticker")
        return ConnectionAlpha.getJSON(url)
    }

    fun getTimeSeriesWeekly(ticker: String): JSONObject {
        val url = ("https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY&symbol=$ticker")
        return ConnectionAlpha.getJSON(url)
    }

    fun getTimeSeriesMonthly(ticker: String): JSONObject {
        val url = ("https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol=$ticker")
        return ConnectionAlpha.getJSON(url)
    }
}

private object ConnectionAlpha {
    private val apiKey = BuildConfig.ApiKey

    fun getJSON(url: String): JSONObject {
        val urlAll = "$url&apikey=$apiKey"
        val json = arrayOf(JSONObject())
        val thread = Thread {
            try {
                val urlConn: URLConnection
                val url = URL(urlAll)
                urlConn = url.openConnection()
                val bufferedReader = BufferedReader(InputStreamReader(urlConn.getInputStream()))
                val stringBuffer = StringBuffer()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) stringBuffer.append(line)
                json[0] = JSONObject(stringBuffer.toString())
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        thread.start()
        try {
            thread.join()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return json[0]
    }

}