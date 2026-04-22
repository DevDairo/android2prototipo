package com.example.musicflow.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicflow.data.api.models.SearchResult
import com.example.musicflow.databinding.ItemSearchResultBinding

class SearchResultsAdapter(
    private val onDownloadClick: (SearchResult) -> Unit
) : ListAdapter<SearchResult, SearchResultsAdapter.ViewHolder>(DiffCallback) {

    // ─── ViewHolder ───────────────────────────────────────────────────────────
    // Mantiene las referencias a las vistas de cada fila.
    // Se crea UNA vez por fila visible y se reutiliza al hacer scroll.

    inner class ViewHolder(
        private val binding: ItemSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(result: SearchResult) {
            // Texto
            binding.txtTitle.text  = result.title
            binding.txtArtist.text = result.artist

            // Imagen: Glide la descarga en background y la cachea automáticamente.
            // placeholder: color gris mientras carga.
            // centerCrop: recorta la imagen para llenar el cuadro sin deformarla.
            Glide.with(binding.imgThumbnail.context)
                .load(result.thumbnail)
                .placeholder(android.R.color.darker_gray)
                .centerCrop()
                .into(binding.imgThumbnail)

            // Botón descargar
            binding.btnDownload.setOnClickListener {
                onDownloadClick(result)
            }
        }
    }

    // ─── Ciclo de vida del RecyclerView ───────────────────────────────────────

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Infla el layout item_search_result.xml usando ViewBinding
        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // ─── DiffCallback ─────────────────────────────────────────────────────────
    // Le dice al RecyclerView exactamente qué filas cambiaron para
    // actualizar solo esas, en lugar de redibujar toda la lista.

    companion object DiffCallback : DiffUtil.ItemCallback<SearchResult>() {

        override fun areItemsTheSame(
            oldItem: SearchResult,
            newItem: SearchResult
        ): Boolean {
            // Dos resultados son "el mismo ítem" si tienen el mismo ID de YouTube
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SearchResult,
            newItem: SearchResult
        ): Boolean {
            // Los contenidos son iguales si todos los campos son idénticos
            return oldItem == newItem
        }
    }
}