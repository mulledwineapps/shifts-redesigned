package ru.mulledwine.shiftsredesigned.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_date_tab.*
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.entities.Day

class DateTabsAdapter(
    private val clickListener: (position: Int, holderLeft: Int) -> Unit
) :
    ListAdapter<Day, DateTabsAdapter.ViewHolder>(DiffCallback()) {

    var selectedItemId: String = Constants.todayId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val containerView = inflater.inflate(R.layout.item_date_tab, parent, false)
        return ViewHolder(containerView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.containerView.setOnClickListener {
            handleSelection(item, holder)
            val holderLeft = holder.containerView.left + holder.containerView.paddingLeft
            clickListener.invoke(position, holderLeft)
        }
        holder.bind(item)
        holder.setSelection(selectedItemId == item.id)
    }

    fun selectDay(dayId: String) {
        if (selectedItemId == dayId) return // already selected, do not remove selection

        val posOld = currentList.indexOfFirst { it.id == selectedItemId }
        selectedItemId = dayId
        // TODO use payload
        notifyItemChanged(posOld) // remove previous selection
        val posNew = currentList.indexOfFirst { it.id == dayId }
        notifyItemChanged(posNew) // set new selection
    }

    private fun handleSelection(item: Day, holder: ViewHolder) {

        if (selectedItemId == item.id) return // already selected, do not remove selection

        val pos = currentList.indexOfFirst { it.id == selectedItemId }
        selectedItemId = item.id
        // TODO use payload
        notifyItemChanged(pos) // remove previous selection
        holder.setSelection(true) // set new selection
    }

    inner class ViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: Day) {
            tv_date_item_date.text = item.date.toString()
            tv_date_item_weekday.text = Constants.weekDays[item.numberInWeek]
        }

        fun setSelection(isSelected: Boolean) {
            setOf(tv_date_item_date, tv_date_item_weekday).forEach {
                it.isSelected = isSelected
                it.typeface = if (isSelected)
                    Constants.typefaceMediumNormal else Constants.typefaceNormal
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Day>() {
        override fun areItemsTheSame(oldItem: Day, newItem: Day): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Day, newItem: Day): Boolean = oldItem == newItem
    }
}