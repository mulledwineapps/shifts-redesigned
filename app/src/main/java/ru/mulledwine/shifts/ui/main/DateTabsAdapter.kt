package ru.mulledwine.shifts.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_date_tab.*
import ru.mulledwine.shifts.Constants
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.entities.Day
import ru.mulledwine.shifts.ui.custom.RecyclerTabLayout
import kotlin.math.roundToInt

class DateTabsAdapter(viewPager: ViewPager2) :
    RecyclerTabLayout.Adapter<Day>(DiffCallback(), viewPager) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerTabLayout.Adapter<Day>.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val containerView = inflater.inflate(R.layout.item_date_tab, parent, false)

        if (tabOnScreenLimit > 0) {
            val w = (parent.measuredWidth / tabOnScreenLimit.toFloat()).roundToInt()
            containerView.updateLayoutParams { width = w }
        }

        return ViewHolder(containerView)
    }

    override fun onBindViewHolder(
        holder: RecyclerTabLayout.Adapter<Day>.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.firstOrNull() == RENDER_SELECTION_PAYLOAD) {
            holder.renderSelection(position == currentIndicatorPosition)
        } else super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(
        holder: RecyclerTabLayout.Adapter<Day>.ViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.bind(item, position)
        holder.renderSelection(position == currentIndicatorPosition)
    }

    inner class ViewHolder(override val containerView: View) :
        RecyclerTabLayout.Adapter<Day>.ViewHolder(containerView), LayoutContainer {
        override fun bind(item: Day, position: Int) {
            tv_date_item_date.text = item.date.toString()
            tv_date_item_weekday.text = Constants.weekDays[item.numberInWeek]
        }

        override fun renderSelection(isSelected: Boolean) {
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