package ru.mulledwine.shifts.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import ru.mulledwine.shifts.Constants
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.CalendarGenerator
import ru.mulledwine.shifts.data.local.models.Month
import ru.mulledwine.shifts.data.local.models.Months

class ChooseMonthDialog : DialogFragment() {

    companion object {
        const val CHOOSE_MONTH_KEY = "CHOOSE_MONTH_KEY"
        const val SELECTED_MONTH = "SELECTED_MONTH"
    }

    private val args: ChooseMonthDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialogView = View.inflate(requireContext(), R.layout.dialog_month_picker, null)
        val monthPicker = dialogView.findViewById<NumberPicker>(R.id.picker_month)
        val yearPicker = dialogView.findViewById<NumberPicker>(R.id.picker_year)

        val months = Months.values().map { it.getName(requireContext()) }

        monthPicker.minValue = 0
        monthPicker.maxValue = months.lastIndex
        monthPicker.displayedValues = months.toTypedArray()
        monthPicker.value = args.month.number

        yearPicker.minValue = Constants.firstYear
        yearPicker.maxValue = Constants.lastYear
        yearPicker.value = args.month.year

        return AlertDialog.Builder(requireContext(), R.style.LightAlertDialogTheme)
            .setView(dialogView)
            .setTitle(R.string.label_choose_month)
            .setPositiveButton(R.string.ok_button) { _, _ ->
                val month = Month(monthPicker.value, yearPicker.value)
                setFragmentResult(CHOOSE_MONTH_KEY, bundleOf(SELECTED_MONTH to month))
            }
            .setNegativeButton(R.string.cancel_button, null)
            .create()

    }
}