package com.example.fitfeast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitfeast.R

class EntriesAdapter(private var entries: MutableList<HealthEntry>) : RecyclerView.Adapter<EntriesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val entryDate: TextView = view.findViewById(R.id.entryDate)
        val entryType: TextView = view.findViewById(R.id.entryType)
        val entryDescription: TextView = view.findViewById(R.id.entryDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val entryView = inflater.inflate(R.layout.item_entry, parent, false)
        return ViewHolder(entryView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.entryDate.text = entry.date
        holder.entryType.text = entry.type
        holder.entryDescription.text = entry.description
    }

    override fun getItemCount(): Int {
        return entries.size
    }

    fun updateEntries(newEntries: List<HealthEntry>) {
        entries = newEntries.toMutableList()
        notifyDataSetChanged()
    }
}
