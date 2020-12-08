package ru.mulledwine.shiftsredesigned.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.DataHolder

class ColorPickerDialog : DialogFragment() {

    companion object {
        private const val TAG = "M_ColorPickerDialog"
        const val COLOR_PICKER_KEY = "COLOR_PICKER_KEY"
        const val PICK_ACTION_KEY = "PICK_ACTION_KEY"
    }

    private val args: ColorPickerDialogArgs by navArgs()

    private val colorsAdapter = ColorsAdapter {
        setFragmentResult(
            COLOR_PICKER_KEY,
            bundleOf(PICK_ACTION_KEY to it.color)
        )
        dialog!!.dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        colorsAdapter.submitList(DataHolder.getColors(requireContext()))

        val rvColors = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = colorsAdapter
            setPadding(0, Constants.dp8, 0, Constants.dp16)
        }

        return AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setView(rvColors)
            .setTitle(args.titleRes)
            .create()

    }

}