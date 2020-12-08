package ru.mulledwine.shiftsredesigned.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_main.*
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.entities.Day
import ru.mulledwine.shiftsredesigned.data.local.models.Months
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftOnMainItem
import ru.mulledwine.shiftsredesigned.extensions.date
import ru.mulledwine.shiftsredesigned.extensions.getDayId
import ru.mulledwine.shiftsredesigned.extensions.month
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.ui.base.Binding
import ru.mulledwine.shiftsredesigned.ui.base.BottombarBuilder
import ru.mulledwine.shiftsredesigned.ui.base.ToolbarBuilder
import ru.mulledwine.shiftsredesigned.ui.delegates.RenderProp
import ru.mulledwine.shiftsredesigned.ui.dialogs.DatePickerDialog
import ru.mulledwine.shiftsredesigned.utils.Utils
import ru.mulledwine.shiftsredesigned.viewmodels.MainState
import ru.mulledwine.shiftsredesigned.viewmodels.MainViewModel
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState
import java.util.*


class MainFragment : BaseFragment<MainViewModel>() {

    companion object {
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
        setFabClickListener { navigateToSchedule() }
        setOnMenuItemClickListener { if (it.itemId == R.id.menu_item_settings) navigateToSettings() }
        setOnNavigationItemClickListener { showNavigationMenu(it) }
    }

    private val datesLayoutManager
        get() = rv_dates.layoutManager as LinearLayoutManager

    private var moveIndicatorOnNextSelection = true

    private val datesAdapter = DatesAdapter(::moveSelectionIndicator, ::daySelectedCallback)

    private val shiftsOnMainAdapter = ShiftsOnMainAdapter(
        ::itemClickCallback,
        ::itemLongClickCallback
    )

    override fun setupViews() {

        moveIndicatorOnNextSelection = false

        with(rv_dates) {
            adapter = datesAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    // move indicator along with recycler
                    indicator.translationX -= dx
                }
            })
        }

        with(rv_shifts_on_main) {
            adapter = shiftsOnMainAdapter
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }

        tv_chosen_date.setOnClickListener {
            navigateToDatePicker(binding.currentDayStart)
        }

        btn_today.setOnClickListener { scrollByDayId(Constants.todayId) }

        // listen for date pick
        setFragmentResultListener(DatePickerDialog.DATE_PICKER_KEY) { _, bundle ->
            val time = bundle.getLong(DatePickerDialog.PICK_ACTION_KEY)
            val dayId = Utils.getCalendarInstance(time).getDayId()
            scrollByDayId(dayId)
        }

    }

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

    private fun scrollByDayId(dayId: String) {
        val pos = datesAdapter.currentList.indexOfFirst { it.id == dayId }
        val day = datesAdapter.currentList.getOrNull(pos)

        if (day == null || day.id == datesAdapter.selectedItemId) return

        val firstPos = datesLayoutManager.findFirstVisibleItemPosition()
        val lastPos = datesLayoutManager.findLastVisibleItemPosition()

        if (pos !in firstPos..lastPos) {
            scrollDatesTo(pos)
            moveIndicatorOnNextSelection = false
            indicator.translationX = Constants.dp48
        }

        daySelectedCallback(day, pos)
    }

    private fun daySelectedCallback(day: Day, position: Int) {
        datesAdapter.selectItem(day.id, position)
        updateTitle(day.start)
        viewModel.updateCurrentDay(day)
    }

    private fun itemClickCallback(item: ShiftOnMainItem) {
        // save ui manually since onSaveInstanceState is not called on navigate action
        binding.saveUi()
        viewModel.handleEditSchedule(
            title = getString(R.string.label_edit_schedule),
            id = item.scheduleId
        )
    }

    private fun itemLongClickCallback(item: ShiftOnMainItem) {
        root.showAreYouSureDialog("${item.scheduleName}\n\nВы уверены, что хотите удалить этот график?") {
            viewModel.handleDeleteSchedule(item.scheduleId)
        }
    }

    private fun moveSelectionIndicator(holderLeft: Int) {

        if (!moveIndicatorOnNextSelection) {
            moveIndicatorOnNextSelection = true
            return
        }

        // move indicator maximal close to an outer edge of screen if it is too far
        when {
            indicator.translationX < -indicator.width ->
                indicator.translationX = -indicator.width.toFloat()
            indicator.translationX > rv_dates.width ->
                indicator.translationX = rv_dates.width.toFloat()
        }

        val newTranslationX = (holderLeft - indicator.left).toFloat()

        indicator.animate()
            .setDuration(200L)
            .translationX(newTranslationX)
    }

    private fun scrollDatesTo(pos: Int) {
        if (pos == -1) return
        datesLayoutManager.scrollToPositionWithOffset(pos - 1, -Constants.dp4)
    }

    private fun updateTitle(calendar: Calendar) {
        val monthName = Months.values()[calendar.month].getName(requireContext())
        val date = "${calendar.date}".padStart(2, '0')
        binding.title = "$monthName, $date"
    }

    private fun showNavigationMenu(view: View) {
        PopupMenu(requireContext(), view).apply {
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_item_schedules -> {
                        navigateToSchedules()
                        true
                    }
                    R.id.menu_item_shift_types -> {
                        navigateToShiftTypes()
                        true
                    }
                    else -> false
                }
            }
            inflate(R.menu.bottom_nav_menu)
            show()
        }
    }

    private fun navigateToSettings() {
        val action = MainFragmentDirections.actionNavMainToNavSettings()
        viewModel.navigateWithAction(action)
    }

    private fun navigateToSchedule() {
        val action = MainFragmentDirections
            .actionNavMainToNavSchedule(getString(R.string.label_add_schedule))
        viewModel.navigateWithAction(action)
    }

    private fun navigateToShiftTypes() {
        val action = MainFragmentDirections.actionNavMainToNavShiftTypes()
        viewModel.navigateWithAction(action)
    }

    private fun navigateToSchedules() {
        val action = MainFragmentDirections.actionNavMainToNavSchedules()
        viewModel.navigateWithAction(action)
    }

    inner class MainBinding : Binding() {

        var currentDayStart: Long = Constants.today.timeInMillis

        var title: String by RenderProp("") {
            if (it.isEmpty()) updateTitle(Constants.today) else tv_chosen_date.text = it
        }

        var indicatorTranslation: Float by RenderProp(Constants.dp48) {
            indicator.translationX = it
        }

        var selectedDayId: String by RenderProp(Constants.todayId) {
            datesAdapter.selectItem(it)
        }

        fun saveUi() {
            indicatorTranslation = indicator.translationX
            selectedDayId = datesAdapter.selectedItemId ?: Constants.todayId
        }

        private var days: List<Day> by RenderProp(emptyList(), false) { list ->
            datesAdapter.submitList(list) {
                val pos = list.indexOfFirst { it.id == selectedDayId }
                scrollDatesTo(pos)
            }
        }

        private var shiftItems: List<ShiftOnMainItem> by RenderProp(emptyList()) {
            shiftsOnMainAdapter.submitList(it)
            tv_list_is_empty.isVisible = it.isEmpty()
        }

        override fun bind(data: IViewModelState) {
            data as MainState
            days = data.days
            shiftItems = data.shiftItems
            currentDayStart = data.currentDayStart
        }

        override fun saveUi(outState: Bundle) {
            outState.putString(::selectedDayId.name, datesAdapter.selectedItemId)
        }

        override fun restoreUi(savedState: Bundle?) {
            if (savedState != null) {
                selectedDayId = savedState.getString(::selectedDayId.name, Constants.todayId)
            }
        }

    }

}