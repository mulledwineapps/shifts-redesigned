package ru.mulledwine.shifts.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import ru.mulledwine.shifts.Constants
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.extensions.date
import ru.mulledwine.shifts.extensions.month
import ru.mulledwine.shifts.extensions.toCalendar
import ru.mulledwine.shifts.extensions.year

class DatePickerDialog : DialogFragment() {

    companion object {
        const val DATE_PICKER_KEY = "DATE_PICKER_KEY"
        const val PICK_ACTION_KEY = "PICK_ACTION_KEY"
        const val VIEW_KEY = "VIEW_KEY"
    }

    private val args: DatePickerDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val context = ContextThemeWrapper(requireContext(), R.style.DatePickerTheme)
        val datePicker = DatePicker(context)
        val time = if (args.time == 0L) Constants.today.timeInMillis else args.time
        val calendar = time.toCalendar()
        datePicker.init(calendar.year, calendar.month, calendar.date, null)

        return AlertDialog.Builder(requireContext(), R.style.DarkAlertDialogTheme)
            .setView(datePicker)
            .setPositiveButton(R.string.ok_button) { _, _ ->
                calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                setFragmentResult(
                    DATE_PICKER_KEY,
                    bundleOf(PICK_ACTION_KEY to calendar.timeInMillis, VIEW_KEY to args.viewId)
                )
            }
            .setNegativeButton(R.string.cancel_button, null)
            .create()
    }

}