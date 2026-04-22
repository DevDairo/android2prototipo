package com.example.musicflow

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.musicflow.ui.search.SearchActivity

class MainActivity : AppCompatActivity() {

    private lateinit var lista: ListView

    private val nombres = arrayOf(
        "we can't be friends",
        "La Quemona",
        "La Groupie"
    )
    private val artistas = arrayOf(
        "Ariana Grande",
        "Mishelle Master Boys",
        "De La Ghetto"
    )
    private val fotos = arrayOf(
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_gallery
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lista = findViewById(R.id.listaCanciones)

        // Botón de búsqueda → abre SearchActivity
        findViewById<ImageButton>(R.id.btnSearch).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        val adapter = object : ArrayAdapter<String>(this, R.layout.item_cancion, nombres) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val vista = layoutInflater.inflate(R.layout.item_cancion, parent, false)
                vista.findViewById<ImageView>(R.id.imgMiniatura)
                    .setImageResource(fotos[position])
                vista.findViewById<TextView>(R.id.txtNombreFila).text = nombres[position]
                vista.findViewById<TextView>(R.id.txtArtistaFila).text = artistas[position]
                return vista
            }
        }

        lista.adapter = adapter

        lista.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("posicion", position)
            startActivity(intent)
        }
    }
}