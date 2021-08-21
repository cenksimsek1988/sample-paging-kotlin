package com.cenk.scorp.data

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cenk.scorp.R

class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val fullName: TextView = itemView.findViewById(R.id.full_name)

}