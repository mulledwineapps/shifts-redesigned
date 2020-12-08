package ru.mulledwine.shiftsredesigned.ui.shifttypes

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_shift_type.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftTypeItem

class ShiftTypesAdapter(
    private val longClickListener: ((item: ShiftTypeItem) -> Unit)? = null,
    private val clickListener: (item: ShiftTypeItem) -> Unit
) :
    ListAdapter<ShiftTypeItem, ShiftTypesAdapter.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TAG = "M_ShiftTypesAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val containerView = inflater.inflate(R.layout.item_shift_type, parent, false)
        return ViewHolder(containerView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.containerView.setOnClickListener {
            clickListener.invoke(item)
        }
        holder.containerView.setOnLongClickListener {
            longClickListener?.invoke(item)
            true
        }
        holder.bind(item)
    }

    class ViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: ShiftTypeItem) {
            tv_shift_type_item_name.text = item.name
            tv_shift_type_item_duration.text = item.duration
            v_shift_type_item_color.backgroundTintList = ColorStateList.valueOf(item.color)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ShiftTypeItem>() {
        override fun areItemsTheSame(
            oldItem: ShiftTypeItem,
            newItem: ShiftTypeItem
        ): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ShiftTypeItem,
            newItem: ShiftTypeItem
        ): Boolean =
            oldItem == newItem
    }

}