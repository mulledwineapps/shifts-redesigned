package ru.mulledwine.shiftsredesigned.ui.vacation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_vacation.*
import kotlinx.android.synthetic.main.fragment_vacation.btn_choose_shift_type
import kotlinx.android.synthetic.main.item_shift_type.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.entities.Vacation
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleShiftItem
import ru.mulledwine.shiftsredesigned.extensions.data.toScheduleShiftItem
import ru.mulledwine.shiftsredesigned.extensions.formatAndCapitalize
import ru.mulledwine.shiftsredesigned.extensions.hideKeyboard
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.ui.base.Binding
import ru.mulledwine.shiftsredesigned.ui.delegates.RenderProp
import ru.mulledwine.shiftsredesigned.ui.dialogs.ChooseShiftDialog
import ru.mulledwine.shiftsredesigned.ui.dialogs.DatePickerDialog
import ru.mulledwine.shiftsredesigned.utils.SimpleOnEditorActionListener
import ru.mulledwine.shiftsredesigned.utils.SimpleTextWatcher
import ru.mulledwine.shiftsredesigned.utils.Utils
import ru.mulledwine.shiftsredesigned.viewmodels.VacationViewModel
import java.util.*

class VacationFragment : BaseFragment<VacationViewModel>() {

    companion object {
        private const val TAG = "M_VacationFragment"
    }

    override val layout: Int = R.layout.fragment_vacation
    override val binding: VacationBinding = VacationBinding()
    override val viewModel: VacationViewModel by viewModels()

    private val args: VacationFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

        // listen for date pick
        setFragmentResultListener(DatePickerDialog.DATE_PICKER_KEY) { _, bundle ->
            val time = bundle.getLong(DatePickerDialog.PICK_ACTION_KEY)
            when (bundle.getInt(DatePickerDialog.VIEW_KEY)) {
                tv_vacation_start.id -> binding.start = time
                tv_vacation_finish.id -> binding.finish = time
            }
        }

        // listen for shift pick
        setFragmentResultListener(ChooseShiftDialog.CHOOSE_SHIFT_KEY) { _, bundle ->
            val item = bundle[ChooseShiftDialog.SELECTED_SHIFT] as ScheduleShiftItem
            updateFirstShift(item.typeName, item.duration, item.color)
            binding.firstShiftId = item.shiftId
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menu_item_save).setOnMenuItemClickListener {

            val item = Vacation(
                id = args.vacation?.id,
                scheduleId = args.schedule.id,
                start = binding.start,
                finish = binding.finish,
                firstShiftId = binding.firstShiftId
            )

            viewModel.handleClickSave(item)
            true
        }
    }

    override fun setupViews() {

        tv_shift_type_hint.text =
            if (args.schedule.isCyclic) getString(R.string.click_to_choose_shift_hint)
            else getString(R.string.available_for_cyclic_schedules_only_hint)

        btn_choose_shift_type.isEnabled = args.schedule.isCyclic == true

        args.vacation?.let {
            binding.start = it.start
            binding.finish = it.finish
            binding.firstShiftId = it.shiftTypeItem?.id

            it.shiftTypeItem?.let { shiftType ->
                updateFirstShift(shiftType.name, shiftType.duration, shiftType.color)
            }
        }

        tv_vacation_start.apply {
            setOnClickListener {
                clearCountFieldFocus()
                navigateToDatePicker(binding.start, it.id)
            }
        }

        tv_vacation_finish.apply {
            setOnClickListener {
                clearCountFieldFocus()
                navigateToDatePicker(binding.finish, it.id)
            }
        }

        et_days_total.addTextChangedListener(SimpleTextWatcher {
            if (it.isBlank() || binding.start == 0L) return@SimpleTextWatcher
            binding.calculateFinish(it.toInt().dec())
        })

        et_days_total.setOnEditorActionListener(SimpleOnEditorActionListener {
            clearCountFieldFocus()
        })

        btn_choose_shift_type.setOnClickListener { navigateToDialogChooseShiftType() }

        viewModel.observePattern(args.schedule.id, viewLifecycleOwner) {
            binding.pattern = it.map { it.toScheduleShiftItem() }
        }

    }

    private fun updateFirstShift(name: String, duration: String, color: Int) {
        tv_shift_type_item_name.text = name
        tv_shift_type_item_duration.text = duration
        v_shift_type_item_color.backgroundTintList = ColorStateList.valueOf(color)
        tv_shift_type_hint.isVisible = false
    }

    private fun clearCountFieldFocus() {
        with(et_days_total) {
            if (hasFocus()) clearFocus()
            requireContext().hideKeyboard(this)
        }
    }

    private fun navigateToDialogChooseShiftType() {
        val action = VacationFragmentDirections.actionNavVacationToDialogChooseShift(
            binding.pattern.toTypedArray()
        )
        viewModel.navigateWithAction(action)
    }

    inner class VacationBinding : Binding() {

        var firstShiftId: Int? = null
        var pattern: List<ScheduleShiftItem> = emptyList()

        var duration: Int by RenderProp(0) {
            if (it == 0) et_days_total.text.clear()
            else et_days_total.setText(it.toString())
        }

        var start: Long by RenderProp(0L) {
            tv_vacation_start.text =
                if (it == 0L) "" else Utils.getCalendarInstance(it).formatAndCapitalize()
            updateDuration()
        }

        var finish: Long by RenderProp(0L) {
            tv_vacation_finish.text =
                if (it == 0L) "" else Utils.getCalendarInstance(it).formatAndCapitalize()
            updateDuration()
        }

        private var doNotUpdateDuration: Boolean = false

        private fun updateDuration() {
            if (doNotUpdateDuration) return
            val count = if (start == 0L || finish == 0L) 0
            else Utils.getDuration(start, finish)
            duration = maxOf(0, count)
        }

        fun calculateFinish(daysCount: Int) {
            val calendar = Utils.getCalendarInstance(binding.start)
            calendar.add(Calendar.DATE, daysCount)
            // to avoid loop turn off duration updating
            doNotUpdateDuration = true
            finish = calendar.timeInMillis
            doNotUpdateDuration = false
        }

    }

}