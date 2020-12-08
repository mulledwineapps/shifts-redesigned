package ru.mulledwine.shiftsredesigned.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_date.*
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.entities.Day

class DatesAdapter(
    private val selectionChangeListener: (holderLeft: Int) -> Unit,
    private val clickListener: (item: Day, position: Int) -> Unit
) :
    ListAdapter<Day, DatesAdapter.DateViewHolder>(DatesDiffCallback()) {

    companion object {
        private const val TAG = "M_DateAdapter"
        const val SELECTION_CHANGED_MARKER = "SELECTION_CHANGED_MARKER"
    }

    private var previouslySelectedItemId: String? = null
    var selectedItemId: String? = null
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val containerView = inflater.inflate(R.layout.item_date, parent, false)
        return DateViewHolder(containerView)
    }

    override fun onBindViewHolder(
        holder: DateViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        if (payloads.contains(SELECTION_CHANGED_MARKER)) {
            val item = getItem(position)
            when (item.id) {
                previouslySelectedItemId -> holder.renderSelection(false)
                selectedItemId -> holder.renderSelection(true)
            }
        }
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val item = getItem(position)
        holder.containerView.setOnClickListener {
            clickListener.invoke(item, holder.adapterPosition)
        }
        holder.bind(item)
        holder.renderSelection(item.id == selectedItemId)
    }

    fun selectItem(itemId: String, position: Int? = null) {

        if (selectedItemId == itemId) return // already selected, do not remove selection

        previouslySelectedItemId = selectedItemId
        selectedItemId = itemId

        val posOld = currentList.indexOfFirst { it.id == previouslySelectedItemId }
        notifyItemChanged(posOld, SELECTION_CHANGED_MARKER)

        val pos = position ?: currentList.indexOfFirst { it.id == selectedItemId }
        notifyItemChanged(pos, SELECTION_CHANGED_MARKER)
    }

    inner class DateViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: Day) {
            tv_date_item_date.text = item.date.toString()
            tv_date_item_weekday.text = Constants.weekDays[item.numberInWeek]
        }

        fun renderSelection(isSelected: Boolean) {

            if (isSelected) selectionChangeListener(
                containerView.left + containerView.paddingLeft
            )

            setOf(tv_date_item_date, tv_date_item_weekday).forEach {
                it.isSelected = isSelected
                it.typeface = if (isSelected)
                    Constants.typefaceMediumNormal else Constants.typefaceNormal
            }
        }

    }
}

private class DatesDiffCallback : DiffUtil.ItemCallback<Day>() {
    override fun areItemsTheSame(oldItem: Day, newItem: Day): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Day, newItem: Day): Boolean = oldItem == newItem
}