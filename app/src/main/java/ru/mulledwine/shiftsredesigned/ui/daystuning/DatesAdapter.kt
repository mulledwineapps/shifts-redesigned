package ru.mulledwine.shiftsredesigned.ui.daystuning

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.extensions.dpToIntPx

class DatesAdapter : ListAdapter<CellItem, DatesAdapter.ViewHolder>(DiffCallback()) {

    val selectedDates = mutableSetOf<String>() // TODO check 29-31 when month change

    override fun getItemViewType(position: Int): Int = getItem(position).viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            CellItem.VIEW_TYPE_DATE -> DateViewHolder(
                inflater.inflate(R.layout.item_date, parent, false)
            )
            CellItem.VIEW_TYPE_EMPTY -> EmptyViewHolder(
                with(parent.context) {
                    View(this).apply {
                        layoutParams = ViewGroup.LayoutParams(dpToIntPx(36), dpToIntPx(36))
                    }
                }
            )
            else -> null
        }!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.containerView.setOnClickListener {
            if (item !is CellItem.DateItem) return@setOnClickListener
            handleSelection(item, holder as DateViewHolder)
        }
        if (holder is DateViewHolder) {
            holder.setSelection(selectedDates.contains((item as CellItem.DateItem).date))
        }
        holder.bind(item)
    }

    private fun handleSelection(item: CellItem.DateItem, holder: DateViewHolder) {
        if (selectedDates.contains(item.date)) {
            selectedDates.remove(item.date)
            holder.setSelection(false)
        } else {
            selectedDates.add(item.date)
            holder.setSelection(true)
        }
    }

    abstract class ViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        abstract fun bind(item: CellItem)
    }

    class DateViewHolder(containerView: View) : ViewHolder(containerView) {

        override fun bind(item: CellItem) {
            item as CellItem.DateItem
            (containerView as TextView).text = item.date
        }

        fun setSelection(isSelected: Boolean) {
            val color = if (isSelected) Constants.brightGray54 else Color.TRANSPARENT
            val textColor = if (isSelected) Color.WHITE else Constants.brightGray
            containerView as TextView
            containerView.backgroundTintList = ColorStateList.valueOf(color)
            containerView.setTextColor(textColor)
        }
    }

    class EmptyViewHolder(containerView: View) : ViewHolder(containerView) {
        override fun bind(item: CellItem) {}
    }

    class DiffCallback : DiffUtil.ItemCallback<CellItem>() {
        override fun areItemsTheSame(
            oldItem: CellItem,
            newItem: CellItem
        ): Boolean = oldItem.getId() == newItem.getId()

        override fun areContentsTheSame(
            oldItem: CellItem,
            newItem: CellItem
        ): Boolean = oldItem == newItem
    }

}

sealed class CellItem {
    companion object {
        const val VIEW_TYPE_EMPTY = 0
        const val VIEW_TYPE_DATE = 1
    }

    abstract val viewType: Int
    abstract fun getId(): String

    data class EmptyCell(
        val id: Int
    ) : CellItem() {
        override val viewType: Int = VIEW_TYPE_EMPTY
        override fun getId(): String = id.toString()
    }

    data class DateItem(
        val date: String
    ) : CellItem() {
        override val viewType: Int = VIEW_TYPE_DATE
        override fun getId(): String = date
    }

}