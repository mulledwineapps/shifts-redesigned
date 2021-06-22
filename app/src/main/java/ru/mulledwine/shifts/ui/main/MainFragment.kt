package ru.mulledwine.shifts.ui.main

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main.*
import ru.mulledwine.shifts.Constants
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.DataHolder
import ru.mulledwine.shifts.data.local.entities.Day
import ru.mulledwine.shifts.extensions.*
import ru.mulledwine.shifts.ui.base.BaseFragment
import ru.mulledwine.shifts.ui.base.Binding
import ru.mulledwine.shifts.ui.base.BottombarBuilder
import ru.mulledwine.shifts.ui.base.ToolbarBuilder
import ru.mulledwine.shifts.ui.delegates.RenderProp
import ru.mulledwine.shifts.ui.dialogs.DatePickerDialog
import ru.mulledwine.shifts.ui.dialogs.JobDialog
import ru.mulledwine.shifts.utils.Utils
import ru.mulledwine.shifts.viewmodels.MainState
import ru.mulledwine.shifts.viewmodels.MainViewModel
import ru.mulledwine.shifts.viewmodels.base.IViewModelState
import java.util.*

@AndroidEntryPoint
class MainFragment : BaseFragment<MainViewModel>() {

    companion object {
        private const val TAG = "M_MainFragment"
        private const val KEY_TOOLBAR_TITLE = "KEY_TOOLBAR_TITLE"
    }

    override val viewModel: MainViewModel by viewModels()
    override val layout: Int = R.layout.fragment_main
    override val binding: MainBinding = MainBinding()

    override val prepareToolbar: (ToolbarBuilder.() -> Unit) = {
        setVisibility(false)
    }

    override val prepareBottombar: (BottombarBuilder.() -> Unit) = {
        setVisibility(true)
        setFabClickListener { ::onFabClick.invoke() }
        setOnMenuItemClickListener(::onMenuItemClick)
        setOnNavigationItemClickListener(::onNavigationIconClick)
    }

    private var isSelectionMode: Boolean = false

    private lateinit var dateTabsAdapter: DateTabsAdapter

    private lateinit var pagerAdapter: PagerAdapter

    val shiftsAdapter = MainAdapter(
        clickListener = ::itemClickCallback,
        longClickListener = ::itemLongClickCallback,
        hintCloseListener = ::hintCloseCallback
    )

    val pool = RecyclerView.RecycledViewPool()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getString(KEY_TOOLBAR_TITLE)?.let {
            tv_chosen_date.text = it
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (tv_chosen_date != null)
            outState.putString(KEY_TOOLBAR_TITLE, tv_chosen_date.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun setupViews() {

        tv_chosen_date.setOnClickListener {
            navigateToDatePicker(binding.currentDayStart)
        }

        btn_today.setOnClickListener { onDayChosen(Constants.todayId) }

        // listen for date pick
        setFragmentResultListener(DatePickerDialog.DATE_PICKER_KEY) { _, bundle ->
            val time = bundle.getLong(DatePickerDialog.PICK_ACTION_KEY)
            val dayId = Utils.getCalendarInstance(time).getDayId()
            onDayChosen(dayId)
        }

        // listen for job added
        setFragmentResultListener(JobDialog.JOB_KEY) { _, bundle ->
            val name = bundle.getString(JobDialog.JOB_NAME, "")

            val dest = bundle.getInt(JobDialog.JOB_NEXT_DEST, -1)
            when (dest) {
                R.id.nav_schedule -> viewModel.handleAddJobThenSchedule(name)
                R.id.nav_schedules -> viewModel.handleAddJobThenGoToSchedules(name)
            }
        }

        setupViewPager()
    }

    private fun setupViewPager() {

        pagerAdapter = PagerAdapter(this)
        view_pager.adapter = pagerAdapter

        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                val day = binding.days[position]

                updateTitle(day)
                viewModel.updateCurrentDay(day)
                binding.selectedDayId = day.id

                super.onPageSelected(position)
            }
        })

        dateTabsAdapter = DateTabsAdapter(view_pager)
        tab_layout.setUpWithAdapter(dateTabsAdapter)
    }

    private fun onDayChosen(dayId: String) {
        val position = binding.days.indexOfFirst { it.id == dayId }
        tab_layout.setCurrentItem(position, true)
    }

    private fun onDeleteRequested() {
        // TODO показывать одно окно с запросом на оба типа
        val selected = shiftsAdapter.selectedItems
        val schedules = selected.filter { it.scheduleId != null }
        onSchedulesDeleteRequested(schedules)
        val vacations = selected.filter { it.vacationId != null }
        onVacationsDeleteRequested(vacations)
    }

    private fun onVacationsDeleteRequested(items: List<MainItem.Element>) {
        if (items.isEmpty()) return

        val message = if (items.size == 1) {
            val item = items.first()
            "Удалить отпуск?\n\n${item.vacationTitle}\n${item.jobName}"
        } else "Удалить ${items.size.getVacationGenitive()}?"

        root.askWhetherToDelete(message) {
            if (items.size == 1) viewModel.handleDeleteVacation(items.first().vacationId!!)
            else viewModel.handleDeleteVacations(items.map { it.vacationId!! })
            disableSelectionMode()
        }
    }

    private fun onSchedulesDeleteRequested(items: List<MainItem.Element>) {
        if (items.isEmpty()) return

        val message = if (items.size == 1) {
            val item = items.first()
            "Удалить график?\n\n${item.scheduleTitle}\n${item.jobName}"
        } else "Удалить ${items.size.getScheduleGenitive()}?"

        root.askWhetherToDelete(message) {
            if (items.size == 1) viewModel.handleDeleteSchedule(items.first().scheduleId!!)
            else viewModel.handleDeleteSchedules(items.map { it.scheduleId!! })
            disableSelectionMode()
        }
    }

    private fun onNavigationIconClick(view: View) {
        if (isSelectionMode) {
            disableSelectionMode()
        } else showNavigationMenu(view)
    }

    private fun itemClickCallback(item: MainItem.Element) {
        if (isSelectionMode) onSelectionChanged()
        else viewModel.handleClickEdit(item)
    }

    private fun hintCloseCallback(id: String) {
        viewModel.addHintToShown(id)
    }

    private fun onSelectionChanged() {
        shiftsAdapter.selectedItems.size.let { size ->
            if (size == 0) disableSelectionMode()
            else updateMenuIconsVisibility(size == 1)
        }
    }

    private fun updateMenuIconsVisibility(isVisible: Boolean) {
        bottombar.menu.children
            .filterNot { it.itemId in setOf(R.id.menu_item_delete, R.id.menu_item_archive) }
            .forEach { it.isVisible = isVisible }
    }

    private fun itemLongClickCallback() {
        if (shiftsAdapter.selectedItems.isEmpty()) disableSelectionMode()
        else enableSelectionMode()
    }

    private fun enableSelectionMode() {
        isSelectionMode = true
        bottombar.replaceMenu(R.menu.menu_main)
        bottombar.setNavigationIcon(R.drawable.ic_round_close_24)
        fab.hide()
    }

    private fun disableSelectionMode(showFab: Boolean = true) {
        isSelectionMode = false
        shiftsAdapter.clearSelection()
        bottombar.replaceMenu(R.menu.menu_settings)
        bottombar.setNavigationIcon(R.drawable.ic_round_menu_24)
        if (showFab) fab.show()
    }

    private fun updateTitle(day: Day) {
        val calendar = day.startTime.toCalendar()
        updateTitle(calendar)
    }

    private fun updateTitle(calendar: Calendar) {
        binding.title = calendar.formatAndCapitalize("dd MMMM, yyyy")
    }

    private fun showNavigationMenu(view: View) {
        PopupMenu(requireContext(), view).apply {
            setOnMenuItemClickListener(::onMenuItemClick)
            inflate(R.menu.bottom_nav_menu)
            show()
        }
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_item_delete -> onDeleteRequested()
            R.id.menu_item_tuning -> viewModel.navigateToTuning(shiftsAdapter.selectedItems.first().jobId)
            R.id.menu_item_alarm -> viewModel.navigateToAlarm(
                getString(R.string.alarm_add_label),
                getString(R.string.alarm_edit_label),
                shiftsAdapter.selectedItems.first()
            )
            R.id.menu_item_statistics -> navigateToStatistics()
            R.id.menu_item_jobs -> navigateToJobs()
            R.id.menu_item_schedules -> navigateToSchedules()
            R.id.menu_item_shift_types -> navigateToShiftTypes()
            R.id.menu_item_vacations -> navigateToVacations()
            R.id.menu_item_settings -> navigateToSettings()
            R.id.menu_item_alarms -> navigateToAlarms()
        }

        return true

        // if (item.itemId != R.id.menu_item_delete) disableSelectionMode(false)
    }

    private fun onFabClick() {
        viewModel.handleClickAdd()
    }

    private fun navigateToStatistics() {
        viewModel.navigateToStatistics(shiftsAdapter.selectedItems.first().jobId)
    }

    private fun navigateToSettings() {
        val action = MainFragmentDirections.actionNavMainToNavSettings()
        viewModel.navigateWithAction(action)
    }

    private fun navigateToAlarms() {
        val action = MainFragmentDirections.actionNavMainToNavAlarms()
        viewModel.navigateWithAction(action)
    }

    private fun navigateToJobs() {
        viewModel.navigateToJobs()
    }

    private fun navigateToSchedules() {
        viewModel.navigateToSchedules()
    }

    private fun navigateToShiftTypes() {
        viewModel.navigateToShiftTypes()
    }

    private fun navigateToVacations() {
        viewModel.navigateToVacations(shiftsAdapter.selectedItems.firstOrNull()?.jobId)
    }

    inner class MainBinding : Binding() {

        var currentDayStart: Long = Constants.today.timeInMillis

        var title: String by RenderProp("") {
            if (it.isEmpty()) updateTitle(Constants.today) else tv_chosen_date.text = it
        }

        var selectedDayId: String by RenderProp(Constants.todayId, false) {

        }

        var days: List<Day> by RenderProp(emptyList()) { list ->

            val pos = list.indexOfFirst { it.id == selectedDayId }

            pagerAdapter.items = days
            pagerAdapter.notifyDataSetChanged()

            dateTabsAdapter.submitList(list) {
                tab_layout.setCurrentItem(pos, false)
            }

        }

        private var shownHints: List<String> by RenderProp(emptyList())

        private var shiftItems: List<MainItem.Element> by RenderProp(emptyList())

        override val afterInflated: (() -> Unit) = {
            dependsOn<List<MainItem.Element>, List<String>>(
                ::shiftItems,
                ::shownHints
            ) { list, sh ->
                if (isSelectionMode) disableSelectionMode()

                val hints = if (list.isNotEmpty()) {
                    DataHolder.getHints(requireContext()).filterNot { it.getId() in sh }
                } else emptyList()

                shiftsAdapter.submitList(hints + list)
                container_list_is_empty.isVisible = list.isEmpty()
            }
        }

        override fun bind(data: IViewModelState) {
            data as MainState
            days = data.days
            shiftItems = data.shiftItems
            currentDayStart = data.currentDayStart
            shownHints = data.shownHints
        }

        override fun restoreUi(savedState: Bundle?) {
            if (savedState != null) {
                selectedDayId = savedState.getString(::selectedDayId.name, Constants.todayId)
            }
        }

    }

}