package ru.mulledwine.shifts.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mulledwine.shifts.Constants
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.ui.schedules.SchedulesAdapter

class ChooseScheduleDialog: DialogFragment() {

    companion object {
        const val CHOOSE_SCHEDULE_KEY = "CHOOSE_SCHEDULE_KEY"
        const val SELECTED_SCHEDULE = "SELECTED_SCHEDULE"
    }

    private val args: ChooseScheduleDialogArgs by navArgs()

    private val schedulesAdapter = SchedulesAdapter {
        setFragmentResult(CHOOSE_SCHEDULE_KEY, bundleOf(SELECTED_SCHEDULE to it))
        dialog!!.dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        schedulesAdapter.submitList(args.items.toList())

        val rvSchedules = RecyclerView(requireContext()).apply {
            adapter = schedulesAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            setPadding(Constants.dp8, Constants.dp16, Constants.dp8, Constants.dp16)
        }

        return AlertDialog.Builder(requireContext(), R.style.AlabasterAlertDialogTheme)
            .setView(rvSchedules)
            .setTitle(R.string.label_choose_schedule)
            .create()

    }
}