package com.cenk.scorp.data

import Person
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import com.cenk.scorp.R
import com.cenk.scorp.data.PersonComparator.clearComparator
import com.cenk.scorp.data.PersonComparator.filterUnique

class PagingPersonAdapter : PagingDataAdapter<Person, PersonViewHolder>(PersonComparator) {

    private val people : MutableList<Person> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val item = inflater.inflate(R.layout.list_item, parent, false)
        return PersonViewHolder(item)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = getItem(position)
        "${person!!.fullName} (${person.id})".also { holder.fullName.text = it }
    }

    suspend fun addAll(additionalPeople: MutableList<Person>) {
        filterUnique(additionalPeople)
        if(additionalPeople.isNotEmpty()){
            people.addAll(additionalPeople)
            submitData(PagingData.from(people))
            notifyItemRangeInserted(itemCount, additionalPeople.size)
        }
    }

    suspend fun clear() {
        if (itemCount > 0) {
            clearComparator()
            people.clear()
            submitData(PagingData.from(people))
            notifyItemRangeRemoved(0, itemCount)
        }
    }

}