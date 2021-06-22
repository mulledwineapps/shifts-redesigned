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

class ChooseShiftDialog : DialogFragment() {

    companion object {
        const val CHOOSE_SHIFT_KEY = "CHOOSE_SHIFT_KEY"
        const val SELECTED_SHIFT = "SELECTED_SHIFT"
    }

    private val args: ChooseShiftDialogArgs by navArgs()

    private val shiftsAdapter = ShiftsAdapter {
        setFragmentResult(CHOOSE_SHIFT_KEY, bundleOf(SELECTED_SHIFT to it))
        dialog!!.dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        shiftsAdapter.submitList(args.items.toList())

        val rvShift = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = shiftsAdapter
            setPadding(0, Constants.dp8, 0, Constants.dp16)
        }

        return AlertDialog.Builder(requireContext(), R.style.LightAlertDialogTheme)
            .setView(rvShift)
            .setTitle(R.string.label_choose_shift)
            .create()

    }

}