package ru.mulledwine.shifts.ui.schedules

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_schedule.*
import ru.mulledwine.shifts.Constants
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.models.ScheduleItem

class SchedulesAdapter(
    private val longClickListener: (() -> Unit)? = null,
    private val clickListener: (item: ScheduleItem) -> Unit
) :
    ListAdapter<ScheduleItem, SchedulesAdapter.ViewHolder>(DiffCallback()) {

    val selectedItems = mutableSetOf<ScheduleItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val containerView = inflater.inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(containerView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.containerView.setOnClickListener {
            if (selectedItems.isNotEmpty()) handleSelection(item, holder)
            clickListener.invoke(item)
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

    private fun handleSelection(item: ScheduleItem, holder: ViewHolder) {
        if (selectedItems.contains(item)) {
            selectedItems.remove(item)
            holder.setSelection(false)
        } else {
            selectedItems.add(item)
            holder.setSelection(true)
        }
    }

    class ViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: ScheduleItem) {
            tv_schedule_item_ordinal.text = item.ordinal.toString()
            tv_schedule_item_duration.text = item.duration
        }

        fun setSelection(isSelected: Boolean) {
            val color = if (isSelected) Constants.selectionColor else Color.WHITE
            containerView.backgroundTintList = ColorStateList.valueOf(color)
            iv_check.isVisible = isSelected
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ScheduleItem>() {
        override fun areItemsTheSame(
            oldItem: ScheduleItem,
            newItem: ScheduleItem
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ScheduleItem,
            newItem: ScheduleItem
        ): Boolean = oldItem == newItem
    }

}