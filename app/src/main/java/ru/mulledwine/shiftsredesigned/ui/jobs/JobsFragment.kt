package ru.mulledwine.shiftsredesigned.ui.jobs

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.children
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_jobs.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.JobItem
import ru.mulledwine.shiftsredesigned.extensions.getJobGenitive
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.ui.dialogs.JobDialog
import ru.mulledwine.shiftsredesigned.viewmodels.JobsViewModel
import kotlin.properties.Delegates

@AndroidEntryPoint
class JobsFragment : BaseFragment<JobsViewModel>() {

    override val viewModel: JobsViewModel by viewModels()
    override val layout: Int = R.layout.fragment_jobs

    // TODO move the logic to BaseFragment
    private var isSelectionMode: Boolean by Delegates.observable(false) { _, _, newValue ->
        root.invalidateOptionsMenu()
        if (newValue) {
            toolbar.setNavigationIcon(R.drawable.ic_round_close_24)
            toolbar.title = "1"
            root.onNavigationIconClick = ::closeSelectionMenu
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
            toolbar.title = getString(R.string.label_jobs)
            root.onNavigationIconClick = null
        }
    }

    private val jobsAdapter by lazy {
        JobsAdapter(
            longClickListener = ::itemLongClickCallback,
            clickListener = ::itemClickCallback
        ).apply { isSelectionAllowed = true }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

        // listen for job
        setFragmentResultListener(JobDialog.JOB_KEY) { _, bundle ->
            val id = bundle.getInt(JobDialog.JOB_ID)
            val name = bundle.getString(JobDialog.JOB_NAME, "")
            if (id == -1) viewModel.handleAddJob(name)
            else viewModel.handleUpdateJob(id, name)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuRes = if (isSelectionMode) R.menu.menu_jobs else R.menu.menu_add
        inflater.inflate(menuRes, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.children.forEach {
            it.setOnMenuItemClickListener(::onMenuItemClick)
        }
    }

    override fun setupViews() {
        with(rv_jobs) {
            adapter = jobsAdapter
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }

        viewModel.observeJobs(viewLifecycleOwner) {
            jobsAdapter.submitList(it)
        }
    }

    private fun closeSelectionMenu() {
        jobsAdapter.clearSelection()
        isSelectionMode = false
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_add -> navigateAddJob()
            R.id.menu_item_delete -> onDeleteRequested()
            else -> return false
        }
        if (item.itemId != R.id.menu_item_delete) closeSelectionMenu()
        return true
    }

    private fun navigateAddJob() {
        viewModel.navigateJobDialog()
    }

    private fun onDeleteRequested() {
        val selected = jobsAdapter.selectedItems
        val message = if (selected.size == 1) "Удалить работу ${selected.first().name}?"
        else "Удалить ${selected.size.getJobGenitive()}?"

        root.askWhetherToDelete(message) {
            if (selected.size == 1) viewModel.handleDeleteJob(selected.first().id)
            else viewModel.handleDeleteJobs(selected.map { it.id })
            jobsAdapter.clearSelection()
            isSelectionMode = false
        }
    }

    private fun itemClickCallback(item: JobItem) {
        if (isSelectionMode) onSelectionChanged()
        else viewModel.navigateJobDialog(item)
    }

    private fun onSelectionChanged() {
        jobsAdapter.selectedItems.size.let { size ->
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
        isSelectionMode = jobsAdapter.selectedItems.isNotEmpty()
    }

}