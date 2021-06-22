package ru.mulledwine.shifts.ui.jobs

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
import kotlinx.android.synthetic.main.item_job.*
import ru.mulledwine.shifts.Constants
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.models.JobItem

class JobsAdapter(
    private val longClickListener: (() -> Unit)? = null,
    private val clickListener: (item: JobItem) -> Unit
) :
    ListAdapter<JobItem, JobsAdapter.ViewHolder>(DiffCallback()) {

    var isSelectionAllowed = false
    val selectedItems = mutableSetOf<JobItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val containerView = inflater.inflate(R.layout.item_job, parent, false)
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

    private fun handleSelection(item: JobItem, holder: ViewHolder) {
        if (isSelectionAllowed) {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item)
                holder.setSelection(false)
            } else {
                selectedItems.add(item)
                holder.setSelection(true)
            }
        }
    }

    class ViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: JobItem) {
            tv_job_item_name.text = item.name
        }

        fun setSelection(isSelected: Boolean) {
            val color = if (isSelected) Constants.selectionColor else Color.WHITE
            containerView.backgroundTintList = ColorStateList.valueOf(color)
            iv_check.isVisible = isSelected
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<JobItem>() {
        override fun areItemsTheSame(oldItem: JobItem, newItem: JobItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: JobItem, newItem: JobItem): Boolean =
            oldItem == newItem
    }

}