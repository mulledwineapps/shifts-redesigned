package ru.mulledwine.shiftsredesigned.ui.schedule

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_shift.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleShiftItem

class ShiftsAdapter(
    private val clickListener: (item: ScheduleShiftItem) -> Unit,
    private val longClickListener: (item: ScheduleShiftItem) -> Unit
) :
    ListAdapter<ScheduleShiftItem, ShiftsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val containerView = inflater.inflate(R.layout.item_shift, parent, false)
        return ViewHolder(containerView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.containerView.setOnClickListener {
            clickListener.invoke(item)
        }
        holder.containerView.setOnLongClickListener {
            longClickListener.invoke(item)
            true
        }
        holder.bind(item)
    }

    class ViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: ScheduleShiftItem) {
            tv_shift_item_ordinal.text = item.ordinal.toString()
            tv_shift_item_name.text = item.typeName
            tv_shift_item_duration.text = item.title
            v_shift_item_color.backgroundTintList = ColorStateList.valueOf(item.color)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ScheduleShiftItem>() {
        override fun areItemsTheSame(
            oldItem: ScheduleShiftItem,
            newItem: ScheduleShiftItem
        ): Boolean =
            oldItem.shiftId == newItem.shiftId

        override fun areContentsTheSame(
            oldItem: ScheduleShiftItem,
            newItem: ScheduleShiftItem
        ): Boolean =
            oldItem == newItem
    }

}