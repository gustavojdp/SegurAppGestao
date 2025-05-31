package com.example.segurappgestao

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.segurappgestao.databinding.ActivityMapaBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class MapaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapaBinding
    private lateinit var mMap: GoogleMap
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        binding = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura o mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_container) as SupportMapFragment? ?: run {
            val fragment = SupportMapFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.map_container, fragment)
                .commit()
            fragment
        }
        mapFragment.getMapAsync(this)

        // Atualiza o menu inferior
        atualizarMenuAtivo(isMapaAtivo = true)

        // Adicionar clique no botão "Lista"
        val btnLista = binding.bottomNav.btnLista
        btnLista.setOnClickListener {
            // Navegar para a MainActivity (Lista de Registros)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Definir posição inicial do mapa
        val initialLocation = LatLng(-22.9826, -47.1013)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 10f))

        // Buscar locais do Firestore e adicionar pinos
        db.collection("Ocorrencias")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val categoria = document.getString("categoria")
                    val descricao = document.getString("descricao")
                    val localizacao = document.getString("localizacao") ?: "Não disponível"

                    // Log para verificar o que está sendo retornado do Firestore
                    Log.d("MapaActivity", "Ocorrência: categoria=$categoria, descricao=$descricao, localizacao=$localizacao")

                    val latLon = extrairCoordenadas(localizacao)
                    if (latLon != null) {
                        val (latitude, longitude) = latLon
                        val location = LatLng(latitude, longitude)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(location)
                                .title("Categoria: $categoria")
                                .snippet("Descrição: $descricao\nLocalização: $localizacao")
                        )
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao buscar dados: ${exception.message}", Toast.LENGTH_LONG).show()
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
        } else {
            // Ícone Lista branco (ativo)
            iconLista.setColorFilter(ContextCompat.getColor(this, android.R.color.white))
            // Ícone Mapa preto (inativo)
            iconMapa.setColorFilter(ContextCompat.getColor(this, android.R.color.black))
        }
    }

    // Função para extrair latitude e longitude de "Lat: <latitude>, Lon: <longitude>"
    private fun extrairCoordenadas(localizacao: String): Pair<Double, Double>? {
        val regex = """Lat:\s*(-?\d+\.\d+),\s*Lon:\s*(-?\d+\.\d+)""".toRegex()
        val matchResult = regex.find(localizacao)

        return if (matchResult != null) {
            val (lat, lon) = matchResult.destructured
            // Adiciona um log para verificar os valores
            Log.d("MapaActivity", "Coordenadas extraídas: Lat=$lat, Lon=$lon")
            Pair(lat.toDouble(), lon.toDouble())
        } else {
            // Log caso não consiga extrair as coordenadas
            Log.d("MapaActivity", "Erro ao extrair coordenadas de: $localizacao")
            null
        }
    }
}
