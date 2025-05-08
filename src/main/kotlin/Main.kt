package ind.glowingstone.nuclear

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import java.util.concurrent.atomic.AtomicInteger

var page: AtomicInteger = AtomicInteger(0)
var end: Int = 20
suspend fun main() {
	for (i in 1.. 900) {
		val result = cleanHtml(sendPostRequest(i))
		val list = extractCompanyNamesAndUrls(result)
		list.forEach {
			if (it.first.startsWith("关于天津") || it.first.startsWith("关于上海魅知文化")) {
				println("${it.first} -> ${it.second} (from page $i)")
			}
		}
	}
	println("Complete")
}
fun cleanHtml(html: String): String {
	val document = Jsoup.parse(html)

	document.select("script, style").remove()
	return document.body().html()
}
fun extractCompanyNamesAndUrls(html: String): List<Pair<String, String>> {
	val document = Jsoup.parse(html)
	val links = document.select("a")

	val companyList = mutableListOf<Pair<String, String>>()

	for (link in links) {
		val companyName = link.text()
		val url = link.attr("href")
		if (companyName.isNotEmpty() && url.startsWith("/") && companyName != "查看") {
			companyList.add(Pair(companyName, "http://wsbs.wgj.sh.gov.cn$url"))
		}
	}

	return companyList
}
suspend fun sendPostRequest(page: Int) : String{
	val client = HttpClient(CIO) {
		install(ContentNegotiation) {
			json(Json { prettyPrint = true })
		}
	}

	try {
		val response: String = client.post("http://wsbs.wgj.sh.gov.cn/shwgj_ywtb/core/web/welcome/index!toResultNotice.action?flag=1") {
			contentType(ContentType.Application.FormUrlEncoded)
			setBody(
				FormDataContent(Parameters.build {
					append("pageDoc.pageNo", page.toString())
				})
			)
			headers {
				append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
			}
		}.body()

		return response
	} catch (e: Exception) {
		println("Error: ${e.message}")
		return ""
	} finally {
		client.close()
	}
}