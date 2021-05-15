package ru.mulledwine.shiftsredesigned.ui.alarms

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_alarms.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.AlarmItem
import ru.mulledwine.shiftsredesigned.extensions.getAlarmGenitive
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.utils.Utils
import ru.mulledwine.shiftsredesigned.viewmodels.AlarmsViewModel
import kotlin.properties.Delegates

class AlarmsFragment : BaseFragment<AlarmsViewModel>() {

    override val viewModel: AlarmsViewModel by viewModels()
    override val layout: Int = R.layout.fragment_alarms

    // TODO запускать проверку будильников при старте приложения, может и при срабатывании будильника -
    // вдруг что-то пошло не так и pending intent есть, а будильника - нет

    // https://stackoverflow.com/questions/7308298/should-i-use-pendingintent-getservice-or-getbroadcast-with-alarmmanager
    // узнать про wake lock (вроде предотвращает засыпание устройства, прежде чем сработает будильник)

    // TODO move the logic to BaseFragment
    private var isSelectionMode: Boolean by Delegates.observable(false) { _, _, newValue ->
        root.invalidateOptionsMenu()
        if (newValue) {
            toolbar.setNavigationIcon(R.drawable.ic_round_close_24)
            toolbar.title = "1"
            root.onNavigationIconClick = ::closeSelectionMenu
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
            toolbar.title = getString(R.string.label_alarms)
            root.onNavigationIconClick = null
        }
    }
    
    private val alarmsAdapter by lazy {
        AlarmsAdapter(
            longClickListener = ::itemLongClickCallback,
            clickListener = ::itemClickCallback,
            toggleListener = ::itemToggleCallback
        ).apply { isSelectionAllowed = true }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuRes = if (isSelectionMode) R.menu.menu_delete else R.menu.menu_add
        inflater.inflate(menuRes, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.children.forEach {
            it.setOnMenuItemClickListener(::onMenuItemClick)
        }
    }

    override fun setupViews() {
        with(rv_alarms) {
            adapter = alarmsAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }

        viewModel.observeAlarms(viewLifecycleOwner) {
            alarmsAdapter.submitList(it)
        }
    }
    
    private fun closeSelectionMenu() {
        alarmsAdapter.clearSelection()
        isSelectionMode = false
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_add -> navigateAddAlarm()
            R.id.menu_item_delete -> onDeleteRequested()
            else -> return false
        }
        if (item.itemId != R.id.menu_item_delete) closeSelectionMenu()
        return true
    }
    
    private fun navigateAddAlarm() {
        viewModel.handleNavigateAddAlarm(getString(R.string.alarm_add_label))
    }

    private fun onDeleteRequested() {
        val selected = alarmsAdapter.selectedItems
        val message = if (selected.size == 1) "Удалить будильник ${selected.first().time}?"
        else "Удалить ${selected.size.getAlarmGenitive()}?"

        root.askWhetherToDelete(message) {
            if (selected.size == 1) viewModel.handleDeleteAlarm(selected.first().id)
            else viewModel.handleDeleteAlarms(selected.map { it.id })
            alarmsAdapter.clearSelection()
            isSelectionMode = false
        }
    }

    private fun itemClickCallback(id: Int) {
        if (isSelectionMode) onSelectionChanged()
        else viewModel.handleNavigateEditAlarm(getString(R.string.alarm_edit_label), id)
    }

    // TODO предохранитель от слишком частого использования переключателя
    private fun itemToggleCallback(id: Int, flagActive: Boolean) {
        viewModel.handleToggleAlarm(id, flagActive, ::setAlarm, ::cancelAlarm)
    }

    private fun setAlarm(id: Int, time: Long) = Utils.setAlarm(requireContext(), id, time)
    private fun cancelAlarm(id: Int) = Utils.cancelAlarm(requireContext(), id)

    private fun onSelectionChanged() {
        alarmsAdapter.selectedItems.size.let { size ->
            if (size == 0) isSelectionMode = false
            else toolbar.title = "$size"
        }
    }

    private fun itemLongClickCallback() {
        isSelectionMode = alarmsAdapter.selectedItems.isNotEmpty()
    }

}