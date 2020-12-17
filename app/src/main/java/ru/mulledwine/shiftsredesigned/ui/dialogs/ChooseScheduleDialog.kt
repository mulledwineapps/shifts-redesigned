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
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleShort
import ru.mulledwine.shiftsredesigned.ui.schedules.SchedulesAdapter

class ChooseScheduleDialog: DialogFragment()  {

    companion object {
        const val CHOOSE_SCHEDULE_KEY = "CHOOSE_SCHEDULE_KEY"
        const val SELECTED_SCHEDULE = "SELECTED_SCHEDULE"
    }

    private val args: ChooseScheduleDialogArgs by navArgs()

    private val schedulesAdapter = SchedulesAdapter {
        val schedule = ScheduleShort(it.id, it.name, it.isCyclic)
        setFragmentResult(CHOOSE_SCHEDULE_KEY, bundleOf(SELECTED_SCHEDULE to schedule))
        dialog!!.dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        schedulesAdapter.submitList(args.schedules.toList())

        val rvSchedules = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = schedulesAdapter
            setPadding(Constants.dp8, Constants.dp8, Constants.dp8, Constants.dp16)
        }

        return AlertDialog.Builder(requireContext(), R.style.LightAlertDialogTheme)
            .setView(rvSchedules)
            .setTitle(R.string.label_choose_schedule)
            .create()

    }
}