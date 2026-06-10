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

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {

        return newHomePageResponse(
            listOf(
                HomePageList("Anime", emptyList())
            )
        )
    }

    override suspend fun search(query: String): List<SearchResponse> {
        return emptyList()
    }

    override suspend fun load(url: String): LoadResponse {
        return newAnimeLoadResponse(
            "Anichin",
            url,
            TvType.Anime
        ) {}
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        return false
    }
}