package com.example.segurappgestao

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.segurappgestao.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var btnRelatorio: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_SegurAppGestao)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnRelatorio = findViewById(R.id.btnRelatorio)

        btnRelatorio.setOnClickListener {
            val intent = Intent(this, RelatorioActivity::class.java)
            startActivity(intent)
        }

        val db = FirebaseFirestore.getInstance()
        val ocorrenciasCollectionRef = db.collection("Ocorrencias")

        data class OcorrenciaInfo(
            val email: String,
            val id: String
        )
        val listaOcorrencias = mutableListOf<OcorrenciaInfo>()

        ocorrenciasCollectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val email = document.getString("email") ?: "Sem email"
                    val id = document.id
                    val ocorrencia = OcorrenciaInfo(email, id)
                    listaOcorrencias.add(ocorrencia)
                }

                // Adiciona um card para cada ocorrência na tela
                for (ocorrencia in listaOcorrencias) {
                    val card = criarCardOcorrencia(ocorrencia.email, ocorrencia.id)
                    binding.listaRegistrosLayout.addView(card)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("FIREBASE", "Erro ao obter documentos", exception)
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
        } else {
            // Ícone Mapa branco (ativo)
            iconMapa.setColorFilter(ContextCompat.getColor(this, android.R.color.white))
            // Ícone Lista preto (inativo)
            iconLista.setColorFilter(ContextCompat.getColor(this, android.R.color.black))
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
            val intent = Intent(contexto, InfoRiscoActivity::class.java).apply {
                putExtra("id", numero)
            }
            contexto.startActivity(intent)
        }

        return card
    }
}
