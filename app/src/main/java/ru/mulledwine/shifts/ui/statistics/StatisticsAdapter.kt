package ru.mulledwine.shifts.ui.statistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_statistics_element.*
import kotlinx.android.synthetic.main.item_statistics_group.*
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.models.StatisticItem

class StatisticsAdapter : ListAdapter<StatisticItem, StatisticsAdapter.ViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int = getItem(position).viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            StatisticItem.VIEW_TYPE_ELEMENT -> ElementViewHolder(
                inflater.inflate(R.layout.item_statistics_element, parent, false)
            )
            StatisticItem.VIEW_TYPE_GROUP -> GroupViewHolder(
                inflater.inflate(R.layout.item_statistics_group, parent, false)
            )
            else -> null
        }!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    abstract class ViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        abstract fun bind(item: StatisticItem)
    }

    class ElementViewHolder(containerView: View) : ViewHolder(containerView) {
        override fun bind(item: StatisticItem) {
            item as StatisticItem.Element
            tv_statistics_item_name.text = item.title
            tv_statistics_item_value.text = item.value
        }
    }

    class GroupViewHolder(containerView: View) : ViewHolder(containerView) {
        override fun bind(item: StatisticItem) {
            item as StatisticItem.Group
            tv_statistics_group_title.text = item.title
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<StatisticItem>() {
        override fun areItemsTheSame(
            oldItem: StatisticItem,
            newItem: StatisticItem
        ): Boolean = oldItem.getId() == newItem.getId()

        override fun areContentsTheSame(
            oldItem: StatisticItem,
            newItem: StatisticItem
        ): Boolean = oldItem == newItem
    }

}