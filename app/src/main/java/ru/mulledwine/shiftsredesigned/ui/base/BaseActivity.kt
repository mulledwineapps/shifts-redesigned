package ru.mulledwine.shiftsredesigned.ui.base

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.bottomappbar.BottomAppBar
import kotlinx.android.synthetic.main.activity_root.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.PrefManager
import ru.mulledwine.shiftsredesigned.viewmodels.BaseViewModel
import ru.mulledwine.shiftsredesigned.viewmodels.Loading
import ru.mulledwine.shiftsredesigned.viewmodels.NavigationCommand
import ru.mulledwine.shiftsredesigned.viewmodels.Notify
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState

abstract class BaseActivity<T : BaseViewModel<out IViewModelState>> : AppCompatActivity() {

    companion object {
        private const val TAG = "M_BaseActivity"
    }

    protected abstract val viewModel: T
    protected abstract val layout: Int
    lateinit var navController: NavController
    lateinit var progressDialog: AlertDialog

    val toolbarBuilder = ToolbarBuilder()
    val bottombarBuilder = BottombarBuilder()

    abstract fun subscribeOnState(state: IViewModelState)

    abstract fun renderNotification(notify: Notify)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        setSupportActionBar(toolbar)

        // чтобы не нарушать data flow, не обращаемся напрямую ко вью модели, а наблюдаем её состояние
        viewModel.observeState(this) { subscribeOnState(it) }
        viewModel.observeNotifications(this) { renderNotification(it) }
        viewModel.observeNavigation(this) { subscribeOnNavigation(it) }
        viewModel.observeLoading(this) {
            renderLoading(it)
        }

        navController = findNavController(R.id.nav_host_fragment)

        val dialogView = View.inflate(this, R.layout.dialog_progress_bar, null)
        progressDialog =
            AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setCustomTitle(null)
                .setView(dialogView)
                .setCancelable(false)
                .create()

        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveState()
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel.restoreState()
    }

    // чтобы при клавише назад мы перемещались вверх по иерархии
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun subscribeOnNavigation(command: NavigationCommand) {
        when (command) {
            is NavigationCommand.To -> {
                try {
                    navController.navigate(
                        command.destination,
                        command.args,
                        command.options,
                        command.extras
                    )
                } catch (e: Throwable) {
                    // несмотря на OnSingleClickListener с задержкой 300 мс
                    // всё равно падает иногда, пытаясь выполнить повторную навигацию
                    Log.e(TAG, "subscribeOnNavigation: ${e.message} ${e.stackTrace}")
                }

            }
            NavigationCommand.Up -> navController.popBackStack()
        }
    }

    open fun renderLoading(loadingState: Loading) {
        when (loadingState) {
            is Loading.ShowLoading -> progress.isVisible = true
            is Loading.ShowBlockingLoading -> {
                progressDialog.show()
            }
            is Loading.HideLoading -> {
                progress.isVisible = false
                progressDialog.hide()
            }
        }
    }

    fun showAreYouSureDialog(message: String, handler: () -> Unit) {
        val dialog = AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setMessage(message)
            .setPositiveButton(R.string.yes_button) { _, _ -> handler.invoke() }
            .setNegativeButton(R.string.no_button, null)
            .create().apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
            }

        dialog.setOnShowListener {
            val positiveBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positiveBtn.setTextColor(this.getColor(R.color.color_attention))
        }

        dialog.show()
    }

}


class ToolbarBuilder() {

    var title: String? = null
    var visibility: Boolean = true

    fun setTitle(title: String): ToolbarBuilder {
        this.title = title
        return this
    }

    fun setVisibility(isVisible: Boolean): ToolbarBuilder {
        this.visibility = isVisible
        return this
    }

    // set default values
    fun invalidate(): ToolbarBuilder {
        this.title = null
        this.visibility = true
        return this
    }

    fun prepare(prepareFn: (ToolbarBuilder.() -> Unit)?): ToolbarBuilder {
        prepareFn?.invoke(this)
        return this
    }

    fun build(context: FragmentActivity) {
        // показать аппбар, если вернулись со скроллируемого объекта
        context.appbar.setExpanded(true, true)

        context.toolbar.isVisible = visibility
        if (title != null) context.toolbar.title = title
    }
}


class BottombarBuilder() {
    private var visible: Boolean = true
    private var fabClickListener: (() -> Unit)? = null
    private var onNavigationItemClickListener: ((View) -> Unit)? = null
    private var onMenuItemClickListener: ((MenuItem) -> Unit)? = null

    fun setVisibility(isVisible: Boolean): BottombarBuilder {
        visible = isVisible
        return this
    }

    fun setFabClickListener(listener: () -> Unit) {
        fabClickListener = listener
    }

    fun setOnNavigationItemClickListener(listener: (View) -> Unit) {
        onNavigationItemClickListener = listener
    }

    fun setOnMenuItemClickListener(listener: (MenuItem) -> Unit) {
        onMenuItemClickListener = listener
    }

    fun prepare(prepareFn: (BottombarBuilder.() -> Unit)?): BottombarBuilder {
        prepareFn?.invoke(this)
        return this
    }

    fun invalidate(): BottombarBuilder {
        visible = false
        fabClickListener = null
        onNavigationItemClickListener = null
        onMenuItemClickListener = null
        return this
    }

    fun build(context: FragmentActivity) {

        context.bottombar.isVisible = visible

        // do not use show & hide since no animation required
        with(context.fab) {
            alpha = if (visible) 1f else 0f
            isEnabled = visible
        }

        if (visible) {
            context.fab.setOnClickListener {
                fabClickListener?.invoke()
            }
            context.bottombar.setOnMenuItemClickListener {
                onMenuItemClickListener?.invoke(it)
                true
            }
            context.bottombar.setNavigationOnClickListener {
                onNavigationItemClickListener?.invoke(it)
            }
        }

    }

}