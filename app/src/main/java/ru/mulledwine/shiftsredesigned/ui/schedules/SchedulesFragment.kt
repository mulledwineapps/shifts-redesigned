package ru.mulledwine.shiftsredesigned.ui.schedules

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
import kotlinx.android.synthetic.main.fragment_schedules.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.JobItem
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleItem
import ru.mulledwine.shiftsredesigned.extensions.getScheduleGenitive
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.ui.base.Binding
import ru.mulledwine.shiftsredesigned.ui.delegates.RenderProp
import ru.mulledwine.shiftsredesigned.ui.dialogs.ChooseJobDialog
import ru.mulledwine.shiftsredesigned.viewmodels.SchedulesState
import ru.mulledwine.shiftsredesigned.viewmodels.SchedulesViewModel
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState
import ru.mulledwine.shiftsredesigned.viewmodels.base.ViewModelFactory
import kotlin.properties.Delegates

class SchedulesFragment : BaseFragment<SchedulesViewModel>() {

    private val args: SchedulesFragmentArgs by navArgs()

    override val layout: Int = R.layout.fragment_schedules
    override val binding: SchedulesBinding by lazy { SchedulesBinding() }
    override val viewModel: SchedulesViewModel by viewModels {
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
            toolbar.title = getString(R.string.label_schedules)
            root.onNavigationIconClick = null
        }
    }

    private val schedulesAdapter by lazy {
        SchedulesAdapter(
            longClickListener = ::itemLongClickCallback,
            clickListener = ::itemClickCallback
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

        // listen for job pick
        setFragmentResultListener(ChooseJobDialog.CHOOSE_JOB_KEY) { _, bundle ->
            val job = bundle[ChooseJobDialog.SELECTED_JOB] as JobItem
            viewModel.handleUpdateJob(job)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (isSelectionMode) inflater.inflate(R.menu.menu_schedules, menu)
        else super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.children.forEach {
            it.setOnMenuItemClickListener(::onMenuItemClick)
        }
    }

    override fun setupViews() {

        with(rv_schedules) {
            adapter = schedulesAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        tv_schedules_job.setOnClickListener { navigateToDialogChooseJob() }
        btn_add.setOnClickListener { viewModel.handleClickAdd(getString(R.string.label_add_schedule)) }

        viewModel.observeJobs(viewLifecycleOwner) {
            binding.jobItems = it
        }

    }

    private fun navigateToDialogChooseJob() {
        val action = SchedulesFragmentDirections.actionToDialogChooseJob(
            binding.jobItems.toTypedArray()
        )
        viewModel.navigateWithAction(action)
    }

    private fun closeSelectionMenu() {
        schedulesAdapter.clearSelection()
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
        val selected = schedulesAdapter.selectedItems
        val message = if (selected.size == 1) {
            val item = selected.first()
            "Удалить график?\n\n${binding.jobName}\n${item.duration}"
        }
        else "Удалить ${selected.size.getScheduleGenitive()}?"

        root.askWhetherToDelete(message) {
            if (selected.size == 1) viewModel.handleDeleteSchedule(selected.first().id)
            else viewModel.handleDeleteSchedules(selected.map { it.id })
            closeSelectionMenu()
        }
    }

    private fun itemClickCallback(item: ScheduleItem) {
        if (isSelectionMode) onSelectionChanged()
        else viewModel.handleClickEdit(getString(R.string.label_edit_schedule), item.id)
    }

    private fun onSelectionChanged() {
        schedulesAdapter.selectedItems.size.let { size ->
            if (size == 0) isSelectionMode = false
            else {
                toolbar.title = "$size"
                toolbar.menu.children
                    .filterNot { it.itemId in setOf(R.id.menu_item_delete, R.id.menu_item_archive) }
                    .forEach { it.isVisible = size == 1 }
            }
        }
    }

    private fun itemLongClickCallback() {
        isSelectionMode = schedulesAdapter.selectedItems.isNotEmpty()
    }

    private fun submitItems(list: List<ScheduleItem>) {
        schedulesAdapter.submitList(list) {
            if (tv_list_is_empty == null) return@submitList // exit in process
            tv_list_is_empty.isVisible = list.isEmpty()
        }
    }

    inner class SchedulesBinding : Binding() {

        var jobItems: List<JobItem> = emptyList()

        var jobName by RenderProp(args.item.jobItem.name) {
            tv_schedules_job.text = it
        }

        var scheduleItems: List<ScheduleItem> by RenderProp(args.item.scheduleItems) {
            submitItems(it)
        }

        override fun bind(data: IViewModelState) {
            data as SchedulesState
            scheduleItems = data.scheduleItems
            jobName = data.jobName
        }

    }

}