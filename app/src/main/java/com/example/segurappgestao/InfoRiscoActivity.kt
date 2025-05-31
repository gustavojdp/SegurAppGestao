package com.example.segurappgestao

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.BitmapFactory
import android.os.StrictMode
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import java.net.URL


class InfoRiscoActivity : AppCompatActivity() {

    private lateinit var txtEmail: TextView
    private lateinit var txtId: TextView
    private lateinit var txtLocal: TextView
    private lateinit var txtDescricao: TextView
    private lateinit var txtCategoria: TextView
    private lateinit var imgOcorrencia: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_risco)

        // Vincula os TextViews
        txtEmail = findViewById(R.id.txtEmail)
        txtId = findViewById(R.id.txtId)
        txtLocal = findViewById(R.id.txtLocal)
        txtDescricao = findViewById(R.id.txtDescricao)
        txtCategoria = findViewById(R.id.txtCategoria)
        imgOcorrencia = findViewById(R.id.imgOcorrencia)

        // Configura o botão de voltar
        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }

        // Pega o ID enviado pela outra Activity
        val idOcorrencia = intent.getStringExtra("id")

        if (idOcorrencia != null) {
            buscarDadosOcorrencia(idOcorrencia)
        } else {
            Toast.makeText(this, "ID da ocorrência não recebido.", Toast.LENGTH_SHORT).show()
        }

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

    private fun buscarDadosOcorrencia(id: String) {
        val db = FirebaseFirestore.getInstance()
        val ocorrenciasRef = db.collection("Ocorrencias").document(id)

        ocorrenciasRef.get()
            .addOnSuccessListener { documento ->
                if (documento.exists()) {
                    val email = documento.getString("email") ?: "Não informado"
                    val local = documento.getString("localizacao") ?: "Não informado"
                    val descricao = documento.getString("descricao") ?: "Não informado"
                    val categoria = documento.getString("categoria") ?: "Não informado"
                    val imagemUrl = documento.getString("imagem")

                    txtEmail.text = "Email do colaborador: $email"
                    txtId.text = "ID da ocorrência: $id"
                    txtLocal.text = "Local: $local"
                    txtDescricao.text = "Descrição: $descricao"
                    txtCategoria.text = "Categoria: $categoria"

                    if (!imagemUrl.isNullOrEmpty()) {
                        Thread {
                            try {
                                val url = URL(imagemUrl)
                                val connection = url.openConnection()
                                connection.doInput = true
                                connection.connect()
                                val inputStream = connection.getInputStream()
                                val bitmap = BitmapFactory.decodeStream(inputStream)

                                runOnUiThread {
                                    imgOcorrencia.setImageBitmap(bitmap)
                                }
                            } catch (e: Exception) {
                                runOnUiThread {
                                    imgOcorrencia.setImageResource(android.R.drawable.ic_dialog_alert)

                                }
                                e.printStackTrace()
                            }
                        }.start()
                    } else {
                        imgOcorrencia.setImageResource(android.R.drawable.ic_dialog_alert)

                    }

                } else {
                    Toast.makeText(this, "Ocorrência não encontrada.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao buscar dados: ${erro.message}", Toast.LENGTH_LONG).show()
                Log.e("InfoRiscoActivity", "Erro Firebase", erro)
            }
    }
}