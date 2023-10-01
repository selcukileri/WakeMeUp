package com.selcukileri.wakeup.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.Placeholder
import androidx.recyclerview.widget.RecyclerView
import com.selcukileri.wakeup.databinding.RecyclerBookmarksBinding
import com.selcukileri.wakeup.model.Place
import com.selcukileri.wakeup.view.MapsActivity

class BookmarksAdapter(val placeList: List<Place>) : RecyclerView.Adapter<BookmarksAdapter.BookmarkHolder>() {

    class BookmarkHolder(val recyclerBookmarksBinding: RecyclerBookmarksBinding) : RecyclerView.ViewHolder(recyclerBookmarksBinding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkHolder {
        val recyclerBookmarksBinding = RecyclerBookmarksBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BookmarkHolder(recyclerBookmarksBinding)
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    override fun onBindViewHolder(holder: BookmarkHolder, position: Int) {
        holder.recyclerBookmarksBinding.recyclerViewTextView.text = placeList[position].name
        holder.itemView.setOnClickListener{
            val context = holder.itemView.context
            val intent = Intent(context,MapsActivity::class.java)
            intent.putExtra("selectedPlace",placeList[position])
            intent.putExtra("info", "old")
            context.startActivity(intent)
        }
    }
}