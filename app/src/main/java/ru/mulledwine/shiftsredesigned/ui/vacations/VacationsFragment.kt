package ru.mulledwine.shiftsredesigned.ui.vacations

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_vacations.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleItem
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleShort
import ru.mulledwine.shiftsredesigned.data.local.models.VacationItem
import ru.mulledwine.shiftsredesigned.extensions.getVacationGenitive
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.ui.base.Binding
import ru.mulledwine.shiftsredesigned.ui.delegates.RenderProp
import ru.mulledwine.shiftsredesigned.ui.dialogs.ChooseScheduleDialog
import ru.mulledwine.shiftsredesigned.viewmodels.VacationsState
import ru.mulledwine.shiftsredesigned.viewmodels.VacationsViewModel
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState
import ru.mulledwine.shiftsredesigned.viewmodels.base.ViewModelFactory
import kotlin.properties.Delegates

class VacationsFragment : BaseFragment<VacationsViewModel>() {

    companion object {
        private const val TAG = "M_VacationsFragment"
    }

    private val args: VacationsFragmentArgs by navArgs()

    override val layout: Int = R.layout.fragment_vacations
    override val binding: VacationsBinding by lazy {
        VacationsBinding()
    }
    override val viewModel: VacationsViewModel by viewModels {
        ViewModelFactory(
            owner = this,
            params = args.item
        )
    }

    private var isSelectionMode: Boolean by Delegates.observable(false) { _, _, newValue ->
        root.invalidateOptionsMenu()
        if (newValue) {
            toolbar.setNavigationIcon(R.drawable.ic_round_close_24)
            toolbar.title = "1"
            root.onNavigationIconClick = ::closeSelectionMenu
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
            toolbar.title = "Отпуска"
            root.onNavigationIconClick = null
        }
    }

    private val vacationsAdapter by lazy {
        VacationsAdapter(
            longClickListener = ::itemLongClickCallback,
            clickListener = ::itemClickCallback
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

        // listen for schedule pick
        setFragmentResultListener(ChooseScheduleDialog.CHOOSE_SCHEDULE_KEY) { _, bundle ->
            val schedule = bundle[ChooseScheduleDialog.SELECTED_SCHEDULE] as ScheduleShort
            viewModel.handleUpdateSchedule(schedule)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (isSelectionMode) inflater.inflate(R.menu.menu_delete, menu)
        else super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.children.forEach {
            it.setOnMenuItemClickListener(::onMenuItemClick)
        }
    }

    override fun setupViews() {

        with(rv_vacations) {
            adapter = vacationsAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }

        tv_schedule_name.setOnClickListener { navigateToDialogChooseSchedule() }
        btn_add.setOnClickListener { viewModel.handleClickAdd(getString(R.string.label_add_vacation)) }

        viewModel.observeSchedules(viewLifecycleOwner) {
            binding.scheduleItems = it
        }

    }

    private fun navigateToDialogChooseSchedule() {
        val action = VacationsFragmentDirections.actionToDialogChooseSchedule(
            binding.scheduleItems.toTypedArray()
        )
        viewModel.navigateWithAction(action)
    }

    private fun submitItems(list: List<VacationItem>) {
        vacationsAdapter.submitList(list) {
            if (tv_list_is_empty == null) return@submitList // exit in process
            tv_list_is_empty.isVisible = list.isEmpty()
        }
    }

    private fun closeSelectionMenu() {
        vacationsAdapter.clearSelection()
        isSelectionMode = false
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_delete -> onDeleteRequested()
            else -> return false
        }
        return true
    }

    private fun onDeleteRequested() {
        val selected = vacationsAdapter.selectedItems
        val message = if (selected.size == 1) "Удалить отпуск ${selected.first().duration}?"
        else "Удалить ${selected.size.getVacationGenitive()}?"

        root.askWhetherToDelete(message) {
            if (selected.size == 1) viewModel.handleDeleteVacation(selected.first().id)
            else viewModel.handleDeleteVacations(selected.map { it.id })
            vacationsAdapter.clearSelection()
            isSelectionMode = false
        }
    }

    private fun itemClickCallback(item: VacationItem) {
        if (isSelectionMode) onSelectionSizeChanged()
        else viewModel.handleClickEdit(getString(R.string.label_edit_vacation), item.id)
    }

    private fun onSelectionSizeChanged() {
        vacationsAdapter.selectedItems.size.let { size ->
            if (size == 0) isSelectionMode = false
            else toolbar.title = "$size"
        }
    }

    private fun itemLongClickCallback() {
        isSelectionMode = vacationsAdapter.selectedItems.isNotEmpty()
    }

    inner class VacationsBinding : Binding() {

        var scheduleItems: List<ScheduleItem> = emptyList()

        var scheduleName by RenderProp(args.item.schedule.name) {
            tv_schedule_name.text = it
        }

        var vacationItems: List<VacationItem> by RenderProp(args.item.vacationItems) {
            submitItems(it)
        }

        override fun bind(data: IViewModelState) {
            data as VacationsState
            vacationItems = data.vacationItems
            scheduleName = data.scheduleName
        }

    }

}