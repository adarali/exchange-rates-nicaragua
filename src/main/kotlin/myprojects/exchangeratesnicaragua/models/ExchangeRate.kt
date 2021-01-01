package myprojects.exchangeratesnicaragua.models

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.math.BigDecimal

import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

interface ExchangeRate {
    val buyRate: BigDecimal
    val sellRate: BigDecimal
    val name: String

    fun toJson(): String {
        val mapper = ObjectMapper()
        return mapper.writeValueAsString(this)
    }
}

class BacExchangeRate(
    override val buyRate: BigDecimal,
    override val sellRate: BigDecimal,
    override val name: String = "BAC"
) : ExchangeRate {



    companion object {
        fun getExchangeRate(): BacExchangeRate {
            val url = "https://www.baccredomatic.com/es-ni/bac/exchange-rate-ajax/es-ni"
            val json = Jsoup.connect(url).ignoreContentType(true).get().body().text()
            val objectMapper = ObjectMapper()
            val map: Map<String, Object> = objectMapper.readValue(json)
            val buyRate = BigDecimal(map["buyRateUSD"].toString()).setScale(4)
            val sellRate = BigDecimal(map["saleRateUSD"].toString()).setScale(4)

            return BacExchangeRate(buyRate, sellRate)
        }
    }

    override fun toString(): String {
        return "BacExchangeRate(buyRate=$buyRate, sellRate=$sellRate, name='$name')"
    }
}

class BanproExchangeRate(
    override val buyRate: BigDecimal,
    override val sellRate: BigDecimal,
    override val name: String = "BANPRO"
) : ExchangeRate {



    companion object {
        fun getExchangeRate() : BanproExchangeRate {
            val url = "https://www.banprogrupopromerica.com.ni/umbraco/Surface/TipoCambio/Run?json=%7B%22operacion%22%3A2%7D"

            val text = Jsoup.connect(url).ignoreContentType(true).get().body().text()
            val buyRate = BigDecimal(text.substring(627, 634))
            val sellRate = BigDecimal(text.substring(724, 731))
            return BanproExchangeRate(buyRate, sellRate)
        }
    }

    override fun toString(): String {
        return "BanproExchangeRate(buyRate=$buyRate, sellRate=$sellRate, name='$name')"
    }
}

class LafiseExchangeRate(
    override val buyRate: BigDecimal,
    override val sellRate: BigDecimal,
    override val name: String = "LAFISE"
) : ExchangeRate {



    companion object {
        fun getExchangeRate() : LafiseExchangeRate {

            val postData = mutableMapOf<String, Any>()
            postData["Activo"] = true
            postData["PathUrl"] = "https://www.lafise.com/blb"
            postData["SimboloVenta"] = "https://www.lafise.com/blb"
            val url = "https://www.lafise.com/DesktopModules/Servicios/API/TasaCambio/VerPorPaisActivo"
            val jsonPayload = jacksonObjectMapper().writeValueAsString(postData)
            val request = HttpPost(url)
            request.setHeader("Accept", "application/json")
            request.setHeader("Content-type", "application/json")
            request.entity = StringEntity(jsonPayload)

            val client = HttpClients.createDefault();
            val response = client.execute(request)
            val data = EntityUtils.toString(response.entity)
            val mapList: List<Map<String, Any>> = jacksonObjectMapper().readValue(data)
            val map = mapList.first {it["SimboloVenta"] == "USD" && it["SimboloCompra"] == "NIO"}
            val buyRate = BigDecimal(map["ValorCompra"].toString().substring(5)).setScale(4)
            val sellRate = BigDecimal(map["ValorVenta"].toString().substring(5)).setScale(4)
            return LafiseExchangeRate(buyRate, sellRate)
        }
    }

    override fun toString(): String {
        return "LafiseExchangeRate(buyRate=$buyRate, sellRate=$sellRate, name='$name')"
    }
}

class FicohsaExchangeRate(
    override val buyRate: BigDecimal,
    override val sellRate: BigDecimal,
    override val name: String = "FICOHSA"
) : ExchangeRate {

    override fun toString(): String {
        return "FicohsaExchangeRate(buyRate=$buyRate, sellRate=$sellRate, name='$name')"
    }

    companion object {
        fun getExchangeRate() : FicohsaExchangeRate {
            val url = "https://www.ficohsa.com/ni/nicaragua/tipo-de-cambio/"
            val doc = Jsoup.connect(url).get()
            val buyIndex = doc.text().indexOf("Compra: ") + 8
            val sellIndex = doc.text().indexOf("Venta: ") + 7
            val buyRate = BigDecimal(doc.text().substring(buyIndex, buyIndex + 7))
            val sellRate = BigDecimal(doc.text().substring(sellIndex, sellIndex + 7))

            return FicohsaExchangeRate(buyRate, sellRate)
        }
    }
}

class AvanzExchangeRate(
    override val buyRate: BigDecimal,
    override val sellRate: BigDecimal,
    override val name: String = "Avanz"
) : ExchangeRate {

    override fun toString(): String {
        return "AvanzExchangeRate(buyRate=$buyRate, sellRate=$sellRate, name='$name')"
    }

    companion object {
        fun getExchangeRate(): AvanzExchangeRate {

            return try {
                val url = "https://www.avanzbanc.com/Pages/Empresas/ServiciosFinancieros/MesaCambio.aspx"
                val element = Jsoup.connect(url).get().getElementById("avanz-mobile-tipo-cambio")
                val bdList = element.children().map { BigDecimal(it.text().trim()).setScale(4) }
                AvanzExchangeRate(bdList[0], bdList[1])
            } catch (e: Exception) {
                AvanzExchangeRate(BigDecimal("-1"), BigDecimal("-1"))
            }
        }
    }
}

class AtlantidaExchangeRate(
    override val buyRate: BigDecimal,
    override val sellRate: BigDecimal,
    override val name: String = "Atlantida"
) : ExchangeRate {

    override fun toString(): String {
        return "AtlantidaExchangeRate(buyRate=$buyRate, sellRate=$sellRate, name='$name')"
    }

    companion object {
        fun getExchangeRate() : AtlantidaExchangeRate {
            val url ="https://bancoatlantida.com.ni/js/moneda.js"
            val doc = Jsoup.connect(url).ignoreContentType(true).get()
            val buyIndex = doc.body().text().indexOf("dh_precio_compra = ") + 20
            val sellIndex = doc.body().text().indexOf("dh_precio_venta = ") + 19
            val buyRate = BigDecimal(doc.body().text().substring(buyIndex, buyIndex + 5)).setScale(4)
            val sellRate = BigDecimal(doc.body().text().substring(sellIndex, sellIndex + 5)).setScale(4)

            return AtlantidaExchangeRate(buyRate, sellRate)
        }
    }
}