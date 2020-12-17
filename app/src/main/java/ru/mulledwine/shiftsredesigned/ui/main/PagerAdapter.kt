package ru.mulledwine.shiftsredesigned.ui.main

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.mulledwine.shiftsredesigned.data.local.entities.Day

class PagerAdapter(fragment: MainFragment) : FragmentStateAdapter(fragment) {

    var items: List<Day> = listOf()

    companion object {
        const val ARG_DAY = "ARG_DAY"
    }

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment {
        return PagerFragment().apply {
            arguments = bundleOf(ARG_DAY to items[position])
        }
    }

}