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
import ru.mulledwine.shiftsredesigned.data.local.models.JobItem
import ru.mulledwine.shiftsredesigned.data.local.models.VacationItem
import ru.mulledwine.shiftsredesigned.extensions.getVacationGenitive
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.ui.base.Binding
import ru.mulledwine.shiftsredesigned.ui.delegates.RenderProp
import ru.mulledwine.shiftsredesigned.ui.dialogs.ChooseJobDialog
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

        // listen for job pick
        setFragmentResultListener(ChooseJobDialog.CHOOSE_JOB_KEY) { _, bundle ->
            val job = bundle[ChooseJobDialog.SELECTED_JOB] as JobItem
            viewModel.handleUpdateJob(job)
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

        tv_vacations_job.setOnClickListener { navigateToDialogChooseJob() }
        btn_add.setOnClickListener { viewModel.handleClickAdd(getString(R.string.label_add_vacation)) }

        viewModel.observeJobs(viewLifecycleOwner) {
            binding.jobItems = it
        }

    }

    private fun navigateToDialogChooseJob() {
        val action = VacationsFragmentDirections.actionToDialogChooseJob(
            binding.jobItems.toTypedArray()
        )
        viewModel.navigateWithAction(action)
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
        if (isSelectionMode) onSelectionChanged()
        else viewModel.handleClickEdit(getString(R.string.label_edit_vacation), item.id)
    }

    private fun onSelectionChanged() {
        vacationsAdapter.selectedItems.size.let { size ->
            if (size == 0) isSelectionMode = false
            else toolbar.title = "$size"
        }
    }

    private fun itemLongClickCallback() {
        isSelectionMode = vacationsAdapter.selectedItems.isNotEmpty()
    }

    private fun submitItems(list: List<VacationItem>) {
        vacationsAdapter.submitList(list) {
            if (tv_list_is_empty == null) return@submitList // exit in process
            tv_list_is_empty.isVisible = list.isEmpty()
        }
    }

    inner class VacationsBinding : Binding() {

        var jobItems: List<JobItem> = emptyList()

        var jobName by RenderProp(args.item.jobItem.name) {
            tv_vacations_job.text = it
        }

        var vacationItems: List<VacationItem> by RenderProp(args.item.vacationItems) {
            submitItems(it)
        }

        override fun bind(data: IViewModelState) {
            data as VacationsState
            vacationItems = data.vacationItems
            jobName = data.jobName
        }

    }

}