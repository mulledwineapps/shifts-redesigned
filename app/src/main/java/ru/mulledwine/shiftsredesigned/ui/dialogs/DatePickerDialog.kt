package ru.mulledwine.shiftsredesigned.ui.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.extensions.date
import ru.mulledwine.shiftsredesigned.extensions.month
import ru.mulledwine.shiftsredesigned.extensions.year
import ru.mulledwine.shiftsredesigned.utils.Utils

class DatePickerDialog : DialogFragment() {

    companion object {
        const val DATE_PICKER_KEY = "DATE_PICKER_KEY"
        const val PICK_ACTION_KEY = "PICK_ACTION_KEY"
        const val VIEW_KEY = "VIEW_KEY"
    }

    private val args: DatePickerDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

//        val context = ContextThemeWrapper(requireContext(), R.style.PickerDialogTheme)
        val datePicker = DatePicker(requireContext())
        val time = if (args.time == 0L) Constants.today.timeInMillis else args.time
        val calendar = Utils.getCalendarInstance(time)
        datePicker.init(calendar.year, calendar.month, calendar.date, null)

        val dialog = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setView(datePicker)
            .setPositiveButton(R.string.ok_button) { _, _ ->
                calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                setFragmentResult(
                    DATE_PICKER_KEY,
                    bundleOf(PICK_ACTION_KEY to calendar.timeInMillis, VIEW_KEY to args.viewId)
                )
            }
            .setNegativeButton(R.string.cancel_button, null)
            //.setNeutralButton(R.string.today_button, null)
            .create()

//        dialog.setOnShowListener {
//            val neutralBtn = dialog.getButton(DialogInterface.BUTTON_NEUTRAL)
//            neutralBtn.setOnClickListener {
//                with(Constants.today) {
//                    datePicker.updateDate(year, month, date)
//                }
//                calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
//            }
//        }

        return dialog
    }

}