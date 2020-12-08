package ru.mulledwine.shiftsredesigned.ui.schedules

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_schedules.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleItem
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.viewmodels.SchedulesViewModel

class SchedulesFragment : BaseFragment<SchedulesViewModel>() {

    companion object {
        private const val TAG = "M_SchedulesFragment"
    }

    override val viewModel: SchedulesViewModel by viewModels()
    override val layout: Int = R.layout.fragment_schedules

    private val schedulesAdapter by lazy {
        SchedulesAdapter(
            longClickListener = ::itemLongClickCallback,
            clickListener = ::itemClickCallback
        )
    }

    private fun itemClickCallback(item: ScheduleItem) {
        viewModel.handleEditSchedule(getString(R.string.label_edit_schedule), item.id)
    }

    private fun itemLongClickCallback(item: ScheduleItem) {
        root.showAreYouSureDialog("${item.name}\n\nВы уверены, что хотите удалить этот график?") {
            viewModel.handleDeleteSchedule(item.id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_shift_types, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menu_item_add).setOnMenuItemClickListener {
            viewModel.handleNavigateAddSchedule(getString(R.string.label_add_schedule))
            true
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

}