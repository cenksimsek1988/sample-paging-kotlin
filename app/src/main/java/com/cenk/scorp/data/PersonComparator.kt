package com.cenk.scorp.data

import Person
import androidx.recyclerview.widget.DiffUtil
object PersonComparator : DiffUtil.ItemCallback<Person>() {
    private val existingIds : MutableList<Int> = ArrayList()

    fun clearComparator(){
        existingIds.clear()
    }

    fun filterUnique(peopleToAdd: MutableList<Person>){
        val indicesToRemove : MutableList<Int> = ArrayList()

        peopleToAdd.forEachIndexed { i, p ->
            var exists :Boolean = false
            for(existingId in existingIds){
                if(p.id == existingId){
                    indicesToRemove.add(i)
                    exists = true;
                    break
                }
            }
            if(!exists){
                existingIds.add(p.id)
            }
        }
        for (i in indicesToRemove.indices.reversed()) {
            peopleToAdd.removeAt(indicesToRemove[i])
        }
    }
    override fun areItemsTheSame(oldItem: Person, newItem: Person): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Person, newItem: Person): Boolean {
        return oldItem.fullName == newItem.fullName
    }

}
