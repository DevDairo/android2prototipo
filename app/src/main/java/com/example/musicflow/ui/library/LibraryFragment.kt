package com.example.musicflow.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.musicflow.utils.NetworkResult

class LibraryFragment : Fragment() {

    private val viewModel: LibraryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Por ahora retorna null — el layout se implementa
        // en la siguiente fase junto con el RecyclerView de biblioteca
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observar canciones de la biblioteca
        viewModel.songs.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    // Mostrar indicador de carga
                }
                is NetworkResult.Success -> {
                    // Poblar el RecyclerView — se implementa en la siguiente fase
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        result.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}