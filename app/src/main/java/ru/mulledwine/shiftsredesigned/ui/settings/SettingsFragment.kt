package ru.mulledwine.shiftsredesigned.ui.settings

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.DataHolder
import ru.mulledwine.shiftsredesigned.data.local.models.SettingsItem
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.viewmodels.SettingsViewModel

class SettingsFragment : BaseFragment<SettingsViewModel>() {

    override val viewModel: SettingsViewModel by viewModels()
    override val layout: Int = R.layout.fragment_settings

    private val settingsAdapter by lazy {
        SettingsAdapter {
            if (it is SettingsItem.Group) return@SettingsAdapter
            it as SettingsItem.Element
            when (it.id) {
                else -> Unit
            }
        }.apply {
            submitList(DataHolder.getSettings(requireContext()))
        }
    }

    override fun setupViews() {

        with(rv_settings) {
            adapter = settingsAdapter
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(
                ContextCompat.getDrawable(requireContext(), R.drawable.list_divider)!!
            ) {
                settingsAdapter.getItemViewType(it) == SettingsItem.VIEW_TYPE_ELEMENT
            })
        }

    }

}