package com.ph.phome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.app_item_layout.view.*

class AllAppListAdapter : RecyclerView.Adapter<AllAppListAdapter.AppViewHodler>() {

    private val appList = ArrayList<App>()
    var mOnAppClickListener: ((App) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHodler {
        val itemView = LayoutInflater.from(parent.context).inflate(viewType, null)
        return AppViewHodler(itemView)
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    override fun onBindViewHolder(holder: AppViewHodler, position: Int) {
        holder.itemView.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                ScaleAnimator.scaleUp(v)
            }else {
                ScaleAnimator.scaleDown(v)
            }
        }
        val app = appList[position]

        holder.itemView.icon?.setImageDrawable(app.icon)
        holder.itemView.name?.text = app.appName
        holder.itemView.setOnClickListener {
            mOnAppClickListener?.invoke(app)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.all_app_item_layout
    }

    fun submitAppList(apps: List<App>){
        appList.clear()
        appList.addAll(apps)
        notifyDataSetChanged()
    }

    inner class AppViewHodler(itemView: View) : RecyclerView.ViewHolder(itemView)
}