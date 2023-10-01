package com.selcukileri.wakeup.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.selcukileri.wakeup.R
import com.selcukileri.wakeup.adapter.BookmarksAdapter
import com.selcukileri.wakeup.databinding.FragmentBookmarksBinding
import com.selcukileri.wakeup.model.Place
import com.selcukileri.wakeup.roomdb.PlaceDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class BookmarksFragment : Fragment() {
    private lateinit var binding: FragmentBookmarksBinding
    private val compositDisposable = CompositeDisposable()
    private var placeList: List<Place> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBookmarksBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = BookmarksAdapter(placeList)
        binding.recyclerView.adapter = adapter
        val db = Room.databaseBuilder(requireContext(), PlaceDatabase::class.java,"Places").build()
        val placeDao = db.placeDao()
        compositDisposable.add(placeDao.getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse)
        )
    }
    private fun handleResponse(placeList: List<Place>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = BookmarksAdapter(placeList)
        binding.recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.wakeup_menu,menu)
        return super.onCreateOptionsMenu(menu,inflater)
    }
    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_item) {
            val intent = Intent(requireContext(), MapsActivity::class.java)
            intent.putExtra("info", "new")
            startActivity(intent)

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositDisposable.clear()
    }

}

