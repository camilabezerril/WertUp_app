package com.example.wert.utils

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wert.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.vp_item_home.view.*
import java.text.NumberFormat


class VpAdapter(
        private val titulos: List<String>,
        private val graficoValues: List<Map<String, Float>>

) : RecyclerView.Adapter<VpAdapter.VpViewHolder>(){
    inner class VpViewHolder(itemvpView: View) : RecyclerView.ViewHolder(itemvpView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VpViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vp_item_home, parent, false)
        return VpViewHolder(view)
    }

    override fun getItemCount(): Int {
        return titulos.size
    }

    override fun onBindViewHolder(holder: VpViewHolder, position: Int) {
        val curTitle = titulos[position]
        val curValues = graficoValues[position]

        // CONFIGURA ICONES DE SLIDERS DA PAGEVIEW
        if (position == 0){
            holder.itemView.iv_slider_1_vp_home.setImageResource(R.drawable.icon_slider_ativo)
            holder.itemView.iv_slider_2_vp_home.setImageResource(R.drawable.icon_slider_inativo)
        } else {
            holder.itemView.iv_slider_1_vp_home.setImageResource(R.drawable.icon_slider_inativo)
            holder.itemView.iv_slider_2_vp_home.setImageResource(R.drawable.icon_slider_ativo)
        }

        Log.e("VpAdapter", "$curValues")

        holder.itemView.tv_titulo_grafico_vp_item.text = curTitle

        // ADJUSTING PIE
        configurePie(holder.itemView.pie_chart_vp_item)

        //CREATING PIE
        holder.itemView.pie_chart_vp_item.data = createDataPie(curValues)
        holder.itemView.pie_chart_vp_item.invalidate()
    }

    private fun configurePie(pieChart: PieChart) {
        pieChart.setExtraOffsets(25f, 0f, 25f, 7f)
        pieChart.renderer = CustomPieChartRenderer(pieChart, 10f)

        pieChart.setUsePercentValues(true)

        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 50f

        // TEXTO DO CENTRO
        pieChart.setDrawCenterText(false)

        // DESABILITAR DESCRIÇÃO
        pieChart.description = null

        // DESABILITAR LEGENDA DENTRO DOS SLICES
        pieChart.legend.isEnabled = true
        pieChart.legend.textColor = Color.parseColor("#828282")

        // VERTICALIZAR LEGENDA
        pieChart.legend.verticalAlignment= Legend.LegendVerticalAlignment.BOTTOM
        pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        pieChart.legend.setDrawInside(false)

        pieChart.setDrawEntryLabels(false)
    }

    private fun createDataPie(map: Map<String, Float>): PieData{
        //CONDIGURANDO DADOS PARA PIE
        val yEntry: ArrayList<PieEntry> = ArrayList()

        for ((key, value) in map)
            yEntry.add(PieEntry(value, key))

        val pieDataSet = PieDataSet(yEntry, "")
        pieDataSet.sliceSpace = 2f
        pieDataSet.valueTextSize = 0f

        //CORES DO PIECHART
        val colors = listOf(
                Color.parseColor("#eb6e7a"),
                Color.parseColor("#99cf43"),
                Color.parseColor("#4777c0"),
                Color.parseColor("#a374c6"),
                Color.parseColor("#4fb3e8"),
                Color.parseColor("#fdc135"),
                Color.parseColor("#fd9a47"),
                Color.parseColor("#6785c2"))
        pieDataSet.colors = colors
        pieDataSet.setValueTextColors(colors)

        // LINHAS DOS VALORES
        pieDataSet.valueLinePart1Length = 0.6f
        pieDataSet.valueLinePart2Length = 0.3f
        pieDataSet.valueLineWidth = 2f
        pieDataSet.valueLinePart1OffsetPercentage = 115f  // linha começa fora do gráfico
        pieDataSet.isUsingSliceColorAsValueLineColor = true

        // APARÊNCIA DOS VALORES DE TEXTO
        pieDataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        pieDataSet.valueTextSize = 14f
        pieDataSet.valueTypeface = Typeface.DEFAULT_BOLD

        // FORMATAÇÃO DOS VALORES
        pieDataSet.valueFormatter = object : ValueFormatter() {
            private val formatter = NumberFormat.getPercentInstance()

            override fun getFormattedValue(value: Float) =
                    formatter.format(value / 100f)
        }

        pieDataSet.selectionShift = 3f

        //ADICIONANDO DADOS NO PIECHART
        return PieData(pieDataSet)
    }
}