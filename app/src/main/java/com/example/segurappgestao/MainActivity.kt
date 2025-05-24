package com.example.segurappgestao

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SegurAppGestao)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listaLayout = findViewById<LinearLayout>(R.id.listaRegistrosLayout)
        val registros = listOf(
            Pair("Bruno Henrique", "001"),
            Pair("Ana Silva", "002"),
            Pair("Carlos Eduardo", "003")
        )

        for ((nome, numero) in registros) {
            listaLayout.addView(criarCardOcorrencia(nome, numero))
        }

        // Marca o botão Lista como ativo na inicialização
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

    private fun criarCardOcorrencia(nome: String, numero: String): LinearLayout {
        val contexto = this

        val card = LinearLayout(contexto).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundResource(R.drawable.card_background)
            setPadding(32, 32, 32, 32)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 24)
            }
        }

        val texto = TextView(contexto).apply {
            text = "$nome\nNúmero da ocorrência: $numero"
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val seta = ImageView(contexto).apply {
            setImageResource(R.drawable.ic_seta)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        card.addView(texto)
        card.addView(seta)

        card.setOnClickListener {
            Toast.makeText(this, "Abrir detalhes da ocorrência $numero", Toast.LENGTH_SHORT).show()
        }

        return card
    }
}
