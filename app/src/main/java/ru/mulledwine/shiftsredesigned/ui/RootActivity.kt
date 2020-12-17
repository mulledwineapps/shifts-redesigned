package ru.mulledwine.shiftsredesigned.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_root.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.ui.base.BaseActivity
import ru.mulledwine.shiftsredesigned.viewmodels.Notify
import ru.mulledwine.shiftsredesigned.viewmodels.RootViewModel
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState


class RootActivity : BaseActivity<RootViewModel>() {

    companion object {
        private const val TAG = "M_RootActivity"
    }

    override val layout: Int = R.layout.activity_root
    public override val viewModel: RootViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // top level destination ids
        val appbarConfiguration = AppBarConfiguration(setOf(R.id.nav_main))
        setupActionBarWithNavController(navController, appbarConfiguration)
    }

    override fun renderNotification(notify: Notify) {

        val snackbar = Snackbar.make(root_container, notify.message, notify.duration)
        notify.anchorViewId?.let { snackbar.setAnchorView(it) }

        when (notify) {
            is Notify.TextMessage -> Unit /* nothing */
            is Notify.ActionMessage -> {
                snackbar.setAction(notify.actionLabel) {
                    notify.actionHandler.invoke()
                }
            }
            is Notify.ErrorMessage -> {
                with(snackbar) {
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel) {
                        notify.errHandler?.invoke()
                    }
                }
            }
        }

        snackbar.show()
    }

    override fun subscribeOnState(state: IViewModelState) {
        // DO something with state
    }

}