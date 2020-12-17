package ru.mulledwine.shiftsredesigned.ui.vacations

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
import kotlinx.android.synthetic.main.item_vacation.*
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.VacationItem

class VacationsAdapter(
    private val longClickListener: (() -> Unit)? = null,
    private val clickListener: (item: VacationItem) -> Unit
) :
    ListAdapter<VacationItem, VacationsAdapter.ViewHolder>(DiffCallback()) {

    val selectedItems = mutableSetOf<VacationItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val containerView = inflater.inflate(R.layout.item_vacation, parent, false)
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

    private fun handleSelection(item: VacationItem, holder: ViewHolder) {
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

        fun bind(item: VacationItem) {
            tv_vacation_item_duration.text = item.duration
            tv_vacation_item_description.text = item.description
        }

        fun setSelection(isSelected: Boolean) {
            val color = if (isSelected) Constants.selectionColor else Color.WHITE
            containerView.backgroundTintList = ColorStateList.valueOf(color)
            iv_check.isVisible = isSelected
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<VacationItem>() {
        override fun areItemsTheSame(
            oldItem: VacationItem,
            newItem: VacationItem
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: VacationItem,
            newItem: VacationItem
        ): Boolean = oldItem == newItem
    }

}