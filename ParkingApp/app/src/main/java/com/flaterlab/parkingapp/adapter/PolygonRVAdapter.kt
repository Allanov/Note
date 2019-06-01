package com.flaterlab.parkingapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.flaterlab.parkingapp.R
import com.flaterlab.parkingapp.model.ParkingZone
import com.flaterlab.parkingapp.model.Polygon
import kotlinx.android.synthetic.main.item_polygon.view.*
import java.text.FieldPosition

class PolygonRVAdapter(val context: Context, val listener: OnPolygonClickedListener)
    : RecyclerView.Adapter<PolygonRVAdapter.ViewHolder>() {

    private var polygons: List<Polygon> = ArrayList()

    override fun getItemCount(): Int = polygons.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val polygon = polygons[position]
        val pol = context.getString(R.string.main_polygon_id)
        viewHolder.tvName.text = String.format(pol, polygon.id)
        viewHolder.tvDescription.text = polygon.description ?: ""

        // TODO: using Glide or Picasso implement image loading
    }

    override fun onCreateViewHolder(viewHolder: ViewGroup, position: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_polygon, viewHolder, false)
        return ViewHolder(v)
    }

    fun update(zone: ParkingZone) {
        polygons = zone.polygons
        notifyDataSetChanged()
    }

    fun update(polygons: List<Polygon>) {
        this.polygons = polygons
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        init {
            v.setOnClickListener {
                listener.onClick(polygons[adapterPosition])
            }
        }
        val ivImage: ImageView = v.findViewById(R.id.iv_image)
        val tvName: TextView = v.findViewById(R.id.tv_name)
        val tvDescription: TextView = v.findViewById(R.id.tv_description)
    }

    interface OnPolygonClickedListener {
        fun onClick(polygon: Polygon)
    }
}