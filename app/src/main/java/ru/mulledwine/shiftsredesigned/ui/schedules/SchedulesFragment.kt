package ru.mulledwine.shiftsredesigned.ui.schedules

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_schedules.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleItem
import ru.mulledwine.shiftsredesigned.extensions.getScheduleGenitive
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.viewmodels.SchedulesViewModel
import kotlin.properties.Delegates

class SchedulesFragment : BaseFragment<SchedulesViewModel>() {

    companion object {
        private const val TAG = "M_SchedulesFragment"
    }

    override val viewModel: SchedulesViewModel by viewModels()
    override val layout: Int = R.layout.fragment_schedules

    private var isSelectionMode: Boolean by Delegates.observable(false) { _, _, newValue ->
        root.invalidateOptionsMenu()
        if (newValue) {
            toolbar.setNavigationIcon(R.drawable.ic_round_close_24)
            toolbar.title = "1"
            root.onNavigationIconClick = ::closeSelectionMenu
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
            toolbar.title = "Графики"
            root.onNavigationIconClick = null
        }
    }

    private val schedulesAdapter by lazy {
        SchedulesAdapter(
            longClickListener = ::itemLongClickCallback,
            clickListener = ::itemClickCallback
        ).apply { isSelectionAllowed = true }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuRes = if (isSelectionMode) R.menu.menu_schedules else R.menu.menu_add
        inflater.inflate(menuRes, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.children.forEach {
            it.setOnMenuItemClickListener(::onMenuItemClick)
        }
    }

    override fun setupViews() {
        with(rv_schedules) {
            adapter = schedulesAdapter
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }

        viewModel.observeSchedules(viewLifecycleOwner) {
            schedulesAdapter.submitList(it)
        }
    }

    private fun closeSelectionMenu() {
        schedulesAdapter.clearSelection()
        isSelectionMode = false
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_add -> navigateAddSchedule()
            R.id.menu_item_delete -> onDeleteRequested()
            else -> return false
        }
        return true
    }

    private fun navigateAddSchedule() {
        val action = SchedulesFragmentDirections
            .actionNavSchedulesToNavSchedule(getString(R.string.label_add_schedule))
        viewModel.navigateWithAction(action)
    }

    private fun onDeleteRequested() {
        val selected = schedulesAdapter.selectedItems
        val message = if (selected.size == 1) "Удалить график ${selected.first().name}?"
        else "Удалить ${selected.size.getScheduleGenitive()}?"

        root.askWhetherToDelete(message) {
            if (selected.size == 1) viewModel.handleDeleteSchedule(selected.first().id)
            else viewModel.handleDeleteSchedules(selected.map { it.id })
            schedulesAdapter.clearSelection()
            isSelectionMode = false
        }
    }

    private fun itemClickCallback(item: ScheduleItem) {
        if (isSelectionMode) onSelectionSizeChanged()
        else viewModel.handleEditSchedule(getString(R.string.label_edit_schedule), item.id)
    }

    private fun onSelectionSizeChanged() {
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

}