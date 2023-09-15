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
import org.jsoup.Jsoup

internal class MetaDataProber {

    suspend fun fetchMetadataSuspend(httpClient: HttpClient, url: String): Result<ProbedData> {
        return try {
            val httpResponse: HttpResponse = httpClient.get(url)
            if (httpResponse.status.value == 200) {
                val html = httpResponse.bodyAsText()
                val doc = Jsoup.parse(html)

                val title = doc.title()
                val description = doc.select("meta[name=description]").attr("content")
                val icon = doc.select("link[rel=icon]").attr("href")

                Result.success(
                    ProbedData(
                        title.ifEmpty { null },
                        description.ifEmpty { null },
                        icon.ifEmpty { null })
                )
            } else {
                Result.failure(Exception(MetaProbeError.ParsingError))
            }
        } catch (e: TimeoutCancellationException) {
            Result.failure(Exception(MetaProbeError.NetworkError))
        } catch (e: Throwable) {
            Result.failure(Exception(MetaProbeError.UnknownError))
        }
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
