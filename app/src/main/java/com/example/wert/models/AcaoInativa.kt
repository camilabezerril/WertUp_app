package com.example.wert.models

class AcaoInativa (var key: String, var ticker: String, var nomeAcao: String, var segmento: String, var prazo: String, var alertaCompra: Double,
                   var valorAtual: Double, var valorAnterior: Double, var nAcoes: String, var valorMercado: String, var weekly: Map<String, Double>?,
                   var monthly: Map<String, Double>?, var months: Map<String, Double>?) {
    constructor() : this("", "", "","","",0.0,0.0,0.0,"",
                        "", null,null,null)
}