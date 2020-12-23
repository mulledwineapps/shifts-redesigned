package ru.mulledwine.shiftsredesigned.ui.main

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_main.*
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.entities.Day
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftOnMainItem
import ru.mulledwine.shiftsredesigned.extensions.formatAndCapitalize
import ru.mulledwine.shiftsredesigned.extensions.getDayId
import ru.mulledwine.shiftsredesigned.extensions.getScheduleGenitive
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.ui.base.Binding
import ru.mulledwine.shiftsredesigned.ui.base.BottombarBuilder
import ru.mulledwine.shiftsredesigned.ui.base.ToolbarBuilder
import ru.mulledwine.shiftsredesigned.ui.delegates.RenderProp
import ru.mulledwine.shiftsredesigned.ui.dialogs.DatePickerDialog
import ru.mulledwine.shiftsredesigned.ui.dialogs.JobDialog
import ru.mulledwine.shiftsredesigned.utils.Utils
import ru.mulledwine.shiftsredesigned.viewmodels.MainState
import ru.mulledwine.shiftsredesigned.viewmodels.MainViewModel
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState
import java.util.*


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
        longClickListener = ::itemLongClickCallback
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
            viewModel.handleAddJobThenSchedule(name, requireContext())
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
        val selected = shiftsAdapter.selectedItems
        val message = if (selected.size == 1) {
            val item = selected.first()
            "Удалить график?\n\n${item.scheduleDuration}\n${item.jobName}"
        } else "Удалить ${selected.size.getScheduleGenitive()}?"

        root.askWhetherToDelete(message) {
            if (selected.size == 1) viewModel.handleDeleteSchedule(selected.first().scheduleId)
            else viewModel.handleDeleteSchedules(selected.map { it.scheduleId })
            disableSelectionMode()
        }
    }

    private fun onNavigationIconClick(view: View) {
        if (isSelectionMode) {
            disableSelectionMode()
        } else showNavigationMenu(view)
    }

    private fun itemClickCallback(item: ShiftOnMainItem) {
        if (isSelectionMode) onSelectionChanged()
        else viewModel.handleClickEdit(
            title = getString(R.string.label_edit_schedule),
            item = item
        )
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
        val calendar = Utils.getCalendarInstance(day.startTime)
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
            R.id.menu_item_statistics -> Unit
            R.id.menu_item_jobs -> navigateToJobs()
            R.id.menu_item_schedules -> navigateToSchedules()
            R.id.menu_item_shift_types -> navigateToShiftTypes()
            R.id.menu_item_vacations -> navigateToVacations()
            R.id.menu_item_settings -> navigateToSettings()
        }

        return true

        // if (item.itemId != R.id.menu_item_delete) disableSelectionMode(false)
    }

    private fun onFabClick() {
        viewModel.handleClickAdd(requireContext())
    }

    private fun navigateToSettings() {
        val action = MainFragmentDirections.actionNavMainToNavSettings()
        viewModel.navigateWithAction(action)
    }

    private fun navigateToJobs() {
        viewModel.navigateToJobs()
    }

    private fun navigateToSchedules() {
        viewModel.navigateToSchedules(shiftsAdapter.selectedItems.firstOrNull()?.jobId)
    }

    private fun navigateToShiftTypes() {
        viewModel.navigateToShiftTypes()
    }

    private fun navigateToVacations() {
        viewModel.navigateToVacations(shiftsAdapter.selectedItems.firstOrNull()?.scheduleId)
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

        private var shiftItems: List<ShiftOnMainItem> by RenderProp(emptyList()) {
            if (isSelectionMode) disableSelectionMode()
            shiftsAdapter.submitList(it)
            container_list_is_empty.isVisible = it.isEmpty()
        }

        override fun bind(data: IViewModelState) {
            data as MainState
            days = data.days
            shiftItems = data.shiftItems
            currentDayStart = data.currentDayStart
        }

        override fun restoreUi(savedState: Bundle?) {
            if (savedState != null) {
                selectedDayId = savedState.getString(::selectedDayId.name, Constants.todayId)
            }
        }

    }

}