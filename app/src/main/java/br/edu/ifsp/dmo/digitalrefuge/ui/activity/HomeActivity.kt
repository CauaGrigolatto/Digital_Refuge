package br.edu.ifsp.dmo.digitalrefuge.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.dmo.digitalrefuge.databinding.ActivityHomeBinding
import br.edu.ifsp.dmo.digitalrefuge.model.Entry
import br.edu.ifsp.dmo.digitalrefuge.ui.adapter.EntryAdapter
import br.edu.ifsp.dmo.digitalrefuge.ui.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var adapter: EntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.fabNewEntry.setOnClickListener {
            startActivity(Intent(this, NewEntryActivity::class.java))
        }

        viewModel.entries.observe(this) { entries ->
            adapter.updateEntries(entries)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadEntries()
    }

    private fun setupRecyclerView() {
        adapter = EntryAdapter(
            onItemClicked = { entry ->
                val intent = Intent(this, EntryDetailsActivity::class.java)
                intent.putExtra("ENTRY_ID", entry.id.toLong()) // Adiciona o ID corretamente!
                startActivity(intent)
            },
            onDeleteClicked = { entry ->
                showDeleteConfirmationDialog {
                    viewModel.deleteEntry(entry)
                }
            },
            onEditClicked = { entry ->
                val intent = Intent(this, NewEntryActivity::class.java)
                intent.putExtra("ENTRY_ID", entry.id.toLong())
                startActivity(intent)
            }
        )
        binding.rvEntries.layoutManager = LinearLayoutManager(this)
        binding.rvEntries.adapter = adapter
    }

    private fun showDeleteConfirmationDialog(onConfirm: () -> Unit) {
        AlertDialog.Builder(this) // 'this' é a Activity ou contexto
            .setTitle("Excluir anotação")
            .setMessage("Tem certeza que deseja excluir esta anotação?")
            .setPositiveButton("Sim") { dialog, _ ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
