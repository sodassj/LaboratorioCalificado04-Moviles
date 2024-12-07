package com.david.maldonado.laboratoriocalificado04

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class GalleryAdapter(
    private val context: Context,
    private val imageFiles: List<File> // Lista de archivos de imágenes
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        // Infla el diseño de cada imagen dentro del RecyclerView
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_img, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        // Carga la imagen en el ImageView usando Glide
        val imageFile = imageFiles[position]
        Glide.with(context)
            .load(imageFile) // Carga la imagen desde el archivo
            .centerCrop() // Recorta la imagen para ajustarse al ImageView
            .into(holder.imageView) // Coloca la imagen en el ImageView
    }

    override fun getItemCount(): Int = imageFiles.size // Retorna la cantidad de imágenes

    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Referencia al ImageView donde se mostrará cada imagen
        val imageView: ImageView = itemView.findViewById(R.id.local_img)
    }
}