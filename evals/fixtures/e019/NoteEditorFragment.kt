package app.notes.editor

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.notes.R
import app.notes.databinding.FragmentNoteEditorBinding

class NoteEditorFragment : Fragment(R.layout.fragment_note_editor) {

    private lateinit var binding: FragmentNoteEditorBinding
    private val viewModel: NoteEditorViewModel by notesViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentNoteEditorBinding.bind(view)
        binding.saveButton.setOnClickListener {
            binding.saveButton.isEnabled = false
            binding.saveButton.text = getString(R.string.saving)
            binding.progress.visibility = View.VISIBLE
            viewModel.save(binding.titleInput.text.toString(), binding.bodyInput.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onSaveResult = { result ->
            binding.progress.visibility = View.GONE
            result
                .onSuccess { findNavController().popBackStack() }
                .onFailure {
                    binding.saveButton.isEnabled = true
                    binding.saveButton.text = getString(R.string.save)
                    Toast.makeText(requireContext(), R.string.save_failed, Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        // Coming back from another app: make sure the user can act again.
        binding.saveButton.isEnabled = true
    }

    override fun onStop() {
        viewModel.onSaveResult = null
        super.onStop()
    }
}
