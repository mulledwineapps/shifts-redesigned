package ru.mulledwine.shiftsredesigned.ui.shifttype

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_shift_type.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftTime
import ru.mulledwine.shiftsredesigned.extensions.getTrimmedString
import ru.mulledwine.shiftsredesigned.extensions.hideKeyboard
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.ui.base.Binding
import ru.mulledwine.shiftsredesigned.ui.delegates.RenderProp
import ru.mulledwine.shiftsredesigned.ui.dialogs.ColorPickerDialog
import ru.mulledwine.shiftsredesigned.ui.dialogs.TimePickerDialog
import ru.mulledwine.shiftsredesigned.utils.Utils
import ru.mulledwine.shiftsredesigned.viewmodels.ShiftTypeViewModel

class ShiftTypeFragment : BaseFragment<ShiftTypeViewModel>() {

    companion object {
        private const val TAG = "M_ShiftTypeFragment"
    }

    private val args: ShiftTypeFragmentArgs by navArgs()

    override val layout: Int = R.layout.fragment_shift_type
    override val viewModel: ShiftTypeViewModel by viewModels()
    override val binding: ShiftTypeBinding = ShiftTypeBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menu_item_save).setOnMenuItemClickListener {
            val item = ShiftType(
                id = args.item?.id,
                name = et_shift_type_name.getTrimmedString(),
                color = btn_shift_type_choose_color.backgroundTintList!!.defaultColor,
                isFullDay = btn_shift_type_full_day.isChecked,
                start = binding.start,
                finish = binding.finish
            )
            viewModel.handleClickSave(item)
            true
        }
    }

    override fun setupViews() {

        args.item?.let {
            et_shift_type_name.setText(it.name)
            if (it.isFullDay) btn_shift_type_full_day.isChecked = true
            else btn_shift_type_choose_hours.isChecked = true
            btn_shift_type_choose_color.backgroundTintList = ColorStateList.valueOf(it.color)

            binding.start = it.start
            binding.finish = it.finish
            binding.isFullDay = it.isFullDay
        } ?: run {
            btn_shift_type_choose_hours.isChecked = true
            btn_shift_type_choose_color.backgroundTintList =
                ColorStateList.valueOf(Utils.getRandomColor())
            binding.isFullDay = false
        }

        tv_shift_type_start.setOnClickListener {
            clearNameFieldFocus()
            navigateToTimePicker(binding.start, it.id)
        }

        tv_shift_type_finish.setOnClickListener {
            clearNameFieldFocus()
            navigateToTimePicker(binding.finish, it.id)
        }

        radio_group_shift_type_duration.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.btn_shift_type_full_day -> binding.isFullDay = true
                R.id.btn_shift_type_choose_hours -> binding.isFullDay = false
            }
        }

        btn_shift_type_choose_color.setOnClickListener {
            clearNameFieldFocus()
            val action = ShiftTypeFragmentDirections
                .actionNavShiftTypeToDialogColorPicker(R.string.label_choose_shift_color)
            viewModel.navigateWithAction(action)
        }

        // listen for time pick
        setFragmentResultListener(TimePickerDialog.TIME_PICKER_KEY) { _, bundle ->
            val shiftTime = bundle[TimePickerDialog.PICK_ACTION_KEY] as ShiftTime
            when (bundle.getInt(TimePickerDialog.VIEW_KEY)) {
                tv_shift_type_start.id -> binding.start = shiftTime
                tv_shift_type_finish.id -> binding.finish = shiftTime
            }
        }

        // listen for color pick
        setFragmentResultListener(ColorPickerDialog.COLOR_PICKER_KEY) { _, bundle ->
            val color = bundle.getInt(ColorPickerDialog.PICK_ACTION_KEY)
            btn_shift_type_choose_color.backgroundTintList = ColorStateList.valueOf(color)
        }

    }

    private fun clearNameFieldFocus() {
        with(et_shift_type_name) {
            if (hasFocus()) clearFocus()
            requireContext().hideKeyboard(this)
        }
    }

    inner class ShiftTypeBinding : Binding() {

        var start: ShiftTime by RenderProp(ShiftTime()) {
            tv_shift_type_start.text = it.toString()
        }

        var finish: ShiftTime by RenderProp(ShiftTime()) {
            tv_shift_type_finish.text = it.toString()
        }

        var isFullDay: Boolean by RenderProp(true) {
            tv_shift_type_duration_title.isEnabled = !it
            tv_shift_type_start.isEnabled = !it
            tv_shift_type_finish.isEnabled = !it
            if (it) {
                start = ShiftTime()
                finish = ShiftTime()
            }
        }

    }

}