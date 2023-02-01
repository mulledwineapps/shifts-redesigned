package ru.mulledwine.shifts.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_root.*
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.entities.AlarmParcelable
import ru.mulledwine.shifts.ui.base.BaseActivity
import ru.mulledwine.shifts.viewmodels.Notify
import ru.mulledwine.shifts.viewmodels.RootViewModel
import ru.mulledwine.shifts.viewmodels.base.IViewModelState


@AndroidEntryPoint
class RootActivity : BaseActivity<RootViewModel>() {

    companion object {
        private const val TAG = "M_RootActivity"
        const val ALARM_PARAM = "ALARM_PARAM"
    }

    override val layout: Int = R.layout.activity_root
    public override val viewModel: RootViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val alarm = intent.getParcelableExtra<AlarmParcelable>(ALARM_PARAM)
        Log.d(TAG, "onCreate: alarm $alarm")
        // FIXME: 22.05.2021 не всегда меняется extra, например, label
        // FIXME: 22.05.2021 не появляется активити с будильником, но срабатывает сигнал
        // (быстро сама уходит в Pause)
        if (alarm != null) setTheme(R.style.Theme_AlarmGoOff)

        super.onCreate(savedInstanceState)

        if (alarm != null) navController.graph = navController.graph.apply {
            this.setStartDestination(R.id.nav_alarm_go_off)
        }

        // top level destination ids
        val appbarConfiguration = AppBarConfiguration(setOf(R.id.nav_main))
        setupActionBarWithNavController(navController, appbarConfiguration)
    }

    override fun renderNotification(notify: Notify) {

        if (notify is Notify.ToastMessage) {
            Toast.makeText(this, notify.message, notify.duration).show()
            return
        }

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
            else -> throw IllegalStateException()
        }

        snackbar.show()
    }

    override fun subscribeOnState(state: IViewModelState) {
        // DO something with state
    }

}