package com.example.wert.models

class AcaoAtiva (var key: String, var ticker: String, var nomeAcao: String, var segmento: String,
                 var prazo: String, var qtdComprada: Int, var precoCompra: Double, var dataCompra: String,
                 var totalInvestido: Double, var alertaVenda: Double, var perdaGanhoP: Double, var perdaGanhoR: Double,
                 var saldoAtual: Double, var valorAtual: Double, var valorAnterior: Double,
                 var weekly: Map<String, Double>?, var monthly: Map<String, Double>?, var months: Map<String, Double>?) {

    constructor() : this("", "", "","","",0,0.0,"",0.0,
            0.0, 0.0,0.0,0.0, 0.0, 0.0, null, null, null)
}
