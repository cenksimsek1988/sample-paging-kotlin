package com.cenk.scorp.data

import Person
import com.google.gson.annotations.SerializedName

class PersonResponse {
    data class PagedResponse(
            @SerializedName("info") val pageInfo: PageInfo,
            val results: List<Person> = listOf()
    )

    data class PageInfo(
            val count: Int,
            val pages: Int,
            val next: String,
            val prev: String?
    )
}