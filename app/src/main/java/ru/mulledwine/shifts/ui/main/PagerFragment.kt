package ru.mulledwine.shifts.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_page.*
import ru.mulledwine.shifts.R

class PagerFragment : Fragment() {

    private val main: MainFragment
        get() = parentFragment as MainFragment
    private val layout: Int = R.layout.fragment_page

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(rv_pager) {
            adapter = main.shiftsAdapter
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            setRecycledViewPool(main.pool)
        }
        super.onViewCreated(view, savedInstanceState)
    }

}