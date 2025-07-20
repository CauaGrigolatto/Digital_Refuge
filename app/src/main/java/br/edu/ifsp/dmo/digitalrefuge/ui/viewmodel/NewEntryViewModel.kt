package br.edu.ifsp.dmo.digitalrefuge.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.edu.ifsp.dmo.digitalrefuge.database.AppDatabase
import br.edu.ifsp.dmo.digitalrefuge.model.Entry
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewEntryViewModel(application: Application) : AndroidViewModel(application) {
    private val entryDao = AppDatabase.getDatabase(application).entryDao()

    private val _entrySaved = MutableLiveData<Boolean>()
    val entrySaved: LiveData<Boolean> get() = _entrySaved

    private val _entry = MutableLiveData<Entry?>()
    val entry: LiveData<Entry?> get() = _entry

    fun loadEntryById(id: Long) {
        viewModelScope.launch {
            val loadedEntry = entryDao.findById(id)
            _entry.postValue(loadedEntry)
        }
    }

    fun saveEntry(text: String, photoUri: String?) {
        val currentDate = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("pt", "BR")).format(Date())
        val newEntry = Entry(date = currentDate, text = text, photoUri = photoUri)

        viewModelScope.launch {
            entryDao.insert(newEntry)
            _entrySaved.postValue(true)
        }
    }

    fun updateEntry(id: Long, text: String, photoUri: String?) {
        viewModelScope.launch {
            val existingEntry = entryDao.findById(id)
            if (existingEntry != null) {
                val updatedEntry = existingEntry.copy(
                    text = text,
                    photoUri = photoUri
                )
                entryDao.update(updatedEntry)
                _entrySaved.postValue(true)
            } else {
                _entrySaved.postValue(false)
            }
        }
    }
}
