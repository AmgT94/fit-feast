package com.example.fitfeast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GoalsAdapter(private val goalsList: List<Goal>) : RecyclerView.Adapter<GoalsAdapter.GoalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_goal, parent, false)
        return GoalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val currentGoal = goalsList[position]
        holder.bind(currentGoal)
    }

    override fun getItemCount() = goalsList.size

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.goalTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.goalDescription)

        fun bind(goal: Goal) {
            titleTextView.text = goal.title
            descriptionTextView.text = goal.description
        }
    }
}
