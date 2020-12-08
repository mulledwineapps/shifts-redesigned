package ru.mulledwine.shiftsredesigned.ui.main

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_shift_on_main.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftOnMainItem

class ShiftsOnMainAdapter(
    private val clickListener: (item: ShiftOnMainItem) -> Unit,
    private val longClickListener: (item: ShiftOnMainItem) -> Unit
) :
    ListAdapter<ShiftOnMainItem, ShiftsOnMainAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val containerView = inflater.inflate(R.layout.item_shift_on_main, parent, false)
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

        fun bind(item: ShiftOnMainItem) {
            tv_main_item_name.text = item.scheduleName
            tv_main_item_description.text = item.description
            v_main_item_color.backgroundTintList = ColorStateList.valueOf(item.color)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ShiftOnMainItem>() {
        override fun areItemsTheSame(
            oldItem: ShiftOnMainItem,
            newItem: ShiftOnMainItem
        ): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ShiftOnMainItem,
            newItem: ShiftOnMainItem
        ): Boolean =
            oldItem == newItem
    }
}