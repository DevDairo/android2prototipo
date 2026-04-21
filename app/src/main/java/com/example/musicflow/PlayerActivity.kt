package com.example.musicflow

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PlayerActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var posicionActual = 0
    private lateinit var runnable: Runnable
    private var handler = Handler(Looper.getMainLooper())

    private val cancionesRes = arrayOf(R.raw.cancion1, R.raw.cancion2, R.raw.cancion3)
    private val nombres = arrayOf("we can't be friends", "La Quemona", "La Groupie")
    private val artistas = arrayOf(
        "Ariana Grande",
        "Mishelle Master Boys",
        "De La Ghetto ft. Ñejo, Nicky Jam..."
    )
    private val portadas = arrayOf(
        R.drawable.portada1,
        R.drawable.portada2,
        R.drawable.portada3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // Recibir la posición de la canción desde MainActivity
        posicionActual = intent.getIntExtra("posicion", 0)

        // Vincular componentes con el XML
        val btnPlay = findViewById<Button>(R.id.btnPlayPause)
        val btnSig = findViewById<Button>(R.id.btnSiguiente)
        val btnAnt = findViewById<Button>(R.id.btnAnterior)
        val btnAdelanta = findViewById<Button>(R.id.btnAdelantar)
        val btnRetrocede = findViewById<Button>(R.id.btnRetroceder)
        val seekVolumen = findViewById<SeekBar>(R.id.seekBarVolumen)

        configurarReproductor()

        // Lógica de Play/Pause con símbolos cortos para mantener el círculo perfecto
        btnPlay.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                btnPlay.text = "▶"
            } else {
                mediaPlayer?.start()
                btnPlay.text = "ll"
            }
        }

        // Navegación
        btnSig.setOnClickListener { cambiarCancion(1) }
        btnAnt.setOnClickListener { cambiarCancion(-1) }

        // Adelantar y Retroceder 10 segundos
        btnAdelanta.setOnClickListener {
            mediaPlayer?.let { it.seekTo(it.currentPosition + 10000) }
        }
        btnRetrocede.setOnClickListener {
            mediaPlayer?.let { it.seekTo(it.currentPosition - 10000) }
        }

        // Control de Volumen
        seekVolumen.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) {
                val vol = p / 100f
                mediaPlayer?.setVolume(vol, vol)
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })
    }

    private fun configurarReproductor() {
        mediaPlayer?.stop()
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer.create(this, cancionesRes[posicionActual])

        // Actualizar UI con la canción actual
        findViewById<TextView>(R.id.txtNombreCancion).text = nombres[posicionActual]
        findViewById<TextView>(R.id.txtArtista).text = artistas[posicionActual]
        findViewById<ImageView>(R.id.imgPortada).setImageResource(portadas[posicionActual])

        val seekBar = findViewById<SeekBar>(R.id.seekBarTiempo)
        mediaPlayer?.let {
            seekBar.max = it.duration
            it.start()
        }

        // Cambiar el botón a pausa porque empieza sonando
        findViewById<Button>(R.id.btnPlayPause).text = "⏸"

        actualizarProgreso()
    }

    private fun cambiarCancion(direccion: Int) {
        posicionActual = (posicionActual + direccion + cancionesRes.size) % cancionesRes.size
        configurarReproductor()
    }

    private fun actualizarProgreso() {
        val seekBar = findViewById<SeekBar>(R.id.seekBarTiempo)
        runnable = Runnable {
            mediaPlayer?.let {
                seekBar.progress = it.currentPosition
                handler.postDelayed(runnable, 1000)
            }
        }
        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}