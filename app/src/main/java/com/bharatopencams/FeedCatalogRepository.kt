package com.bharatopencams

import org.json.JSONArray
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Reads the public, version-controlled catalog over HTTPS. Only entries with
 * explicit approved states and usage-appropriate permission are returned.
 * Camera URLs are never discovered or probed by this client.
 */
object FeedCatalogRepository {
    private const val CATALOG_URL =
        "https://raw.githubusercontent.com/vikasdocker/Bharat-Open-Cams/main/data/feeds.json"

    private const val CONNECT_TIMEOUT_MS = 8_000
    private const val READ_TIMEOUT_MS = 8_000

    fun loadApprovedFeeds(): List<CameraFeed> {
        val connection = (URL(CATALOG_URL).openConnection() as HttpsURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            instanceFollowRedirects = false
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Cache-Control", "no-cache")
            setRequestProperty("User-Agent", "BharatOpenCams-Android/0.1")
        }

        try {
            if (connection.url.protocol != "https" || connection.url.host != "raw.githubusercontent.com") {
                throw IllegalStateException("Catalog host must be the approved HTTPS host.")
            }
            val status = connection.responseCode
            if (status != HttpsURLConnection.HTTP_OK) {
                throw IllegalStateException("The catalog server returned HTTP $status.")
            }
            val payload = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            return parseApprovedFeeds(JSONArray(payload))
        } finally {
            connection.disconnect()
        }
    }

    internal fun parseApprovedFeeds(catalog: JSONArray): List<CameraFeed> {
        val approved = mutableListOf<CameraFeed>()
        for (index in 0 until catalog.length()) {
            val item = catalog.optJSONObject(index) ?: continue
            val authorization = item.optJSONObject("authorization") ?: continue
            if (item.optString("status") != "approved" ||
                authorization.optString("status") != "approved"
            ) continue

            val publisherUrl = httpsUrl(item.optString("publisher_url")) ?: continue
            val sourcePageUrl = httpsUrl(item.optString("source_page_url")) ?: continue
            val termsUrlRaw = item.optString("terms_url")
            val termsUrl = httpsUrl(termsUrlRaw)

            val authorizationType = authorization.optString("authorization_type")
            if (authorizationType !in setOf(
                    "publisher-terms",
                    "written-permission",
                    "explicit-public-license",
                    "publisher-public-page",
                    "publisher-embed-feature"
                )
            ) continue
            if (authorizationType in setOf("publisher-terms", "explicit-public-license") && termsUrl == null) continue

            val reuseScope = authorization.optString("reuse_scope")
            if (reuseScope !in setOf("link-only", "embedding", "redistribution")) continue

            val viewingMethod = item.optString("viewing_method")
            val viewingUrl = httpsUrl(item.optString("viewing_url"))
            if (viewingMethod !in setOf("official-link", "approved-embed", "approved-stream")) continue
            if (viewingMethod == "approved-embed" && reuseScope !in setOf("embedding", "redistribution")) continue
            if (viewingMethod == "approved-stream" && reuseScope != "redistribution") continue
            // This app currently supports only the three reviewed OpenCCTV viewer wrappers.
            // Do not accept arbitrary iframe hosts or raw upstream media URLs from the catalog.
            if (authorizationType == "publisher-embed-feature") {
                val viewerUri = runCatching { android.net.Uri.parse(viewingUrl ?: "") }.getOrNull()
                val validPath = Regex("^/cameras/india/[a-z0-9-]+/[a-z0-9-]+/[a-z0-9-]+-\\d{4,}$")
                    .matches(viewerUri?.path.orEmpty())
                if (viewingMethod != "approved-embed" ||
                    reuseScope != "embedding" ||
                    viewingUrl == null ||
                    viewingUrl != sourcePageUrl ||
                    viewerUri == null ||
                    viewerUri.scheme != "https" ||
                    viewerUri.host != "opencctv.org" ||
                    viewerUri.query != null ||
                    viewerUri.fragment != null ||
                    !validPath
                ) continue
            }
            if (authorizationType == "publisher-public-page" &&
                (viewingMethod != "official-link" || reuseScope != "link-only" || item.has("viewing_url"))
            ) continue

            val evidenceReference = authorization.optString("evidence_reference").trim()
            val termsReviewNote = authorization.optString("terms_review_note").trim()
            if (evidenceReference.isBlank() || termsReviewNote.length < 20) continue

            val name = item.optString("name").trim()
            val publisher = item.optString("publisher_name").trim()
            val attribution = item.optString("attribution").trim()
            val city = item.optString("city").trim()
            val feedId = item.optString("id").trim()
            if (feedId.isBlank() || name.isBlank() || publisher.isBlank() || attribution.isBlank() || city.isBlank()) continue

            approved += CameraFeed(
                id = feedId,
                name = name,
                description = item.optString("description", ""),
                publisherName = publisher,
                publisherUrl = publisherUrl,
                sourcePageUrl = sourcePageUrl,
                viewingUrl = if (authorizationType == "publisher-embed-feature") viewingUrl else null,
                termsUrl = termsUrl,
                termsReviewNote = termsReviewNote,
                city = city,
                area = item.optString("area", ""),
                category = item.optString("category"),
                viewingMethod = viewingMethod,
                reuseScope = reuseScope,
                attribution = attribution,
                verifiedAt = item.optString("verified_at")
            )
        }
        return approved.distinctBy { it.id }.sortedWith(
            compareBy<CameraFeed> { if (it.id.startsWith("india-")) 0 else 1 }
                .thenBy { it.city }
                .thenBy { it.name }
        )
    }
    private fun httpsUrl(value: String): String? {
        val uri = runCatching { android.net.Uri.parse(value) }.getOrNull() ?: return null
        return value.takeIf { uri.scheme == "https" && !uri.host.isNullOrBlank() }
    }
}

data class CameraFeed(
    val id: String,
    val name: String,
    val description: String,
    val publisherName: String,
    val publisherUrl: String,
    val sourcePageUrl: String,
    val viewingUrl: String?,
    val termsUrl: String?,
    val termsReviewNote: String,
    val city: String,
    val area: String,
    val category: String,
    val viewingMethod: String,
    val reuseScope: String,
    val attribution: String,
    val verifiedAt: String
) {
    val categoryLabel: String
        get() = when (category) {
            "traffic" -> "Traffic"
            "tourism" -> "Tourism"
            "weather" -> "Weather"
            "environment" -> "Environment"
            "public-venue" -> "Public places"
            else -> "Other approved"
        }
}
