package ru.mulledwine.shifts.ui.statistics

import android.os.Bundle
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlinx.android.synthetic.main.layout_month_choosing.*
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.models.JobItem
import ru.mulledwine.shifts.data.local.models.Month
import ru.mulledwine.shifts.data.local.models.Months
import ru.mulledwine.shifts.data.local.models.StatisticItem
import ru.mulledwine.shifts.extensions.getDrawableCompat
import ru.mulledwine.shifts.ui.base.BaseFragment
import ru.mulledwine.shifts.ui.base.Binding
import ru.mulledwine.shifts.ui.custom.DividerItemDecoration
import ru.mulledwine.shifts.ui.delegates.RenderProp
import ru.mulledwine.shifts.ui.dialogs.ChooseJobDialog
import ru.mulledwine.shifts.ui.dialogs.ChooseMonthDialog
import ru.mulledwine.shifts.viewmodels.StatisticsState
import ru.mulledwine.shifts.viewmodels.StatisticsViewModel
import ru.mulledwine.shifts.viewmodels.base.IViewModelState

@AndroidEntryPoint
class StatisticsFragment : BaseFragment<StatisticsViewModel>() {

    private val args: StatisticsFragmentArgs by navArgs()
    private val statisticsAdapter = StatisticsAdapter()

    override val layout: Int = R.layout.fragment_statistics
    override val binding: StatisticsBinding by lazy { StatisticsBinding() }
    override val viewModel: StatisticsViewModel by viewModels()

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
            getDrawableCompat(R.drawable.list_divider)?.let {
                addItemDecoration(DividerItemDecoration(it) { pos ->
                    if (pos > statisticsAdapter.itemCount.dec()) false else
                        statisticsAdapter.getItemViewType(pos) == StatisticItem.VIEW_TYPE_ELEMENT
                })
            }
        }

        tv_statistics_job.setOnClickListener { navigateToDialogChooseJob() }
        tv_month_name.setOnClickListener { navigateToDialogChooseMonth() }

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

    private fun navigateToDialogChooseMonth() {
        val action = StatisticsFragmentDirections
            .actionNavStatisticsToDialogChooseMonth(binding.month)
        viewModel.navigateWithAction(action)
    }

    inner class StatisticsBinding : Binding() {

        var jobItems: List<JobItem> = emptyList()

        var month by RenderProp(args.item.month) {
            val monthName = Months.values()[it.number].getName(requireContext())
            tv_month_name.text = getString(R.string.month_year, monthName, it.year.toString())
        }

        private var jobName by RenderProp(args.item.jobItem.name) {
            tv_statistics_job.text = it
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