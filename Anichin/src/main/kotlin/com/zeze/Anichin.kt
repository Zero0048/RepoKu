package com.zeze

import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.TvType

class Anichin : MainAPI() { // All providers must be an instance of MainAPI
    override var mainUrl = "https://anichin.cafe/" 
    override var name = "Anime"
    override val supportedTypes = setOf(TvType.Anime)

    override var lang = "id"

    // Enable this when your provider has a main page
    override val hasMainPage = true

    // This function gets called when you search for something
    override suspend fun search(query: String): List<SearchResponse> {
        return listOf()
    }
}