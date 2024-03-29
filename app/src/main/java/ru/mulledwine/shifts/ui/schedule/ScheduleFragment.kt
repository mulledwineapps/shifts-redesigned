package ru.mulledwine.shifts.ui.schedule

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_schedule.*
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.entities.Schedule
import ru.mulledwine.shifts.data.local.models.JobItem
import ru.mulledwine.shifts.data.local.models.ScheduleShiftItem
import ru.mulledwine.shifts.data.local.models.ShiftTypeListItem
import ru.mulledwine.shifts.extensions.formatAndCapitalize
import ru.mulledwine.shifts.extensions.toCalendar
import ru.mulledwine.shifts.ui.base.BaseFragment
import ru.mulledwine.shifts.ui.base.Binding
import ru.mulledwine.shifts.ui.base.ToolbarBuilder
import ru.mulledwine.shifts.ui.delegates.RenderProp
import ru.mulledwine.shifts.ui.dialogs.ChooseJobDialog
import ru.mulledwine.shifts.ui.dialogs.ChooseShiftTypeDialog
import ru.mulledwine.shifts.ui.dialogs.DatePickerDialog
import ru.mulledwine.shifts.ui.schedules.SchedulesFragmentDirections
import ru.mulledwine.shifts.utils.Utils
import ru.mulledwine.shifts.viewmodels.InputErrors
import ru.mulledwine.shifts.viewmodels.ScheduleViewModel

@AndroidEntryPoint
class ScheduleFragment : BaseFragment<ScheduleViewModel>() {

    override val layout: Int = R.layout.fragment_schedule
    override val binding: ScheduleBinding by lazy { ScheduleBinding() }
    override val viewModel: ScheduleViewModel by viewModels()

    override val prepareToolbar: (ToolbarBuilder.() -> Unit) = {
        val title =
            if (args.schedule == null) requireContext().getString(R.string.label_add_schedule)
            else requireContext().getString(R.string.label_edit_schedule)
        this.setTitle(title)
    }

    private val args: ScheduleFragmentArgs by navArgs()

    private val scheduleShiftsAdapter = ShiftsAdapter(
        ::itemClickCallback,
        ::itemLongClickCallback
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

        // listen for job pick
        setFragmentResultListener(ChooseJobDialog.CHOOSE_JOB_KEY) { _, bundle ->
            binding.jobItem = bundle[ChooseJobDialog.SELECTED_JOB] as JobItem
        }

        // listen for date pick
        setFragmentResultListener(DatePickerDialog.DATE_PICKER_KEY) { _, bundle ->
            val time = bundle.getLong(DatePickerDialog.PICK_ACTION_KEY)
            when (bundle.getInt(DatePickerDialog.VIEW_KEY)) {
                tv_schedule_start.id -> binding.start = time
                tv_schedule_finish.id -> binding.finish = time
            }
        }

        // listen for shift type pick
        setFragmentResultListener(ChooseShiftTypeDialog.CHOOSE_SHIFT_TYPE_KEY) { _, bundle ->

            val shiftTypeItem =
                bundle[ChooseShiftTypeDialog.SELECTED_SHIFT_TYPE] as ShiftTypeListItem
            val currentItemId = bundle.getInt(ChooseShiftTypeDialog.CURRENT_ITEM_ID)
            val copy = scheduleShiftsAdapter.currentList

            if (currentItemId == -1) { // add new item
                val maxOrdinal = copy.map { it.ordinal }.maxOrNull() ?: 0
                val maxId = copy.map { it.shiftId }.maxOrNull() ?: 0

                val item = ScheduleShiftItem(
                    shiftId = maxId + 1,
                    shiftTypeId = shiftTypeItem.id,
                    ordinal = maxOrdinal + 1,
                    title = shiftTypeItem.title,
                    typeName = shiftTypeItem.name,
                    color = shiftTypeItem.color,
                    isNewItem = true,
                    originalShiftTypeId = shiftTypeItem.id
                )
                submitItems(copy + item)

            } else { // change selected item
                val list = copy.map {
                    if (it.shiftId == currentItemId)
                        it.copy(
                            shiftTypeId = shiftTypeItem.id,
                            title = shiftTypeItem.title,
                            typeName = shiftTypeItem.name,
                            color = shiftTypeItem.color,
                        )
                    else it
                }
                submitItems(list)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menu_item_save).setOnMenuItemClickListener {

            // TODO make validation dependent on schedule type (cyclic or not)

            if (scheduleShiftsAdapter.itemCount == 0) {
                viewModel.handleInputError(InputErrors.NoShifts)
                return@setOnMenuItemClickListener true
            }

            getBoundsInputError(scheduleShiftsAdapter.itemCount)?.let {
                viewModel.handleInputError(it)
                return@setOnMenuItemClickListener true
            }

            val schedule = Schedule(
                id = args.schedule?.id,
                jobId = binding.jobItem.id,
                isCyclic = btn_schedule_type_cyclic.isChecked,
                start = binding.start,
                finish = binding.finish
            )

            viewModel.handleClickSave(
                schedule = schedule,
                shiftsToUpsert = scheduleShiftsAdapter.currentList,
                shiftIdsToDelete = binding.shiftIdsToDelete,
                ::cancelAlarms
            )
            true
        }
    }

    private fun cancelAlarms(ids: List<Int>) {
        ids.forEach {
            Utils.cancelAlarm(requireContext(), it)
        }
    }

    override fun setupViews() {

        tv_list_is_empty.isVisible = args.schedule == null // TODO and if schedule.shifts is empty?

        tv_schedule_job.setOnClickListener { navigateToDialogChooseJob() }

        args.schedule?.let {
            binding.start = it.start
            binding.finish = it.finish
            submitItems(it.shiftItems)
            if (it.isCyclic) btn_schedule_type_cyclic.isChecked = true
            else btn_schedule_type_regular.isChecked = true
            binding.isCyclic = it.isCyclic
        } ?: run {
            btn_schedule_type_regular.isChecked = true
        }

        viewModel.observeShiftTypes(viewLifecycleOwner) {
            binding.shiftTypeItems = it
        }

        viewModel.observeJobs(viewLifecycleOwner) {
            binding.jobItems = it
        }

        tv_schedule_start.apply {
            setOnClickListener {
                navigateToDatePicker(binding.start, it.id)
            }
            setOnLongClickListener {
                binding.start = 0L
                true
            }
        }

        tv_schedule_finish.apply {
            setOnClickListener {
                navigateToDatePicker(binding.finish, it.id)
            }
            setOnLongClickListener {
                binding.finish = 0L
                true
            }
        }

        radio_group_schedule_type.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.btn_schedule_type_regular -> binding.isCyclic = false
                R.id.btn_schedule_type_cyclic -> binding.isCyclic = true
            }
        }

        with(rv_schedule_shifts) {
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            adapter = scheduleShiftsAdapter
            onSizeChangeListener = {
                scroll_view_schedule.scrollTo(0, scroll_view_schedule.getChildAt(0).height)
            }
        }

        btn_add.setOnClickListener {

            getBoundsInputError(scheduleShiftsAdapter.itemCount + 1)?.let {
                viewModel.handleInputError(it)
                return@setOnClickListener
            }

            val action = ScheduleFragmentDirections.actionToDialogChooseShiftType(
                binding.shiftTypeItems.toTypedArray()
            )
            viewModel.navigateWithAction(action)
        }

    }

    private fun navigateToDialogChooseJob() {
        if (binding.jobItems.count() <= 1) return // не из чего выбирать

        val action = SchedulesFragmentDirections.actionToDialogChooseJob(
            binding.jobItems.toTypedArray()
        )
        viewModel.navigateWithAction(action)
    }

    private fun getBoundsInputError(itemCount: Int): InputErrors? {
        val areBothBoundsSet = binding.start > 0L && binding.finish > 0L
        if (areBothBoundsSet) {
            val scheduleDuration = Utils.getDuration(binding.start, binding.finish)
            if (scheduleDuration < 0) {
                return InputErrors.IllegalScheduleDuration
            }
            if (scheduleDuration < itemCount) {
                return InputErrors.TooManyShifts(scheduleDuration)
            }
        }
        return null
    }

    private fun itemClickCallback(item: ScheduleShiftItem) {
        val action = ScheduleFragmentDirections.actionToDialogChooseShiftType(
            binding.shiftTypeItems.toTypedArray(),
            item.shiftId
        )
        viewModel.navigateWithAction(action)
    }

    private fun itemLongClickCallback(item: ScheduleShiftItem) {
        root.askWhetherToDelete("Удалить из графика смену ${item.typeName}?") {

            binding.shiftIdsToDelete.add(item.shiftId)

            var ordinal = 1
            val list = scheduleShiftsAdapter.currentList
                .filterNot { it.shiftId == item.shiftId }
                .sortedBy { it.ordinal }
                .map { it.copy(ordinal = ordinal++) }

            submitItems(list)
        }
    }

    private fun submitItems(list: List<ScheduleShiftItem>) {
        scheduleShiftsAdapter.submitList(list) {
            if (tv_list_is_empty == null) return@submitList // exit in process
            tv_list_is_empty.isVisible = list.isEmpty()
        }
    }

    inner class ScheduleBinding : Binding() {

        var shiftTypeItems: List<ShiftTypeListItem> = emptyList()
        val shiftIdsToDelete: MutableList<Int> = mutableListOf()

        var jobItems: List<JobItem> = emptyList()

        var jobItem by RenderProp(args.job) {
            tv_schedule_job.text = it.name
        }

        var start: Long by RenderProp(0L) {
            tv_schedule_start.text = if (it == 0L) ""
            else it.toCalendar().formatAndCapitalize()
        }

        var finish: Long by RenderProp(0L) {
            tv_schedule_finish.text = if (it == 0L) ""
            else it.toCalendar().formatAndCapitalize()
        }

        var isCyclic: Boolean by RenderProp(false) {
            tv_setup_shifts_title.text = if (it) getString(R.string.schedule_setup_cycle_btn_title)
            else getString(R.string.schedule_setup_shifts_btn_title)
        }

    }

}