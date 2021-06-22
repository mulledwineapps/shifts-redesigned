package ru.mulledwine.shifts.ui.alarm

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_alarm.*
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.models.ClockTime
import ru.mulledwine.shifts.data.local.models.JobItem
import ru.mulledwine.shifts.data.local.models.ScheduleItem
import ru.mulledwine.shifts.data.local.models.ShiftItem
import ru.mulledwine.shifts.extensions.getTrimmedString
import ru.mulledwine.shifts.ui.alarms.AlarmsFragmentDirections
import ru.mulledwine.shifts.ui.base.BaseFragment
import ru.mulledwine.shifts.ui.base.Binding
import ru.mulledwine.shifts.ui.delegates.RenderProp
import ru.mulledwine.shifts.ui.dialogs.ChooseJobDialog
import ru.mulledwine.shifts.ui.dialogs.ChooseScheduleDialog
import ru.mulledwine.shifts.ui.dialogs.ChooseShiftDialog
import ru.mulledwine.shifts.ui.dialogs.TimePickerDialog
import ru.mulledwine.shifts.utils.Utils
import ru.mulledwine.shifts.viewmodels.AlarmState
import ru.mulledwine.shifts.viewmodels.AlarmViewModel
import ru.mulledwine.shifts.viewmodels.base.IViewModelState

@AndroidEntryPoint
class AlarmFragment : BaseFragment<AlarmViewModel>() {

    override val layout: Int = R.layout.fragment_alarm
    override val binding: AlarmBinding by lazy { AlarmBinding() }
    override val viewModel: AlarmViewModel by viewModels()

    private val args: AlarmFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

        // listen for job pick
        setFragmentResultListener(ChooseJobDialog.CHOOSE_JOB_KEY) { _, bundle ->
            val job = bundle[ChooseJobDialog.SELECTED_JOB] as JobItem
            viewModel.handleUpdateJob(job)
        }

        // listen for schedule pick
        setFragmentResultListener(ChooseScheduleDialog.CHOOSE_SCHEDULE_KEY) { _, bundle ->
            val schedule = bundle[ChooseScheduleDialog.SELECTED_SCHEDULE] as ScheduleItem
            viewModel.handleUpdateSchedule(schedule)
        }

        // listen for shift pick
        setFragmentResultListener(ChooseShiftDialog.CHOOSE_SHIFT_KEY) { _, bundle ->
            val shift = bundle[ChooseShiftDialog.SELECTED_SHIFT] as ShiftItem
            viewModel.handleUpdateShift(shift)
        }

        // listen for time pick
        setFragmentResultListener(TimePickerDialog.TIME_PICKER_KEY) { _, bundle ->
            val time = bundle[TimePickerDialog.PICK_ACTION_KEY] as ClockTime
            checkbox_is_alarm_active.isChecked = true
            viewModel.handleUpdateTime(time)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menu_item_save).setOnMenuItemClickListener {
            val isActive = checkbox_is_alarm_active.isChecked
            val label = et_alarm_label.getTrimmedString()
            viewModel.handleClickSave(isActive, label) { alarm, time ->
                // cancel the alarm
                Utils.cancelAlarm(requireContext(), alarm.id)
                // setup new alarm if active
                if (isActive) Utils.setAlarm(requireContext(), alarm, time)
            }
            true
        }
    }

    override fun setupViews() {

        viewModel.observeJobs(viewLifecycleOwner) {
            binding.jobItems = it
            if (it.count() == 1) viewModel.handleUpdateJob(it.first())
        }

        viewModel.observeSchedules(viewLifecycleOwner) {
            binding.scheduleItems = it
            if (it.count() == 1) viewModel.handleUpdateSchedule(it.first())
        }

        viewModel.observeShifts(viewLifecycleOwner) {
            binding.shiftItems = it
        }

        tv_alarm_job.setOnClickListener { navigateToDialogChooseJob() }
        tv_alarm_schedule.setOnClickListener { navigateToDialogChooseSchedule() }
        tv_alarm_shift.setOnClickListener { navigateToDialogChooseShift() }

        checkbox_is_alarm_active.isChecked = args.item?.alarm?.isActive ?: true

        tv_alarm_time.setOnClickListener {
            navigateToTimePicker(binding.alarmClock)
        }

        args.item?.alarm?.let {
            et_alarm_label.setText(it.label)
        }
    }

    private fun navigateToDialogChooseJob() {
        val action = AlarmsFragmentDirections.actionToDialogChooseJob(
            binding.jobItems.toTypedArray()
        )
        viewModel.navigateWithAction(action)
    }

    private fun navigateToDialogChooseSchedule() {
        val action = AlarmsFragmentDirections.actionToDialogChooseSchedule(
            binding.scheduleItems.toTypedArray()
        )
        viewModel.navigateWithAction(action)
    }

    private fun navigateToDialogChooseShift() {
        val action = AlarmsFragmentDirections.actionToDialogChooseShift(
            binding.shiftItems.toTypedArray()
        )
        viewModel.navigateWithAction(action)
    }

    inner class AlarmBinding : Binding() {

        var jobItems: List<JobItem> = emptyList()
        var scheduleItems: List<ScheduleItem> = emptyList()
        var shiftItems: List<ShiftItem> = emptyList()

        private var jobTitle by RenderProp(args.item?.job?.name ?: "") {
            tv_alarm_job.text = it
        }

        private var scheduleTitle by RenderProp(args.item?.job?.name ?: "") {
            tv_alarm_schedule.text = it
        }

        private var shiftTitle by RenderProp(args.item?.job?.name ?: "") {
            tv_alarm_shift.text = it
        }

        var alarmClock: ClockTime by RenderProp(args.item?.alarm?.time ?: ClockTime()) {
            tv_alarm_time.text = it.toString()
        }

        private var alarmInfo: String by RenderProp("") {
            tv_alarm_info.text = it
        }

        override fun bind(data: IViewModelState) {
            data as AlarmState
            jobTitle = data.jobTitle
            scheduleTitle = data.scheduleTitle
            shiftTitle = data.shiftTitle
            alarmClock = data.alarmClock
            alarmInfo = data.alarmInfo
        }

    }

}