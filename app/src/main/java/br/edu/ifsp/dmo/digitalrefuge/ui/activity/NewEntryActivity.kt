package br.edu.ifsp.dmo.digitalrefuge.ui.activity

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import br.edu.ifsp.dmo.digitalrefuge.databinding.ActivityNewEntryBinding
import br.edu.ifsp.dmo.digitalrefuge.ui.viewmodel.NewEntryViewModel
import java.io.File

class NewEntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewEntryBinding
    private val viewModel: NewEntryViewModel by viewModels()
    private var photoUri: Uri? = null
    private var photoFile: File? = null
    private var entryId: Long = -1L

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) openCamera() else
                Toast.makeText(this, "Permissão para usar a câmera negada", Toast.LENGTH_SHORT).show()
        }

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val localPath = copyPhotoToInternalStorage(it)
            photoFile = File(localPath)
            photoUri = Uri.fromFile(photoFile)
            loadPhotoPreview(localPath)
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && photoFile != null && photoFile!!.exists()) {
            photoUri = Uri.fromFile(photoFile)
            loadPhotoPreview(photoFile!!.absolutePath)
        } else {
            Toast.makeText(this, "Erro ao capturar a foto.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File {
        val fileName = "pic_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, fileName)
        photoFile = file
        return file
    }

    private fun openCamera() {
        val file = createImageFile()
        // A Uri para passar para a câmera via FileProvider
        photoUri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        takePictureLauncher.launch(photoUri!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        entryId = intent.getLongExtra("ENTRY_ID", -1L)
        if (entryId != -1L) {
            viewModel.loadEntryById(entryId)
        }

        viewModel.entry.observe(this) { entry ->
            entry?.let {
                binding.etEntryText.setText(it.text)

                if (!it.photoUri.isNullOrBlank()) {
                    photoFile = File(it.photoUri)
                    if (photoFile!!.exists()) {
                        photoUri = Uri.fromFile(photoFile)
                        loadPhotoPreview(photoFile!!.absolutePath)
                    } else {
                        Toast.makeText(this, "Arquivo da foto não encontrado.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnCamera.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }

        binding.btnGallery.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            val text = binding.etEntryText.text.toString()
            if (text.isNotBlank()) {
                if (entryId != -1L) {
                    viewModel.updateEntry(entryId, text, photoFile?.absolutePath)
                } else {
                    viewModel.saveEntry(text, photoFile?.absolutePath)
                }
            } else {
                Toast.makeText(this, "O texto não pode estar vazio.", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.entrySaved.observe(this) { saved ->
            if (saved) {
                Toast.makeText(this, "Anotação salva!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun copyPhotoToInternalStorage(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val fileName = "entry_photo_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, fileName)
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file.absolutePath
    }

    private fun loadPhotoPreview(path: String) {
        val bitmap = android.graphics.BitmapFactory.decodeFile(path)
        if (bitmap != null) {
            binding.ivPhotoPreview.setImageBitmap(bitmap)
            binding.ivPhotoPreview.visibility = android.view.View.VISIBLE
        } else {
            Toast.makeText(this, "Erro ao carregar imagem.", Toast.LENGTH_SHORT).show()
        }
    }
}
