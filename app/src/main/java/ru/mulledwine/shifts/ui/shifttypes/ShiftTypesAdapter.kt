package ru.mulledwine.shifts.ui.shifttypes

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_shift_type.*
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.models.ShiftTypeListItem

class ShiftTypesAdapter(
    private val longClickListener: ((item: ShiftTypeListItem) -> Unit)? = null,
    private val clickListener: (item: ShiftTypeListItem) -> Unit
) :
    ListAdapter<ShiftTypeListItem, ShiftTypesAdapter.ViewHolder>(DiffCallback()) {

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

        fun bind(item: ShiftTypeListItem) {
            tv_shift_type_item_name.text = item.name
            tv_shift_type_item_duration.text = item.title
            v_shift_type_item_color.backgroundTintList = ColorStateList.valueOf(item.color)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ShiftTypeListItem>() {
        override fun areItemsTheSame(
            oldItem: ShiftTypeListItem,
            newItem: ShiftTypeListItem
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ShiftTypeListItem,
            newItem: ShiftTypeListItem
        ): Boolean = oldItem == newItem
    }

}