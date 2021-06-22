package ru.mulledwine.shifts.ui.settings

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.DataHolder
import ru.mulledwine.shifts.data.local.models.SettingsItem
import ru.mulledwine.shifts.extensions.getDrawableCompat
import ru.mulledwine.shifts.ui.base.BaseFragment
import ru.mulledwine.shifts.ui.custom.DividerItemDecoration
import ru.mulledwine.shifts.viewmodels.SettingsViewModel

@AndroidEntryPoint
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
            getDrawableCompat(R.drawable.list_divider)?.let {
                addItemDecoration(DividerItemDecoration(it) { pos ->
                    settingsAdapter.getItemViewType(pos) == SettingsItem.VIEW_TYPE_ELEMENT
                })
            }
        }

    }

}