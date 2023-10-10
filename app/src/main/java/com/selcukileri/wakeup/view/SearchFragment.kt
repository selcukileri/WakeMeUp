package com.selcukileri.wakeup.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.room.Room
import com.selcukileri.wakeup.R
import com.selcukileri.wakeup.databinding.FragmentSearchBinding
import com.selcukileri.wakeup.roomdb.HistoryDatabase
import com.selcukileri.wakeup.roomdb.SearchHistoryDao

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var db: HistoryDatabase
    private lateinit var searchHistoryDao : SearchHistoryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Room.databaseBuilder(requireContext(), HistoryDatabase::class.java,"History").build()
        searchHistoryDao = db.searchHistoryDao()

    }
    fun delete() {

    }
    fun save() {

    }
}