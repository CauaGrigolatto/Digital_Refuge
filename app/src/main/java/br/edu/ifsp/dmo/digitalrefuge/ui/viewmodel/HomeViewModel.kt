package br.edu.ifsp.dmo.digitalrefuge.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.edu.ifsp.dmo.digitalrefuge.database.AppDatabase
import br.edu.ifsp.dmo.digitalrefuge.model.Entry
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val entryDao = AppDatabase.getDatabase(application).entryDao()

    private val _entries = MutableLiveData<List<Entry>>()
    val entries: LiveData<List<Entry>> = _entries

    fun loadEntries() {
        viewModelScope.launch {
            val allEntries = entryDao.getAll()
            val filtered = allEntries.filter { it.photoUri?.startsWith("content://") != true }
            _entries.postValue(filtered)
        }
    }

    fun deleteEntry(entry: Entry) {
        viewModelScope.launch {
            entryDao.delete(entry)
            loadEntries()
        }
    }
}