package com.dsige.lectura.dominion.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.dsige.lectura.dominion.ui.fragments.*

abstract class TabLayoutAdapter {

    class TabLayoutRecibo(fm: FragmentManager, private val numberOfTabs: Int, var repartoId: Int, var recibo: String, var operarioId: Int, var cliente: String, var validation: Int)
        : FragmentStatePagerAdapter(fm, numberOfTabs) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
//                0 -> GeneralFragment.newInstance(repartoId, recibo, operarioId, cliente, validation)
//                1 -> FirmFragment.newInstance(repartoId)
                else -> Fragment()
            }
        }

        override fun getCount(): Int {
            return numberOfTabs
        }
    }

    class TabLayoutClient(fm: FragmentManager, private val numberOfTabs: Int, val id: Int)
        : FragmentStatePagerAdapter(fm, numberOfTabs) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> GeneralClientFragment.newInstance(id)
                1 -> FileFragment.newInstance(id)
                else -> Fragment()
            }
        }

        override fun getCount(): Int {
            return numberOfTabs
        }
    }
}