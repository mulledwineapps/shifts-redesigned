package ru.mulledwine.shifts.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_settings_element.*
import kotlinx.android.synthetic.main.item_settings_group.*
import ru.mulledwine.shifts.Constants
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.models.SettingsItem

class SettingsAdapter(
    private val clickListener: (item: SettingsItem) -> Unit
) :
    ListAdapter<SettingsItem, SettingsAdapter.ViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int = getItem(position).viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            SettingsItem.VIEW_TYPE_ELEMENT -> ElementViewHolder(
                inflater.inflate(R.layout.item_settings_element, parent, false)
            )
            SettingsItem.VIEW_TYPE_GROUP -> GroupViewHolder(
                inflater.inflate(R.layout.item_settings_group, parent, false)
            )
            else -> null
        }!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        if (item is SettingsItem.Element && item.hasSwitch) {
            holder.switch_settings.setOnClickListener {
                clickListener.invoke(item)
            }
        } else {
            holder.containerView.setOnClickListener {
                clickListener.invoke(item)
            }
        }

        holder.bind(item)
    }

    abstract class ViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        abstract fun bind(item: SettingsItem)
    }

    class ElementViewHolder(containerView: View) : ViewHolder(containerView) {
        override fun bind(item: SettingsItem) {
            item as SettingsItem.Element
            tv_settings_element_title.text = item.title
            tv_settings_description.text = item.description
            switch_settings.isVisible = item.hasSwitch
            switch_settings.isChecked = item.isSwitchChecked
        }
    }

    class GroupViewHolder(containerView: View) : ViewHolder(containerView) {
        override fun bind(item: SettingsItem) {
            if (bindingAdapterPosition == 0) containerView.updateLayoutParams { height = Constants.dp40 }
            item as SettingsItem.Group
            tv_settings_group_title.text = item.title
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SettingsItem>() {
        override fun areItemsTheSame(
            oldItem: SettingsItem,
            newItem: SettingsItem
        ): Boolean = oldItem.getId() == newItem.getId()

        override fun areContentsTheSame(
            oldItem: SettingsItem,
            newItem: SettingsItem
        ): Boolean = oldItem == newItem
    }

}