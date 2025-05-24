package com.example.segurappgestao

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.segurappgestao.databinding.ActivityMapaBinding

class MapaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajusta padding para sistema de barras (status, navegação)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        atualizarMenuAtivo(isMapaAtivo = true)

        val bottomNav = binding.bottomNav
        val btnMapa = bottomNav.btnMapa
        val btnLista = bottomNav.btnLista

        btnMapa.setOnClickListener {
            Toast.makeText(this, "Você já está na tela Mapa", Toast.LENGTH_SHORT).show()
        }

        btnLista.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun atualizarMenuAtivo(isMapaAtivo: Boolean) {
        val btnMapa = binding.bottomNav.btnMapa
        val btnLista = binding.bottomNav.btnLista

        val iconLista = btnLista.findViewById<ImageView>(R.id.iconLista)
        val iconMapa = btnMapa.findViewById<ImageView>(R.id.iconMapa)

        if (isMapaAtivo) {
            // Ícone Mapa branco (ativo)
            iconMapa.setColorFilter(ContextCompat.getColor(this, android.R.color.white))
            // Ícone Lista preto (inativo)
            iconLista.setColorFilter(ContextCompat.getColor(this, android.R.color.black))
            // Fundo azul fixo definido no XML para ambos os botões
        } else {
            // Ícone Lista branco (ativo)
            iconLista.setColorFilter(ContextCompat.getColor(this, android.R.color.white))
            // Ícone Mapa preto (inativo)
            iconMapa.setColorFilter(ContextCompat.getColor(this, android.R.color.black))
        }
    }
}
