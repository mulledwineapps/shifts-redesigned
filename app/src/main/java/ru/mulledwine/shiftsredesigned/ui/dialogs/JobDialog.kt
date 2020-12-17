package ru.mulledwine.shiftsredesigned.ui.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.extensions.getTrimmedString
import ru.mulledwine.shiftsredesigned.ui.RootActivity

class JobDialog : DialogFragment() {

    companion object {
        const val JOB_KEY = "ADD_JOB_KEY"
        const val JOB_ID = "JOB_ID"
        const val JOB_NAME = "JOB_ITEM"
    }

    private val args: JobDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_job, null, false)

        val etJobName = view.findViewById<EditText>(R.id.et_job_name)
        args.item?.name?.let {
            etJobName.append(it)
        }

        val titleRes = if (args.item == null) R.string.label_add_job else R.string.label_edit_job

        val dialog = AlertDialog.Builder(requireContext(), R.style.AlabasterAlertDialogTheme)
            .setView(view)
            .setTitle(getString(titleRes))
            .setPositiveButton(R.string.ok_button, null)
            .setNegativeButton(R.string.cancel_button, null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                setFragmentResult(
                    JOB_KEY,
                    bundleOf(
                        JOB_ID to (args.item?.id ?: -1),
                        JOB_NAME to etJobName.getTrimmedString()
                    )
                )
                (activity as RootActivity).viewModel.navigateUp()
            }
        }

        return dialog

    }
}