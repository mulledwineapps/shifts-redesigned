package ru.mulledwine.shiftsredesigned.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_root.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.ClockTime
import ru.mulledwine.shiftsredesigned.ui.RootActivity
import ru.mulledwine.shiftsredesigned.viewmodels.BaseViewModel
import ru.mulledwine.shiftsredesigned.viewmodels.Loading
import ru.mulledwine.shiftsredesigned.viewmodels.NavigationCommand
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState

abstract class BaseFragment<T : BaseViewModel<out IViewModelState>> : Fragment() {

    companion object {
        private const val TAG = "M_BaseFragment"
    }

    val root: RootActivity get() = activity as RootActivity

    open val binding: Binding? = null
    protected abstract val viewModel: T
    protected abstract val layout: Int

    open val prepareToolbar: (ToolbarBuilder.() -> Unit)? = null
    open val prepareBottombar: (BottombarBuilder.() -> Unit)? = null

    val appbar: AppBarLayout get() = root.appbar
    val toolbar: MaterialToolbar get() = root.toolbar
    val bottombar: BottomAppBar get() = root.bottombar
    val fab: FloatingActionButton get() = root.fab

    // set listeners, tuning views
    abstract fun setupViews()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // restore state
        viewModel.restoreState()
        binding?.restoreUi(savedInstanceState)

        // owner it is view
        viewModel.observeState(viewLifecycleOwner) { binding?.bind(it) }
        // bind default values if viewmodel not loaded data
        if (binding?.isInflated == false) binding?.onFinishInflate()

        viewModel.observeNotifications(viewLifecycleOwner) { root.renderNotification(it) }
        viewModel.observeNavigation(viewLifecycleOwner) { root.viewModel.navigate(it) }

        viewModel.observeLoading(viewLifecycleOwner) {
            renderLoading(it)
        }

        root.toolbar?.setNavigationIcon(R.drawable.ic_round_arrow_back_24)

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        // prepare toolbar
        root.toolbarBuilder
            .invalidate()
            .prepare(prepareToolbar)
            .build(root)

        // prepare bottombar
        root.bottombarBuilder
            .invalidate()
            .prepare(prepareBottombar)
            .build(root)

        setupViews()

        binding?.rebind()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveState()
        binding?.saveUi(outState)
        super.onSaveInstanceState(outState)
    }

    fun navigateToDatePicker(time: Long, viewId: Int = -1) {
        val command = NavigationCommand.To(
            destination = R.id.dialog_date_picker,
            args = bundleOf(
                "time" to time,
                "view_id" to viewId
            )
        )
        viewModel.navigate(command)
    }

    fun navigateToTimePicker(time: ClockTime, viewId: Int = -1) {
        val command = NavigationCommand.To(
            destination = R.id.dialog_time_picker,
            args = bundleOf(
                "time" to time,
                "view_id" to viewId
            )
        )
        viewModel.navigate(command)
    }

    // open, потому что в конкретном фрагменте поведение загрузчика может отличаться
    open fun renderLoading(loadingState: Loading) {
        root.renderLoading(loadingState)
    }

}