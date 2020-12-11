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
import ru.mulledwine.shiftsredesigned.ui.shifttypes.ShiftTypesAdapter

class ChooseShiftTypeDialog : DialogFragment() {

    companion object {
        const val CHOOSE_SHIFT_TYPE_KEY = "CHOOSE_SHIFT_TYPE_KEY"
        const val SELECTED_SHIFT_TYPE = "SELECTED_SHIFT_TYPE"
        const val CURRENT_ITEM_ID = "CURRENT_ITEM_ID"
    }

    private val args: ChooseShiftTypeDialogArgs by navArgs()

    private val shiftsTypesAdapter = ShiftTypesAdapter {
        setFragmentResult(
            CHOOSE_SHIFT_TYPE_KEY,
            bundleOf(SELECTED_SHIFT_TYPE to it, CURRENT_ITEM_ID to args.currentItemId)
        )
        dialog!!.dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        shiftsTypesAdapter.submitList(args.shiftTypes.toList())

        val rvShiftTypes = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = shiftsTypesAdapter
            setPadding(Constants.dp8, Constants.dp8, Constants.dp8, Constants.dp16)
        }

        return AlertDialog.Builder(requireContext(), R.style.LightAlertDialogTheme)
            .setView(rvShiftTypes)
            .setTitle(R.string.label_choose_shift)
            .create()

    }

}