package com.example.segurappgestao

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RelatorioActivity : AppCompatActivity() {

    private var dataInicial: Date? = null
    private var dataFinal: Date? = null
    private lateinit var btnDataInicial: Button
    private lateinit var btnDataFinal: Button
    private lateinit var btnBuscar: Button
    private lateinit var txtData: TextView
    private lateinit var txtPeriodo: TextView
    private lateinit var txtTotal: TextView
    private lateinit var txtCategorias: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_relatorio)
        atualizarMenuAtivo(isListaAtivo = true)

        btnDataInicial = findViewById<Button>(R.id.btnDataInicial)
        btnDataFinal = findViewById<Button>(R.id.btnDataFinal)
        btnBuscar = findViewById<Button>(R.id.btnBuscar)
        txtData = findViewById<TextView>(R.id.txtData)
        txtPeriodo = findViewById<TextView>(R.id.txtPeriodo)
        txtTotal = findViewById<TextView>(R.id.txtTotal)
        txtCategorias = findViewById<TextView>(R.id.txtCategorias)

        btnDataInicial.setOnClickListener { selecionarDataInicial() }
        btnDataFinal.setOnClickListener { selecionarDataFinal() }
        btnBuscar.setOnClickListener { buscarRelatorios() }

        atualizarMenuAtivo(isListaAtivo = true)

        val bottomNav = findViewById<ConstraintLayout>(R.id.bottom_nav)
        val btnMapa = bottomNav.findViewById<LinearLayout>(R.id.btnMapa)
        val btnLista = bottomNav.findViewById<LinearLayout>(R.id.btnLista)

        btnMapa.setOnClickListener {
            startActivity(Intent(this, MapaActivity::class.java))
            finish()
        }

        btnLista.setOnClickListener {
            Toast.makeText(this, "Você já está na tela Lista", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selecionarDataInicial() {
        val c = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth, 0, 0, 0)
            dataInicial = calendar.time
            Toast.makeText(this, "Data inicial: $dataInicial", Toast.LENGTH_SHORT).show()
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun selecionarDataFinal() {
        val c = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth, 23, 59, 59)
            dataFinal = calendar.time
            Toast.makeText(this, "Data final: $dataFinal", Toast.LENGTH_SHORT).show()
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun buscarRelatorios() {
        if (dataInicial == null || dataFinal == null) {
            Toast.makeText(this, "Selecione as duas datas!", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val ocorrenciasRef = db.collection("Ocorrencias")

        // Esconder os botões e mostrar os textos
        btnBuscar.visibility = Button.GONE
        btnDataInicial.visibility = Button.GONE
        btnDataFinal.visibility = Button.GONE
        txtData.visibility = TextView.VISIBLE
        txtPeriodo.visibility = TextView.VISIBLE
        txtTotal.visibility = TextView.VISIBLE
        txtCategorias.visibility = TextView.VISIBLE

        // Datas formatadas
        val hoje = Calendar.getInstance().time
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dataFormatada = formato.format(hoje)
        val dataInicialFormatada = formato.format(dataInicial)
        val dataFinalFormatada = formato.format(dataFinal)

        txtData.text = "Data do relatório: $dataFormatada"
        txtPeriodo.text = "Período do relatório: $dataInicialFormatada - $dataFinalFormatada"

        ocorrenciasRef
            .whereGreaterThanOrEqualTo("timestamp", dataInicial!!)
            .whereLessThanOrEqualTo("timestamp", dataFinal!!)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val total = querySnapshot.size()
                txtTotal.text = "Total de Riscos: $total"

                var riscoEletrico = 0
                var obstrucao = 0
                var outro = 0

                for (doc in querySnapshot) {
                    when (doc.getString("categoria")) {
                        "Risco Elétrico" -> riscoEletrico++
                        "Obstrução" -> obstrucao++
                        else -> outro++
                    }
                }

                val detalhes = """
                Risco Elétrico: $riscoEletrico
                Obstrução: $obstrucao
                Outro: $outro
            """.trimIndent()

                txtCategorias.text = detalhes
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao buscar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
    private fun atualizarMenuAtivo(isListaAtivo: Boolean) {
        val bottomNav = findViewById<ConstraintLayout>(R.id.bottom_nav)
        val btnMapa = bottomNav.findViewById<LinearLayout>(R.id.btnMapa)
        val btnLista = bottomNav.findViewById<LinearLayout>(R.id.btnLista)

        val iconLista = btnLista.findViewById<ImageView>(R.id.iconLista)
        val iconMapa = btnMapa.findViewById<ImageView>(R.id.iconMapa)

        if (isListaAtivo) {
            // Ícone Lista branco (ativo)
            iconLista.setColorFilter(ContextCompat.getColor(this, android.R.color.white))
            // Ícone Mapa preto (inativo)
            iconMapa.setColorFilter(ContextCompat.getColor(this, android.R.color.black))
            // Ambos os botões mantêm o fundo azul definido no XML
            // Não altera background aqui!
        } else {
            // Ícone Mapa branco (ativo)
            iconMapa.setColorFilter(ContextCompat.getColor(this, android.R.color.white))
            // Ícone Lista preto (inativo)
            iconLista.setColorFilter(ContextCompat.getColor(this, android.R.color.black))
            // Mantém fundo azul fixo para ambos os botões
        }
    }

}
