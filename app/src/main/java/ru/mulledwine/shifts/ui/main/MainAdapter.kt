package ru.mulledwine.shifts.ui.main

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_main_element.*
import kotlinx.android.synthetic.main.item_main_hint.*
import ru.mulledwine.shifts.Constants
import ru.mulledwine.shifts.R

class MainAdapter(
    private val clickListener: (item: MainItem.Element) -> Unit,
    private val longClickListener: () -> Unit,
    private val hintCloseListener: (id: String) -> Unit
) :
    ListAdapter<MainItem, MainAdapter.ViewHolder>(DiffCallback()) {

    val selectedItems = mutableSetOf<MainItem.Element>()

    override fun getItemViewType(position: Int): Int = getItem(position).viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MainItem.VIEW_TYPE_ELEMENT -> ElementViewHolder(
                inflater.inflate(R.layout.item_main_element, parent, false)
            )
            MainItem.VIEW_TYPE_HINT -> HintViewHolder(
                inflater.inflate(R.layout.item_main_hint, parent, false)
            )
            else -> null
        }!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        when (item) {
            is MainItem.Element -> {
                holder as ElementViewHolder

                holder.containerView.setOnClickListener {
                    if (selectedItems.isNotEmpty()) handleSelection(item, holder)
                    clickListener.invoke(item)
                }
                holder.containerView.setOnLongClickListener {
                    handleSelection(item, holder)
                    longClickListener.invoke()
                    true
                }

                holder.setSelection(selectedItems.contains(item), item.shiftId != null)
            }
            is MainItem.Hint -> {
                holder as HintViewHolder
                holder.setOnCloseListener(item, hintCloseListener)
            }
        }

        holder.bind(item)
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    private fun handleSelection(item: MainItem.Element, holder: ElementViewHolder) {
        if (selectedItems.contains(item)) {
            selectedItems.remove(item)
            holder.setSelection(false, item.shiftId != null)
        } else {
            selectedItems.add(item)
            holder.setSelection(true, item.shiftId != null)
        }
    }

    abstract class ViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        abstract fun bind(item: MainItem)
    }

    class HintViewHolder(
        override val containerView: View
    ) : MainAdapter.ViewHolder(containerView), LayoutContainer {
        override fun bind(item: MainItem) {
            item as MainItem.Hint
            tv_main_hint_text.text = item.text
        }

        fun setOnCloseListener(item: MainItem.Hint, listener: (String) -> Unit) {
            iv_main_hint_close.setOnClickListener {
                listener.invoke(item.getId())
            }
        }
    }

    class ElementViewHolder(
        override val containerView: View
    ) : MainAdapter.ViewHolder(containerView), LayoutContainer {

        override fun bind(item: MainItem) {
            item as MainItem.Element

            tv_main_item_title.text = item.jobName

            tv_main_item_description.text = when {
                item.shiftDetails != null -> item.shiftDetails
                item.scheduleId == null -> "График не задан"
                item.shiftId == null -> "Смена не задана"
                else -> ""
            }

            if (item.color == null) {
                v_main_item_colored_dot.isVisible = false
                iv_main_item_check.imageTintList =
                    ColorStateList.valueOf(Constants.brightGray54)
            } else {
                v_main_item_colored_dot.isVisible = true
                v_main_item_colored_dot.backgroundTintList =
                    ColorStateList.valueOf(item.color)
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
}

class DiffCallback : DiffUtil.ItemCallback<MainItem>() {
    override fun areItemsTheSame(oldItem: MainItem, newItem: MainItem): Boolean =
        oldItem.getId() == newItem.getId()

    override fun areContentsTheSame(oldItem: MainItem, newItem: MainItem): Boolean =
        oldItem == newItem
}

sealed class MainItem {

    companion object {
        const val VIEW_TYPE_ELEMENT = 0
        const val VIEW_TYPE_HINT = 1
    }

    abstract val viewType: Int
    abstract fun getId(): String

    data class Element(
        val jobId: Int,
        val jobName: String,
        val scheduleId: Int? = null,
        val scheduleTitle: String? = null,
        val shiftId: Int? = null,
        val shiftDetails: String? = null, // тип смены и её продолжительность
        val color: Int? = null,
        val vacationId: Int? = null,
        val vacationTitle: String? = null
    ) : MainItem() {
        override val viewType: Int = VIEW_TYPE_ELEMENT
        override fun getId(): String = shiftId.toString()
    }

    data class Hint(
        val text: String
    ) : MainItem() {
        override val viewType: Int = VIEW_TYPE_HINT
        override fun getId(): String = text
    }
}

data class HintItemRes(
    val id: Int,
    @StringRes val textRes: Int
) {
    fun toHintItem(context: Context): MainItem.Hint {
        return MainItem.Hint(
            text = context.getString(textRes)
        )
    }
}