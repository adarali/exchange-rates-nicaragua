package myprojects.exchangeratesnicaragua

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import myprojects.exchangeratesnicaragua.models.*
import myprojects.exchangeratesnicaragua.utils.AppServer
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.http.cookie.Cookie
import java.util.*
import java.math.BigDecimal

fun main(args: Array<String>) {
    Hello.parseArgs(args)() { Hello.getExchangeRateList() }
}

object Hello {

    val objectMapper = jacksonObjectMapper()

    fun parseArgs(args: Array<String>): (dataGetter: () -> List<ExchangeRate>) -> Unit {
        val options = Options()
        val serve1 = Option("s", "serve", true, "Create server")
        serve1.setOptionalArg(true)
        options.addOption(serve1)
        val help = Option ("help", "Print this message")
        options.addOption(help)
        val parser = DefaultParser()
        val cmd = parser.parse(options, args)

        if (cmd.hasOption("serve")) {
            return { AppServer.serve(cmd.getOptionValue("serve")?.toInt() ?: 8080) }
        }
        return { content ->
            content().forEach { println(it) }
            println("To create a server: add -s <port number>")
            println("8080 is the default port")
        }
    }

    fun getExchangeRateList(headers: MutableMap<String, String> = mutableMapOf(), respHeaders: MutableMap<String, String> = mutableMapOf()): List<ExchangeRate> {
        val list = Collections.synchronizedList(mutableListOf<ExchangeRate>())

        val threadList = mutableListOf<Thread>()
        threadList.add(Thread { val bac = BacExchangeRate.getExchangeRate(); list.add(bac) })
        threadList.add(Thread { val bdf = BdfExchangeRate.getExchangeRate(headers, respHeaders); list.add(bdf) })
        threadList.add(Thread { val banpro = BanproExchangeRate.getExchangeRate(); list.add(banpro) })
        threadList.add(Thread { val lafise = LafiseExchangeRate.getExchangeRate(); list.add(lafise) })
        threadList.add(Thread { val ficohsa = FicohsaExchangeRate.getExchangeRate(); list.add(ficohsa) })
        threadList.add(Thread { val avanz = AvanzExchangeRate.getExchangeRate(); list.add(avanz) })
        threadList.add(Thread { val atlantida = AtlantidaExchangeRate.getExchangeRate(); list.add(atlantida) })

        threadList.forEach { it.start() }
        threadList.forEach { it.join() }
        return list.filter { it.buyRate >= BigDecimal.ZERO }
    }
}



fun spark.Response.addCookie(cookie: Cookie) {
    try {
        if (!cookie.name.startsWith("visid_incap_")) {
            this.cookie(cookie.name, cookie.value)
        } else {
            this.cookie(cookie.name, cookie.value, 3600*24*365)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }


}