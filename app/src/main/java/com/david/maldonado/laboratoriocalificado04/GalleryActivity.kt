package com.david.maldonado.laboratoriocalificado04

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import java.io.File

class GalleryActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2 // Vista que permite navegar entre imágenes
    private lateinit var galleryAdapter: GalleryAdapter // Adaptador que proporciona las imágenes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        // Inicializa el ViewPager2 que permitirá ver las imágenes
        viewPager = findViewById(R.id.view_pager)

        // Obtiene la lista de imágenes desde el directorio
        val imageFiles = getImagesFromDirectory()

        // Configura el adaptador para las imágenes
        galleryAdapter = GalleryAdapter(this, imageFiles)
        viewPager.adapter = galleryAdapter
    }

    // Función para recuperar los archivos de imagen desde un directorio
    private fun getImagesFromDirectory(): List<File> {
        val imagesDir = File(getExternalFilesDir(null), "images") // Ruta del directorio de imágenes
        if (!imagesDir.exists()) {
            imagesDir.mkdirs() // Crea el directorio si no existe
        }
        return imagesDir.listFiles()?.filter { it.isFile && it.extension in listOf("jpg", "png") } ?: emptyList()
    }
}