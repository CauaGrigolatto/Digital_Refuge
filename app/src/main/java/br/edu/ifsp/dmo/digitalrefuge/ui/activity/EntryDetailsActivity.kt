package br.edu.ifsp.dmo.digitalrefuge.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.edu.ifsp.dmo.digitalrefuge.database.AppDatabase
import br.edu.ifsp.dmo.digitalrefuge.databinding.ActivityEntryDetailsBinding
import kotlinx.coroutines.launch
import java.io.File

class EntryDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntryDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val entryId = intent.getLongExtra("ENTRY_ID", -1L)
        if (entryId != -1L) {
            loadEntry(entryId)
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun loadEntry(id: Long) {
        lifecycleScope.launch {
            val dao = AppDatabase.getDatabase(applicationContext).entryDao()
            val entry = dao.findById(id)
            if (entry == null) {
                Toast.makeText(this@EntryDetailsActivity, "Anotação não encontrada (ID: $id)", Toast.LENGTH_LONG).show()
            } else {
                binding.tvEntryDate.text = entry.date
                binding.tvEntryText.text = entry.text

                entry.photoUri?.let { path ->
                    try {
                        val file = File(path)
                        if (file.exists()) {
                            val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                            binding.ivEntryPhoto.setImageBitmap(bitmap)
                            binding.ivEntryPhoto.visibility = android.view.View.VISIBLE
                        } else {
                            Toast.makeText(this@EntryDetailsActivity, "Arquivo da imagem não encontrado.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@EntryDetailsActivity, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}