package com.selcukileri.wakeup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.Place
import com.selcukileri.wakeup.databinding.FragmentSearchBinding
import com.selcukileri.wakeup.databinding.RecyclerSearchBinding
import com.selcukileri.wakeup.model.SearchHistory

class SearchAdapter(val searchList: List<SearchHistory>) : RecyclerView.Adapter<SearchAdapter.SearchHolder>() {
    class SearchHolder(val recyclerSearchBinding: RecyclerSearchBinding) : RecyclerView.ViewHolder(recyclerSearchBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val recyclerSearchBinding = RecyclerSearchBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return SearchHolder(recyclerSearchBinding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        holder.recyclerSearchBinding.recyclerViewSearchTextView.text = searchList[position].query
    }
}