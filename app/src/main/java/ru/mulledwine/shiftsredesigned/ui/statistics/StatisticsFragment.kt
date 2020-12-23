package ru.mulledwine.shiftsredesigned.ui.statistics

import android.os.Bundle
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_statistics.*
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.models.JobItem
import ru.mulledwine.shiftsredesigned.data.local.models.Month
import ru.mulledwine.shiftsredesigned.data.local.models.Months
import ru.mulledwine.shiftsredesigned.data.local.models.StatisticItem
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.ui.base.Binding
import ru.mulledwine.shiftsredesigned.ui.delegates.RenderProp
import ru.mulledwine.shiftsredesigned.ui.dialogs.ChooseJobDialog
import ru.mulledwine.shiftsredesigned.ui.dialogs.ChooseMonthDialog
import ru.mulledwine.shiftsredesigned.viewmodels.StatisticsState
import ru.mulledwine.shiftsredesigned.viewmodels.StatisticsViewModel
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState
import ru.mulledwine.shiftsredesigned.viewmodels.base.ViewModelFactory

class StatisticsFragment : BaseFragment<StatisticsViewModel>() {

    private val args: StatisticsFragmentArgs by navArgs()
    private val statisticsAdapter = StatisticsAdapter()

    override val layout: Int = R.layout.fragment_statistics
    override val binding: StatisticsBinding by lazy { StatisticsBinding() }
    override val viewModel: StatisticsViewModel by viewModels {
        ViewModelFactory(
            owner = this,
            params = args.item
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // listen for job pick
        setFragmentResultListener(ChooseJobDialog.CHOOSE_JOB_KEY) { _, bundle ->
            val job = bundle[ChooseJobDialog.SELECTED_JOB] as JobItem
            viewModel.handleUpdateJob(job)
        }

        // listen for month pick
        setFragmentResultListener(ChooseMonthDialog.CHOOSE_MONTH_KEY) { _, bundle ->
            val month = bundle[ChooseMonthDialog.SELECTED_MONTH] as Month
            viewModel.handleUpdateMonth(month)
        }

    }

    override fun setupViews() {

        with(rv_statistics) {
            adapter = statisticsAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        tv_statistics_job.setOnClickListener { navigateToDialogChooseJob() }

        btn_previous_month.setOnClickListener {
            viewModel.handleSetPreviousMonth()
        }

        btn_next_month.setOnClickListener {
            viewModel.handleSetNextMonth()
        }

        viewModel.observeJobs(viewLifecycleOwner) {
            binding.jobItems = it
        }
    }

    private fun navigateToDialogChooseJob() {
        val action = StatisticsFragmentDirections.actionToDialogChooseJob(
            binding.jobItems.toTypedArray()
        )
        viewModel.navigateWithAction(action)
    }

    inner class StatisticsBinding : Binding() {

        var jobItems: List<JobItem> = emptyList()

        private var jobName by RenderProp(args.item.jobItem.name) {
            tv_statistics_job.text = it
        }

        private var month by RenderProp(args.item.month) {
            val monthName = Months.values()[it.number].getName(requireContext())
            tv_month_name.text = getString(R.string.month_year, monthName, it.year.toString())
        }

        private var statisticItems: List<StatisticItem> by RenderProp(args.item.statisticItems) {
            statisticsAdapter.submitList(it)
        }

        override fun bind(data: IViewModelState) {
            data as StatisticsState
            jobName = data.jobName
            month = data.month
            statisticItems = data.statisticItems
        }

    }

}