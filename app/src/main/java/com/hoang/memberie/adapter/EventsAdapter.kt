package com.hoang.memberie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hoang.memberie.R
import com.hoang.memberie.models.Event

class EventsAdapter() : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    var onItemClicked: (Event) -> Unit = { }
    private var dataSet = mutableListOf<Event>()


    fun setData(dataParam: List<Event>) {
        dataSet.clear()
        dataSet.addAll(dataParam)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentData: Event = dataSet[position]
        holder.eventTitle.text = currentData.title
        holder.eventThumbnail.load(currentData.photosUrls.firstOrNull()) {
            placeholder(R.drawable.placeholder_img)
        }
        holder.itemView.setOnClickListener { onItemClicked(currentData) }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var eventTitle: TextView = view.findViewById(R.id.tv_event_title)
        var eventThumbnail: ImageView = view.findViewById(R.id.iv_event_detail)

    }
}