package ru.mulledwine.shiftsredesigned.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.ClockTime

class TimePickerDialog : DialogFragment() {

    companion object {
        const val TIME_PICKER_KEY = "TIME_PICKER_KEY"
        const val PICK_ACTION_KEY = "PICK_ACTION_KEY"
        const val VIEW_KEY = "VIEW_KEY"
    }

    private val args: TimePickerDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val timePicker = TimePicker(requireContext()).apply {
            setIs24HourView(true)
            hour = args.time.hour
            minute = args.time.minute
        }

        return AlertDialog.Builder(requireContext(), R.style.LightAlertDialogTheme)
            .setView(timePicker)
            .setPositiveButton(R.string.ok_button) { _, _ ->
                val pickedTime = ClockTime(timePicker.hour, timePicker.minute)
                setFragmentResult(
                    TIME_PICKER_KEY,
                    bundleOf(PICK_ACTION_KEY to pickedTime, VIEW_KEY to args.viewId)
                )
            }
            .setNegativeButton(R.string.cancel_button, null)
            .create()
    }

}