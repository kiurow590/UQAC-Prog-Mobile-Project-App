// app/src/main/java/com/example/uqac_progmob_project/AccountDialogFragment.kt
package com.example.uqac_progmob_project

import android.app.Dialog
import android.os.Bundle
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import com.example.uqac_progmob_project.databinding.DialogAccountBinding

class AccountDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAccountBinding.inflate(layoutInflater)

        // Configurez les informations de l'utilisateur ici
        binding.userPseudo.text = "UserPseudo"
        // on utilise une string pour le score concaténé avec le score
        binding.globalScore.text = getString(R.string.global_score) + " 0"

        // Set up the close button to dismiss the dialog
        binding.root.findViewById<ImageButton>(R.id.back_button2).setOnClickListener {
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }
}