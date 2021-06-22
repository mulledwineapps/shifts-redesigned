package ru.mulledwine.shifts.ui.shifttypes

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_shift_types.*
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.models.ShiftTypeListItem
import ru.mulledwine.shifts.ui.base.BaseFragment
import ru.mulledwine.shifts.viewmodels.ShiftTypesViewModel

@AndroidEntryPoint
class ShiftTypesFragment : BaseFragment<ShiftTypesViewModel>() {

    companion object {
        private const val TAG = "M_ShiftTypesFragment"
    }

    override val viewModel: ShiftTypesViewModel by viewModels()
    override val layout: Int = R.layout.fragment_shift_types

    private val shiftTypesAdapter by lazy {
        ShiftTypesAdapter(
            longClickListener = ::itemLongClickCallback,
            clickListener = ::itemClickCallback
        )
    }

    private fun itemClickCallback(item: ShiftTypeListItem) {
        viewModel.handleNavigateEditShiftType(getString(R.string.shift_type_edit_label), item.id)
    }

    private fun itemLongClickCallback(item: ShiftTypeListItem) {
        root.askWhetherToDelete("Удалить тип смены ${item.name}?") {
            viewModel.handleDeleteShiftType(item.id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menu_item_add).setOnMenuItemClickListener {
            viewModel.handleNavigateAddShiftType(getString(R.string.shift_type_add_label))
            true
        }
    }

    override fun setupViews() {
        with(rv_shift_types) {
            adapter = shiftTypesAdapter
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }

        viewModel.observeShiftTypes(viewLifecycleOwner) {
            shiftTypesAdapter.submitList(it)
        }
    }

}