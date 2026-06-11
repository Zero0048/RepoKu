package com.zeze

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element
import java.util.Base64

class Anichin : MainAPI() {
    override var mainUrl = "https://anichin.cafe"
    override var name = "Anichin"
    override val hasMainPage = true

    override var lang = "id"

    override val supportedTypes = setOf(
        TvType.Anime
    )

    override val mainPage = mainPageOf(
        "$mainUrl/" to "Latest Donghua"
    )

    private fun Element.toSearchResponse(): SearchResponse {
        val title = selectFirst("h2")?.text()?.trim()
            ?: selectFirst(".tt")?.text()?.trim()
            ?: "Unknown"

        val href = fixUrl(
            selectFirst("a")?.attr("href") ?: ""
        )

        val poster = fixUrlNull(
            selectFirst("img")?.attr("src")
        )

        return newAnimeSearchResponse(
            title,
            href,
            TvType.Anime
        ) {
            this.posterUrl = poster
        }
    }

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {

        val doc = app.get(request.data).document

        val home = doc.select(".listupd article.bs")
            .map { it.toSearchResponse() }

        return newHomePageResponse(
            listOf(
                HomePageList(
                    request.name,
                    home
                )
            ),
            hasNext = false
        )
    }

    override suspend fun search(query: String): List<SearchResponse> {

        val doc = app.get(
            "$mainUrl/?s=$query"
        ).document

        return doc.select(".listupd article.bs")
            .map {
                it.toSearchResponse()
            }
    }

    override suspend fun load(url: String): LoadResponse {

        val doc = app.get(url).document

        val title =
            doc.selectFirst(".infolimit h2")?.text()
                ?: doc.selectFirst("h1")?.text()
                ?: throw ErrorLoadingException()

        val poster =
            fixUrlNull(
                doc.selectFirst(".thumb img")?.attr("src")
            )

        val description =
            doc.selectFirst(".desc")?.text()

        val tags =
            doc.select(".genxed a")
                .map { it.text() }

        val episodes = doc.select(".eplister li").map { ep ->

    val epUrl = fixUrl(
        ep.selectFirst("a")?.attr("href")
            ?: return@map null
    )

    val epName = ep.selectFirst(".epl-title")?.text()

    val epNumber = ep.selectFirst(".epl-num")
        ?.text()
        ?.trim()
        ?.toFloatOrNull()

    newEpisode(epUrl) {
        name = epName
        episode = epNumber?.toInt()
    }
}.filterNotNull()

        return newAnimeLoadResponse(
            title,
            url,
            TvType.Anime
        ) {
            posterUrl = poster
            plot = description
            this.tags = tags
            addEpisodes(DubStatus.Subbed, episodes)
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {

        val doc = app.get(data).document

        val iframe =
            doc.selectFirst("#embed_holder iframe")
                ?.attr("src")

        iframe?.let {
            loadExtractor(
                fixUrl(it),
                data,
                subtitleCallback,
                callback
            )
        }

        doc.select("select.mirror option").forEach { option ->

            val encoded = option.attr("value")

            if (encoded.isBlank()) return@forEach

            try {

                val html = String(
                    Base64.getDecoder().decode(encoded)
                )

                val src =
                    Regex("""src=["']([^"']+)""")
                        .find(html)
                        ?.groupValues
                        ?.get(1)

                src?.let {
                    loadExtractor(
                        fixUrl(it),
                        data,
                        subtitleCallback,
                        callback
                    )
                }

            } catch (_: Exception) {
            }
        }

        return true
    }
}