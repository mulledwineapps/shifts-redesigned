package ru.mulledwine.shiftsredesigned.ui.statistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_statistics.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.StatisticItem

class StatisticsAdapter :
    ListAdapter<StatisticItem, StatisticsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val containerView = inflater.inflate(R.layout.item_statistics, parent, false)
        return ViewHolder(containerView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: StatisticItem) {
            tv_statistics_item_name.text = item.name
            tv_statistics_item_value.text = item.value
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<StatisticItem>() {
        override fun areItemsTheSame(
            oldItem: StatisticItem,
            newItem: StatisticItem
        ): Boolean = oldItem.name == newItem.name

        override fun areContentsTheSame(
            oldItem: StatisticItem,
            newItem: StatisticItem
        ): Boolean = oldItem == newItem
    }

}