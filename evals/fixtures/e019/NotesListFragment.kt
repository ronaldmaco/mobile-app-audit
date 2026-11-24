package app.notes.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import app.notes.R
import app.notes.data.NotesApi
import app.notes.databinding.FragmentNotesListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesListFragment : Fragment(R.layout.fragment_notes_list) {

    private lateinit var binding: FragmentNotesListBinding
    private val api: NotesApi by notesApi()
    private val adapter = NotesAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentNotesListBinding.bind(view)
        binding.list.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // Always show what the server has.
        viewLifecycleOwner.lifecycleScope.launch {
            val notes = withContext(Dispatchers.IO) { runCatching { api.listNotes() } }
            notes.onSuccess { adapter.submitList(it) }
        }
    }
}
