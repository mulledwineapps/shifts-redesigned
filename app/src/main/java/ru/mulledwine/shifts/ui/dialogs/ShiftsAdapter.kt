package ru.mulledwine.shifts.ui.dialogs

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_shift.*
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.models.ShiftItem

class ShiftsAdapter(
    private val clickListener: (item: ShiftItem) -> Unit
) :
    ListAdapter<ShiftItem, ShiftsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val containerView = inflater.inflate(R.layout.item_shift, parent, false)
        return ViewHolder(containerView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.containerView.setOnClickListener {
            clickListener.invoke(item)
        }
        holder.bind(item)
    }

    class ViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: ShiftItem) {
            tv_shift_item_ordinal.text = item.ordinal.toString()
            tv_shift_item_name.text = item.typeName
            tv_shift_item_duration.text = item.duration
            v_shift_item_color.backgroundTintList = ColorStateList.valueOf(item.color)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ShiftItem>() {
        override fun areItemsTheSame(
            oldItem: ShiftItem,
            newItem: ShiftItem
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ShiftItem,
            newItem: ShiftItem
        ): Boolean = oldItem == newItem
    }

}