package com.ph.phome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.city_item_layout.view.*

class CityListAdapter : RecyclerView.Adapter<CityListAdapter.CityViewHodler>() {

    private val cityList = ArrayList<String>()
    var mOnCityClickListener: ((Int) -> Unit)? = null
    var default: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHodler {
        val itemView = LayoutInflater.from(parent.context).inflate(viewType, null)
        return CityViewHodler(itemView)
    }

    override fun getItemCount(): Int {
        return cityList.size
    }

    override fun onBindViewHolder(holder: CityViewHodler, position: Int) {
//        holder.itemView.setOnFocusChangeListener { v, hasFocus ->
//            if(hasFocus){
//                ScaleAnimator.scaleUp(v)
//            }else {
//                ScaleAnimator.scaleDown(v)
//            }
//        }
        if(position == default){
            holder.itemView.radio.setImageResource(R.drawable.ic_radio_button_checked_orange_24dp)
            holder.itemView.requestFocus()
        }else{
            holder.itemView.radio.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp)
        }
        val current = cityList[position]
        holder.itemView.city?.text = current
        holder.itemView.setOnClickListener {
            default = position
            notifyDataSetChanged()
            mOnCityClickListener?.invoke(position)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.city_item_layout
    }

    fun submitCityList(cities: Array<String>){
        cityList.clear()
        cityList.addAll(cities)
        notifyDataSetChanged()
    }

    inner class CityViewHodler(itemView: View) : RecyclerView.ViewHolder(itemView)
}