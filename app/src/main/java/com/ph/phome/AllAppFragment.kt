package com.ph.phome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_shortcut_selection.*

class AllAppFragment : DialogFragment() {


    lateinit var appViewModel: AppViewModel
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val appListAdapter = AllAppListAdapter()
        appViewModel = ViewModelProviders.of(activity!!).get(AppViewModel::class.java)
        appViewModel.appList?.observe(this, Observer {
            appListAdapter.submitAppList(it)
        })
//        appViewModel.addNewAppIntoDB(activity!!)
        appListAdapter.mOnAppClickListener = {
            appViewModel.startApp(it, activity!!)
        }
        app_grid?.adapter = appListAdapter
        app_grid?.layoutManager = GridLayoutManager(context,6)
//        app_grid?.addItemDecoration(object : RecyclerView.ItemDecoration(){
//
//            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
//                super.getItemOffsets(outRect, view, parent, state)
//                outRect.set(10,10,10,10)
//            }
//        })

        val appList = appViewModel.appList?.value
        appList?.let {
            appListAdapter.submitAppList(it)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all_apps,container,false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_NoTitleBar_Fullscreen)
        super.onCreate(savedInstanceState)
    }
}