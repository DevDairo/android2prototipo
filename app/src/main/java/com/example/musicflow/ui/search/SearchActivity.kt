package com.example.musicflow.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicflow.databinding.ActivitySearchBinding
import com.example.musicflow.utils.NetworkResult

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: SearchResultsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchBar()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = SearchResultsAdapter { result ->
            // Al pulsar el botón de descarga de un resultado
            viewModel.downloadSong(result.url)
            binding.downloadPanel.visibility = View.VISIBLE
            binding.txtDownloadTitle.text = "Descargando: ${result.title}"
        }
        binding.recyclerResults.layoutManager = LinearLayoutManager(this)
        binding.recyclerResults.adapter = adapter
    }

    private fun setupSearchBar() {
        // Mostrar/ocultar botón X según el texto
        binding.editSearch.addTextChangedListener { text ->
            binding.btnClear.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        // Buscar al pulsar Enter en el teclado
        binding.editSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                performSearch()
                true
            } else false
        }

        binding.btnClear.setOnClickListener {
            binding.editSearch.text?.clear()
            adapter.submitList(emptyList())
            binding.txtStatus.visibility = View.GONE
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun performSearch() {
        val query = binding.editSearch.text.toString().trim()
        if (query.isEmpty()) return

        // Ocultar teclado
        val imm = getSystemService(InputMethodManager::class.java)
        imm.hideSoftInputFromWindow(binding.editSearch.windowToken, 0)

        viewModel.search(query)
    }

    private fun observeViewModel() {
        // Observar resultados de búsqueda
        viewModel.searchResults.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    binding.txtStatus.visibility = View.VISIBLE
                    binding.txtStatus.text = "Buscando..."
                }
                is NetworkResult.Success -> {
                    binding.txtStatus.visibility = View.GONE
                    if (result.data.isEmpty()) {
                        binding.txtStatus.visibility = View.VISIBLE
                        binding.txtStatus.text = "No se encontraron resultados."
                    }
                    adapter.submitList(result.data)
                }
                is NetworkResult.Error -> {
                    binding.txtStatus.visibility = View.VISIBLE
                    binding.txtStatus.text = "Error: ${result.message}"
                }
            }
        }

        // Observar estado de descarga
        viewModel.downloadStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    binding.downloadPanel.visibility = View.VISIBLE
                    binding.progressDownload.isIndeterminate = true
                }
                is NetworkResult.Success -> {
                    val status = result.data
                    binding.progressDownload.isIndeterminate = false
                    binding.progressDownload.progress = status.progress.toInt()
                    binding.txtDownloadPercent.text = "${status.progress.toInt()}%"

                    when (status.status) {
                        "done" -> {
                            Toast.makeText(this, "✓ Descarga completada", Toast.LENGTH_SHORT).show()
                            // Ocultar panel después de 2 segundos
                            binding.downloadPanel.postDelayed({
                                binding.downloadPanel.visibility = View.GONE
                            }, 2000)
                        }
                        "error" -> {
                            Toast.makeText(this, "Error: ${status.message}", Toast.LENGTH_LONG).show()
                            binding.downloadPanel.visibility = View.GONE
                        }
                        "processing" -> {
                            binding.txtDownloadTitle.text = status.message
                            binding.progressDownload.isIndeterminate = true
                        }
                    }
                }
                is NetworkResult.Error -> {
                    binding.downloadPanel.visibility = View.GONE
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}