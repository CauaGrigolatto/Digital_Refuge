package br.edu.ifsp.dmo.digitalrefuge.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.dmo.digitalrefuge.databinding.ItemEntryBinding
import br.edu.ifsp.dmo.digitalrefuge.model.Entry

class EntryAdapter(
    private val onItemClicked: (Entry) -> Unit,
    private val onDeleteClicked: (Entry) -> Unit,
    private val onEditClicked: (Entry) -> Unit  // novo callback para editar
) : RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {

    private var entries = listOf<Entry>()

    fun updateEntries(newEntries: List<Entry>) {
        entries = newEntries
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val binding = ItemEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        holder.bind(entries[position])
    }

    override fun getItemCount() = entries.size

    inner class EntryViewHolder(private val binding: ItemEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: Entry) {
            binding.tvEntryDate.text = entry.date
            binding.tvEntrySnippet.text = entry.text

            binding.root.setOnClickListener { onItemClicked(entry) }
            binding.btnDelete.setOnClickListener { onDeleteClicked(entry) }
            binding.btnEdit.setOnClickListener { onEditClicked(entry) }  // aqui
        }
    }
}