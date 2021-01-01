package myprojects.exchangeratesnicaragua.models

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.jsoup.Jsoup
import java.math.BigDecimal

class BdfExchangeRate(
    override val buyRate: BigDecimal,
    override val sellRate: BigDecimal,
    override val name: String = "BDF"
) : ExchangeRate {

    companion object {

        var incapSes: String = ""

        fun getExchangeRate(requestHeaderMap: MutableMap<String, String> = mutableMapOf(), responseHeaderMap: MutableMap<String, String> = mutableMapOf()): BdfExchangeRate {

            val url = "https://www.bdfnet.com/"

            val httpGet = HttpGet(url)
            httpGet.addHeader("Host", "www.bdfnet.com")
            if(incapSes.isNotBlank()) {
                httpGet.addHeader("Cookie", incapSes)
            } else {
                if(!requestHeaderMap["Cookie"].isNullOrBlank()) httpGet.addHeader("Cookie", requestHeaderMap["Cookie"])
            }

            val client = HttpClients.createDefault()

            val resp = client.execute(httpGet)

            resp.allHeaders.forEach {
                responseHeaderMap[it.name] = it.value
            }


            val doc = Jsoup.parse(EntityUtils.toString(resp.entity))
//            println(doc)
            val buyRate = BigDecimal(
                doc.getElementById("ctl00_ContentPlaceHolder1_wucHerramientas1_lblCompraDolar")?.text() ?: "-1.0000"
            )
            val sellRate = BigDecimal(
                doc.getElementById("ctl00_ContentPlaceHolder1_wucHerramientas1_lblVentaDolar")?.text() ?: "-1.0000"
            )

            incapSes = if(buyRate < BigDecimal.ZERO) {
                ""
            } else {
                requestHeaderMap["Cookie"]?: incapSes
            }

            return BdfExchangeRate(buyRate, sellRate)
        }

        private fun getHeaders(): Map<String, String> {
            val map = mutableMapOf<String, String>()
            map["Host"] = "www.bdfnet.com"
            map["User-Agent"] = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:84.0) Gecko/20100101 Firefox/84.0"
            map["Accept"] = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
            map["Accept-Language"] = "en-US,en;q=0.5"
            map["Accept-Encoding"] = "gzip, deflate, br"
            map["Connection"] = "keep-alive"
            map["Upgrade-Insecure-Requests"] = "1"
            map["Cache-Control"] = "max-age=0"
            return map
        }
    }

    override fun toString(): String {
        return "BdfExchangeRate(buyRate=$buyRate, sellRate=$sellRate, name='$name')"
    }
}
