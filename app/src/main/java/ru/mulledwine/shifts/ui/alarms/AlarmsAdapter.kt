package ru.mulledwine.shifts.ui.alarms

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_alarm.*
import ru.mulledwine.shifts.Constants
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.models.AlarmItem

class AlarmsAdapter(
    private val longClickListener: (() -> Unit)? = null,
    private val clickListener: (id: Int) -> Unit,
    private val toggleListener: (alarm: AlarmItem, Boolean) -> Unit
) :
    ListAdapter<AlarmItem, AlarmsAdapter.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TAG = "M_AlarmsAdapter"
    }

    var isSelectionAllowed = false
    val selectedItems = mutableSetOf<AlarmItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val containerView = inflater.inflate(R.layout.item_alarm, parent, false)
        return ViewHolder(containerView, toggleListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.containerView.setOnClickListener {
            if (selectedItems.isNotEmpty()) handleSelection(item, holder)
            clickListener.invoke(item.id)
        }
        holder.containerView.setOnLongClickListener {
            handleSelection(item, holder)
            longClickListener?.invoke()
            true
        }
        holder.bind(item)
        holder.setSelection(selectedItems.contains(item))
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    private fun handleSelection(item: AlarmItem, holder: ViewHolder) {
        if (selectedItems.contains(item)) {
            selectedItems.remove(item)
            holder.setSelection(false)
        } else {
            selectedItems.add(item)
            holder.setSelection(true)
        }
    }

    class ViewHolder(
        override val containerView: View,
        private val toggleListener: (alarm: AlarmItem, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: AlarmItem) {
            tv_alarm_item_time.text = item.time
            tv_alarm_item_job.text = item.jobName
            tv_alarm_item_shift.text = item.shiftName
            switch_alarm_item.isChecked = item.isActive

            switch_alarm_item.setOnCheckedChangeListener { _, isChecked ->
                Log.d(TAG, "OnCheckedChangeListener: $isChecked")
                toggleListener.invoke(item, isChecked)
            }
        }

        fun setSelection(isSelected: Boolean) {
            val color = if (isSelected) Constants.selectionColor else Color.WHITE
            containerView.backgroundTintList = ColorStateList.valueOf(color)
            iv_alarm_check.isVisible = isSelected
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AlarmItem>() {
        override fun areItemsTheSame(oldItem: AlarmItem, newItem: AlarmItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AlarmItem, newItem: AlarmItem): Boolean =
            oldItem == newItem
    }

}