package ru.mulledwine.shiftsredesigned.ui.main

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
import kotlinx.android.synthetic.main.item_shift_on_main.*
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftOnMainItem

class MainAdapter(
    private val clickListener: (item: ShiftOnMainItem) -> Unit,
    private val longClickListener: () -> Unit,
) :
    ListAdapter<ShiftOnMainItem, MainAdapter.ViewHolder>(DiffCallback()) {

    val selectedItems = mutableSetOf<ShiftOnMainItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val containerView = inflater.inflate(R.layout.item_shift_on_main, parent, false)
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
            longClickListener.invoke()
            true
        }
        holder.bind(item)
        holder.setSelection(selectedItems.contains(item), item.shiftId != null)
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    private fun handleSelection(item: ShiftOnMainItem, holder: ViewHolder) {
        if (selectedItems.contains(item)) {
            selectedItems.remove(item)
            holder.setSelection(false, item.shiftId != null)
        } else {
            selectedItems.add(item)
            holder.setSelection(true, item.shiftId != null)
        }
    }

    class ViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: ShiftOnMainItem) {
            tv_main_item_name.text = item.jobName

            tv_main_item_description.text = when {
                item.shiftDetails != null -> item.shiftDetails
                item.scheduleId == null -> "График не задан"
                item.shiftId == null -> "Смена не задана"
                else -> ""
            }

            if (item.color == null) {
                v_main_item_colored_dot.isVisible = false
                iv_main_item_check.imageTintList = ColorStateList.valueOf(Constants.brightGray54)
            } else {
                v_main_item_colored_dot.isVisible = true
                v_main_item_colored_dot.backgroundTintList = ColorStateList.valueOf(item.color)
                iv_main_item_check.imageTintList = ColorStateList.valueOf(item.color)
            }
            iv_main_item_vacation.isVisible = item.vacationId != null
        }

        fun setSelection(isSelected: Boolean, isThereShift: Boolean) {
            val color = if (isSelected) Constants.selectionColor else Color.WHITE
            containerView.backgroundTintList = ColorStateList.valueOf(color)
            iv_main_item_check.isVisible = isSelected
            setDotVisibility(isVisible = !isSelected && isThereShift)
        }

        private fun setDotVisibility(isVisible: Boolean) {
            v_main_item_colored_dot.isVisible = isVisible
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ShiftOnMainItem>() {
        override fun areItemsTheSame(
            oldItem: ShiftOnMainItem,
            newItem: ShiftOnMainItem
        ): Boolean = oldItem.shiftId == newItem.shiftId

        override fun areContentsTheSame(
            oldItem: ShiftOnMainItem,
            newItem: ShiftOnMainItem
        ): Boolean = oldItem == newItem
    }
}