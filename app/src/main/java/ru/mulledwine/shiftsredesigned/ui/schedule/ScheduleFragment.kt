package ru.mulledwine.shiftsredesigned.ui.schedule

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_schedule.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleShiftItem
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftTypeItem
import ru.mulledwine.shiftsredesigned.extensions.format
import ru.mulledwine.shiftsredesigned.extensions.getTrimmedString
import ru.mulledwine.shiftsredesigned.extensions.hideKeyboard
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.ui.base.Binding
import ru.mulledwine.shiftsredesigned.ui.delegates.RenderProp
import ru.mulledwine.shiftsredesigned.ui.dialogs.ChooseShiftTypeDialog
import ru.mulledwine.shiftsredesigned.ui.dialogs.DatePickerDialog
import ru.mulledwine.shiftsredesigned.utils.SimpleOnEditorActionListener
import ru.mulledwine.shiftsredesigned.utils.Utils
import ru.mulledwine.shiftsredesigned.viewmodels.InputErrors
import ru.mulledwine.shiftsredesigned.viewmodels.ScheduleViewModel


class ScheduleFragment : BaseFragment<ScheduleViewModel>() {

    companion object {
        private const val TAG = "M_ScheduleFragment"
    }

    override val layout: Int = R.layout.fragment_schedule
    override val binding: ScheduleBinding = ScheduleBinding()
    override val viewModel: ScheduleViewModel by viewModels()

    private val args: ScheduleFragmentArgs by navArgs()

    private val scheduleShiftsAdapter = ShiftsAdapter(
        ::itemClickCallback,
        ::itemLongClickCallback
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

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

            val shiftTypeItem = bundle[ChooseShiftTypeDialog.SELECTED_SHIFT_TYPE] as ShiftTypeItem
            val currentItemId = bundle.getInt(ChooseShiftTypeDialog.CURRENT_ITEM_ID)
            val copy = scheduleShiftsAdapter.currentList

            if (currentItemId == -1) { // add new item
                val maxOrdinal = copy.map { it.ordinal }.maxOrNull() ?: 0
                val maxId = copy.map { it.shiftId }.maxOrNull() ?: 0

                val item = ScheduleShiftItem(
                    shiftId = maxId + 1,
                    shiftTypeId = shiftTypeItem.id,
                    ordinal = maxOrdinal + 1,
                    duration = shiftTypeItem.duration,
                    typeName = shiftTypeItem.name,
                    color = shiftTypeItem.color,
                    isNewItem = true
                )
                submitItems(copy + item)

            } else { // change selected item
                val list = copy.map {
                    if (it.shiftId == currentItemId)
                        it.copy(
                            shiftTypeId = shiftTypeItem.id,
                            duration = shiftTypeItem.duration,
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
        inflater.inflate(R.menu.menu_schedule, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menu_item_save).setOnMenuItemClickListener {

            if (et_schedule_name.text.isBlank()) {
                viewModel.handleInputError(InputErrors.NameIsBlank)
                return@setOnMenuItemClickListener true
            }

            if (scheduleShiftsAdapter.itemCount == 0) {
                viewModel.handleInputError(InputErrors.NoShifts)
                return@setOnMenuItemClickListener true
            }

            getBoundsInputError(scheduleShiftsAdapter.itemCount)?.let {
                viewModel.handleInputError(it)
                return@setOnMenuItemClickListener true
            }

            val item = Schedule(
                id = args.item?.id,
                name = et_schedule_name.getTrimmedString(),
                start = binding.start,
                finish = binding.finish
            )

            viewModel.handleClickSave(
                schedule = item,
                shiftsToUpsert = scheduleShiftsAdapter.currentList,
                shiftIdsToDelete = binding.shiftIdsToDelete
            )
            true
        }
    }

    override fun setupViews() {

        tv_list_is_empty.isVisible = args.item == null

        args.item?.let {
            et_schedule_name.setText(it.name)
            binding.start = it.start
            binding.finish = it.finish
            submitItems(it.shiftItems)
        }

        viewModel.observeShiftTypes(viewLifecycleOwner) { shiftTypeItems ->
            binding.shiftTypeItems = shiftTypeItems
        }

        et_schedule_name.setOnEditorActionListener(SimpleOnEditorActionListener {
            clearNameFieldFocus()
        })

        tv_schedule_start.apply {
            setOnClickListener {
                clearNameFieldFocus()
                navigateToDatePicker(binding.start, it.id)
            }
            setOnLongClickListener {
                binding.start = 0L
                true
            }
        }

        tv_schedule_finish.apply {
            setOnClickListener {
                clearNameFieldFocus()
                navigateToDatePicker(binding.finish, it.id)
            }
            setOnLongClickListener {
                binding.finish = 0L
                true
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

            clearNameFieldFocus()
            val action = ScheduleFragmentDirections.actionNavScheduleToDialogChooseShiftType(
                binding.shiftTypeItems.toTypedArray()
            )
            viewModel.navigateWithAction(action)
        }

    }

    private fun clearNameFieldFocus() {
        with(et_schedule_name) {
            if (hasFocus()) clearFocus()
            requireContext().hideKeyboard(this)
        }
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
        val action = ScheduleFragmentDirections.actionNavScheduleToDialogChooseShiftType(
            binding.shiftTypeItems.toTypedArray(),
            item.shiftId
        )
        viewModel.navigateWithAction(action)
    }

    private fun itemLongClickCallback(item: ScheduleShiftItem) {
        root.showAreYouSureDialog("${item.typeName}\n\nВы уверены, что хотите удалить эту смену?") {

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

        var shiftTypeItems: List<ShiftTypeItem> = emptyList()
        val shiftIdsToDelete: MutableList<Int> = mutableListOf()

        var start: Long by RenderProp(0L) {
            tv_schedule_start.text = if (it == 0L) getString(R.string.choose_placeholder)
            else Utils.getCalendarInstance(it).format()
        }

        var finish: Long by RenderProp(0L) {
            tv_schedule_finish.text = if (it == 0L) getString(R.string.choose_placeholder)
            else Utils.getCalendarInstance(it).format()
        }

    }

}