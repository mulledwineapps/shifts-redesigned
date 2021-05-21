package ru.mulledwine.shiftsredesigned.ui.daystuning

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_days_tuning.*
import kotlinx.android.synthetic.main.item_shift_type.*
import kotlinx.android.synthetic.main.layout_month_choosing.*
import ru.mulledwine.shiftsredesigned.Constants.today
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.JobItem
import ru.mulledwine.shiftsredesigned.data.local.models.Month
import ru.mulledwine.shiftsredesigned.data.local.models.Months
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftTypeListItem
import ru.mulledwine.shiftsredesigned.extensions.data.next
import ru.mulledwine.shiftsredesigned.extensions.data.previous
import ru.mulledwine.shiftsredesigned.extensions.daysFromWeekStart
import ru.mulledwine.shiftsredesigned.extensions.month
import ru.mulledwine.shiftsredesigned.extensions.setWithZeroTime
import ru.mulledwine.shiftsredesigned.extensions.year
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.ui.base.Binding
import ru.mulledwine.shiftsredesigned.ui.delegates.RenderProp
import ru.mulledwine.shiftsredesigned.ui.dialogs.ChooseJobDialog
import ru.mulledwine.shiftsredesigned.ui.dialogs.ChooseMonthDialog
import ru.mulledwine.shiftsredesigned.ui.dialogs.ChooseShiftTypeDialog
import ru.mulledwine.shiftsredesigned.utils.Utils
import ru.mulledwine.shiftsredesigned.viewmodels.DaysTuningViewModel
import java.util.*

class DaysTuningFragment : BaseFragment<DaysTuningViewModel>() {

    private val args: DaysTuningFragmentArgs by navArgs()
    private val datesAdapter = DatesAdapter()

    override val layout: Int = R.layout.fragment_days_tuning
    override val binding: DaysTuningBinding by lazy { DaysTuningBinding() }
    override val viewModel: DaysTuningViewModel by viewModels()

    private var selectedShiftTypeId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

        // listen for job pick
        setFragmentResultListener(ChooseJobDialog.CHOOSE_JOB_KEY) { _, bundle ->
            val job = bundle[ChooseJobDialog.SELECTED_JOB] as JobItem
            binding.jobName = job.name
        }

        // listen for month pick
        setFragmentResultListener(ChooseMonthDialog.CHOOSE_MONTH_KEY) { _, bundle ->
            val selectedMonth = bundle[ChooseMonthDialog.SELECTED_MONTH] as Month
            binding.month = selectedMonth
        }

        // listen for shift type pick
        setFragmentResultListener(ChooseShiftTypeDialog.CHOOSE_SHIFT_TYPE_KEY) { _, bundle ->
            val shiftTypeItem = bundle[ChooseShiftTypeDialog.SELECTED_SHIFT_TYPE] as ShiftTypeListItem
            selectedShiftTypeId = shiftTypeItem.id
            tv_shift_type_item_name.text = shiftTypeItem.name
            tv_shift_type_item_duration.text = shiftTypeItem.title
            v_shift_type_item_color.backgroundTintList = ColorStateList.valueOf(shiftTypeItem.color)
            tv_shift_placeholder.isVisible = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menu_item_save).setOnMenuItemClickListener { true }
    }

    override fun setupViews() {

        rv_dates.adapter = datesAdapter
        rv_dates.itemAnimator = null

        tv_tuning_job.setOnClickListener { navigateToDialogChooseJob() }
        tv_month_name.setOnClickListener { navigateToDialogChooseMonth() }

        btn_previous_month.setOnClickListener {
            binding.month = binding.month.previous()
        }

        btn_next_month.setOnClickListener {
            binding.month = binding.month.next()
        }

        btn_choose_shift_type.setOnClickListener { navigateToDialogChooseShiftType() }

        viewModel.observeJobs(viewLifecycleOwner) {
            binding.jobItems = it
        }

        viewModel.observeShiftTypes(viewLifecycleOwner) {
            binding.shiftTypeItems = it
        }

    }

    private fun navigateToDialogChooseJob() {
        val action = DaysTuningFragmentDirections.actionToDialogChooseJob(
            binding.jobItems.toTypedArray()
        )
        viewModel.navigateWithAction(action)
    }

    private fun navigateToDialogChooseMonth() {
        val action = DaysTuningFragmentDirections
            .actionNavDaysTuningToDialogChooseMonth(binding.month)
        viewModel.navigateWithAction(action)
    }

    private fun navigateToDialogChooseShiftType() {
        val action = DaysTuningFragmentDirections.actionToDialogChooseShiftType(
            binding.shiftTypeItems.toTypedArray()
        )
        viewModel.navigateWithAction(action)
    }

    inner class DaysTuningBinding : Binding() {

        var jobItems: List<JobItem> = emptyList()
        var shiftTypeItems: List<ShiftTypeListItem> = emptyList()

        var jobName: String by RenderProp(args.job.name) {
            tv_tuning_job.text = it
        }

        var month by RenderProp(Month(today.month, today.year)) {
            val monthName = Months.values()[it.number].getName(requireContext())
            tv_month_name.text = getString(R.string.month_year, monthName, it.year.toString())

            val calendar = Utils.getCalendarInstance()
            calendar.setWithZeroTime(it.year, it.number, 1)

            val empty = List(calendar.daysFromWeekStart) { CellItem.EmptyCell(it) }

            val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            val list = (1..days).map { CellItem.DateItem(it.toString()) }

            rv_dates.adapter = datesAdapter
            datesAdapter.submitList(empty + list)
        }

    }

}