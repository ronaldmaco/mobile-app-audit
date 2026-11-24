package app.notes.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.notes.data.Note
import app.notes.data.NotesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteEditorViewModel(private val api: NotesApi) : ViewModel() {

    var isSaving = false
        private set

    // Set by the fragment in onStart, cleared in onStop.
    var onSaveResult: ((Result<Note>) -> Unit)? = null

    fun save(title: String, body: String) {
        isSaving = true
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { api.createNote(title, body) }
            }
            isSaving = false
            onSaveResult?.invoke(result)
        }
    }
}
