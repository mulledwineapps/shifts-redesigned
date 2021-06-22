package ru.mulledwine.shifts.ui.dialogs

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_color.*
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.models.ColorItem

class ColorsAdapter(
    private val clickListener: (item: ColorItem) -> Unit
) :
    ListAdapter<ColorItem, ColorsAdapter.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TAG = "M_ColorsAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val containerView = inflater.inflate(R.layout.item_color, parent, false)
        return ViewHolder(containerView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.containerView.setOnClickListener {
            clickListener.invoke(item)
        }
        holder.containerView.setOnLongClickListener {
            true
        }
        holder.bind(item)
    }

    class ViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: ColorItem) {
            v_color_item_circle.backgroundTintList = ColorStateList.valueOf(item.color)
            tv_color_item_name.text = item.name
            tv_color_item_hex.text = item.hex
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ColorItem>() {
        override fun areItemsTheSame(
            oldItem: ColorItem,
            newItem: ColorItem
        ): Boolean =
            oldItem.color == newItem.color

        override fun areContentsTheSame(
            oldItem: ColorItem,
            newItem: ColorItem
        ): Boolean =
            oldItem == newItem
    }

}