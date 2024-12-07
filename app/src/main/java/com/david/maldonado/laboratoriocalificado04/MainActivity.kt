package com.david.maldonado.laboratoriocalificado04

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView // Vista previa de la cámara
    private lateinit var imgCaptureBtn: Button // Botón para capturar la foto
    private lateinit var switchBtn: Button // Botón para cambiar de cámara
    private lateinit var galleryBtn: Button // Botón para abrir la galería

    private var imageCapture: ImageCapture? = null // Objeto para manejar la captura de imágenes
    private var isUsingFrontCamera = false // Controla si la cámara actual es frontal o trasera
    private var cameraProvider: ProcessCameraProvider? = null // Proveedor de cámara que gestiona las instancias de cámara

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Verificar si se tienen los permisos necesarios
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 101)
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 102)
        }

        // Inicializa las vistas
        previewView = findViewById(R.id.preview)
        imgCaptureBtn = findViewById(R.id.img_capture_btn)
        switchBtn = findViewById(R.id.switch_btn)
        galleryBtn = findViewById(R.id.gallery_btn)

        // Inicia la cámara
        startCamera()

        // Configura los botones
        imgCaptureBtn.setOnClickListener {
            takePhoto() // Toma una foto cuando se presiona el botón
        }

        switchBtn.setOnClickListener {
            switchCamera() // Cambia entre la cámara frontal y trasera
        }

        galleryBtn.setOnClickListener {
            openGallery() // Abre la actividad de galería al presionar el botón
        }
    }

    private fun startCamera() {
        // Obtiene el proveedor de la cámara
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases() // Vincula los casos de uso de la cámara
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases() {
        // Verifica que el proveedor de cámara esté disponible
        val cameraProvider = cameraProvider ?: return

        // Crea un caso de uso para la vista previa
        val preview = androidx.camera.core.Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider) // Muestra la vista previa en el PreviewView
        }

        // Configura la captura de imágenes
        imageCapture = ImageCapture.Builder().build()

        // Selecciona la cámara (frontal o trasera)
        val cameraSelector = if (isUsingFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA // Cámara frontal
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA // Cámara trasera
        }

        try {
            // Desvincula cualquier uso anterior de la cámara y vincula los nuevos casos de uso
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            // Muestra un mensaje de error si ocurre un problema al vincular la cámara
            Toast.makeText(this, "Error starting camera: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun takePhoto() {
        // Captura la imagen si la instancia de ImageCapture es válida
        val imageCapture = imageCapture ?: return

        // Crea el directorio de salida para guardar la imagen
        val outputDir = File(getExternalFilesDir(null), "images")
        if (!outputDir.exists()) {
            outputDir.mkdirs() // Crea el directorio si no existe
        }

        // Genera un nombre único para la imagen
        val fileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg"
        val outputFile = File(outputDir, fileName)

        // Define las opciones de salida para la imagen capturada
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Muestra un mensaje cuando la foto se guarda correctamente
                    Toast.makeText(this@MainActivity, "Photo saved: ${outputFile.absolutePath}", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    // Muestra un mensaje de error si ocurre un problema al capturar la foto
                    Toast.makeText(this@MainActivity, "Error capturing photo: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun switchCamera() {
        // Alterna entre la cámara frontal y trasera
        isUsingFrontCamera = !isUsingFrontCamera
        bindCameraUseCases() // Vuelve a vincular los casos de uso con la nueva cámara seleccionada
    }

    private fun openGallery() {
        // Abre la actividad de galería para ver las fotos tomadas
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }
}