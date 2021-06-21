package ru.mulledwine.shiftsredesigned.ui.alarmgooff

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.util.Log
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_alarm_go_off.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.entities.AlarmParcelable
import ru.mulledwine.shiftsredesigned.data.local.models.TimeUnits
import ru.mulledwine.shiftsredesigned.ui.RootActivity
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.ui.base.ToolbarBuilder
import ru.mulledwine.shiftsredesigned.utils.Utils
import ru.mulledwine.shiftsredesigned.viewmodels.AlarmsViewModel

@AndroidEntryPoint
class AlarmGoOff : BaseFragment<AlarmsViewModel>() {

    companion object {
        private const val TAG = "M_AlarmGoOff"
    }

    override val layout: Int = R.layout.fragment_alarm_go_off
    override val viewModel: AlarmsViewModel by viewModels()

    private lateinit var alarm: AlarmParcelable
    private var isStopped = false

    override val prepareToolbar: (ToolbarBuilder.() -> Unit) = {
        setVisibility(false)
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        if (!isStopped) snooze()
        viewModel.navigateUp() // TODO remove task не срабатывает без navigate up
        root.finishAndRemoveTask()
        super.onPause()
    }

    override fun setupViews() {

        alarm = root.intent.getParcelableExtra(RootActivity.ALARM_PARAM)!!

        tv_alarm_time.text = alarm.time.toString()
        tv_alarm_label.text = alarm.label

        swipe_button.setSwipeLeftListener {
            // выключить сигнал и повторить через 10 минут
            stopAlarm()
            snooze()
            root.finishAndRemoveTask()
        }

        swipe_button.setSwipeRightListener {
            // выключить сигнал
            stopAlarm()
            root.finishAndRemoveTask()
        }

        // reschedule the alarm if the schedule is cyclic
//        viewModel.rescheduleAlarm(alarm.id) { time ->
//            Utils.setAlarm(this, alarm, time)
//        }

        // fire the alarm
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        val mediaPlayer: MediaPlayer = MediaPlayer().apply {
            setDataSource(requireContext(), uri)
            setAudioAttributes(audioAttributes)
            prepare()
        }

        // mediaPlayer ?: throw Exception("Не удалось создать медиа проигрыватель") // TODO check on alarm setup

        mediaPlayer.start() // no need to call prepare(); create() does
    }

    private fun snooze() {
        val time = Utils.getTime() + 1 * TimeUnits.MINUTE.value
        Utils.setAlarm(requireContext(), alarm, time)
    }

    private fun stopAlarm() {
        isStopped = true
        // stop the player
    }
}