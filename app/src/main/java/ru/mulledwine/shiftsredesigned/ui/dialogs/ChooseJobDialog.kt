package ru.mulledwine.shiftsredesigned.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.ui.jobs.JobsAdapter

class ChooseJobDialog : DialogFragment() {

    companion object {
        const val CHOOSE_JOB_KEY = "CHOOSE_JOB_KEY"
        const val SELECTED_JOB = "SELECTED_JOB"
    }

    private val args: ChooseJobDialogArgs by navArgs()

    private val jobsAdapter = JobsAdapter {
        setFragmentResult(CHOOSE_JOB_KEY, bundleOf(SELECTED_JOB to it))
        dialog!!.dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        jobsAdapter.submitList(args.items.toList())

        val rvJobs = RecyclerView(requireContext()).apply {
            adapter = jobsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            setPadding(Constants.dp8, Constants.dp16, Constants.dp8, Constants.dp16)
        }

        return AlertDialog.Builder(requireContext(), R.style.AlabasterAlertDialogTheme)
            .setView(rvJobs)
            .setTitle(R.string.label_choose_job)
            .create()

    }
}