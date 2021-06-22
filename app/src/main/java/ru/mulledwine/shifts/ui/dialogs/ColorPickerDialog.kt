package ru.mulledwine.shifts.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mulledwine.shifts.Constants
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.DataHolder

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

        return AlertDialog.Builder(requireContext(), R.style.LightAlertDialogTheme)
            .setView(rvColors)
            .setTitle(args.titleRes)
            .create()

    }

}