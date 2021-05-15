package ru.mulledwine.shiftsredesigned.ui

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_root.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.ui.base.BaseActivity
import ru.mulledwine.shiftsredesigned.viewmodels.AlarmsViewModel
import ru.mulledwine.shiftsredesigned.viewmodels.Notify
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState

class AlarmActivity : BaseActivity<AlarmsViewModel>() {

    companion object {
        const val ALARM_ID_PARAM = "ALARM_ID_PARAM"
    }

    public override val viewModel: AlarmsViewModel by viewModels()
    override val layout: Int = R.layout.activity_alarm

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val alarmId = intent.getIntExtra(ALARM_ID_PARAM, -1)

        // reschedule the alarm if needed
//        viewModel.rescheduleAlarm(alarmId) { time ->
//            Utils.setAlarm(this, alarmId, time)
//        }

        // fire the alarm
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        val mediaPlayer: MediaPlayer = MediaPlayer().apply {
            setDataSource(this@AlarmActivity, uri)
            setAudioAttributes(audioAttributes)
            prepare()
        }

        // mediaPlayer ?: throw Exception("Не удалось создать медиа проигрыватель") // TODO check on alarm setup

        mediaPlayer.start() // no need to call prepare(); create() does

    }


    override fun subscribeOnState(state: IViewModelState) {
    }

    override fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(root_container, notify.message, notify.duration)
        with(snackbar) {
            setBackgroundTint(getColor(R.color.design_default_color_error))
            setTextColor(getColor(android.R.color.white))
            setActionTextColor(getColor(android.R.color.white))
        }
    }

}