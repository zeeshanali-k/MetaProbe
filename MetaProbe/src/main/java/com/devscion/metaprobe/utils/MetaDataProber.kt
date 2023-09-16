package com.devscion.metaprobe.utils

import com.devscion.metaprobe.model.MetaProbeError
import com.devscion.metaprobe.model.ProbedData
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class MetaDataProber {

    suspend fun fetchMetadataSuspend(httpClient: HttpClient, url: String): Result<ProbedData> {
        return try {
            withContext(Dispatchers.IO) {
                val httpResponse: HttpResponse = httpClient.get(url)
                if (httpResponse.status.value == 200) {
                    val html = httpResponse.bodyAsText()
                    val doc = Jsoup.parse(html)

                    val title = doc.title()
                    val description = doc.select("meta[name=description]").attr("content")
                    val icon =
                        doc.select("link[rel=icon]").attr("href").ifEmpty { parseIcon(html, url) }

                    Result.success(
                        ProbedData(
                            title.ifEmpty { null },
                            description.ifEmpty { null },
                            icon?.ifEmpty { null })
                    )
                } else {
                    Result.failure(Exception(MetaProbeError.ParsingError))
                }
            }
        } catch (e: TimeoutCancellationException) {
            Result.failure(Exception(MetaProbeError.NetworkError))
        } catch (e: Throwable) {
            Result.failure(Exception(MetaProbeError.UnknownError))
        }
    }


    private fun parseIcon(html: String, url: String): String? {
        val lines = html.lineSequence()

        var icon: String? = null
        for (line in lines) {

            if (icon == null) {
                // Try parsing different types of icon links
                icon = Regex(
                    "<link rel=\"icon\" href=\"(.*?)\"",
                    RegexOption.IGNORE_CASE
                ).find(line)?.groups?.get(1)?.value
                    ?: Regex(
                        "<link rel=\"shortcut icon\" href=\"(.*?)\"",
                        RegexOption.IGNORE_CASE
                    ).find(line)?.groups?.get(1)?.value
                            ?: Regex(
                        "<link rel=\"apple-touch-icon\" href=\"(.*?)\"",
                        RegexOption.IGNORE_CASE
                    ).find(line)?.groups?.get(1)?.value

                if (icon != null && !icon.startsWith("http")) {
                    // Handle relative URLs
                    icon = url + icon
                }
            }

            if (icon != null) {
                return icon
            }
        }
        return null
    }


    fun fetchMetadataCallback(
        httpClient: HttpClient,
        url: String,
        callback: (Result<ProbedData>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            fetchMetadataSuspend(httpClient, url).let(callback)
        }
    }
}
