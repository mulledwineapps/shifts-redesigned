package ru.mulledwine.shiftsredesigned.ui.schedules

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_schedule.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleItem

class SchedulesAdapter(
    private val longClickListener: ((item: ScheduleItem) -> Unit)? = null,
    private val clickListener: (item: ScheduleItem) -> Unit
) :
    ListAdapter<ScheduleItem, SchedulesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val containerView = inflater.inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(containerView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.containerView.setOnClickListener {
            clickListener.invoke(item)
        }
        holder.containerView.setOnLongClickListener {
            longClickListener?.invoke(item)
            true
        }
        holder.bind(item)
    }

    class ViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: ScheduleItem) {
            tv_schedule_item_name.text = item.name
            tv_schedule_item_duration.text = item.duration
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ScheduleItem>() {
        override fun areItemsTheSame(
            oldItem: ScheduleItem,
            newItem: ScheduleItem
        ): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ScheduleItem,
            newItem: ScheduleItem
        ): Boolean =
            oldItem == newItem
    }

}