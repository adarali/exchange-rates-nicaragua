package myprojects.exchangeratesnicaragua.utils

import myprojects.exchangeratesnicaragua.Hello
import spark.Spark

object AppServer {
    fun serve(port: Int = 8080) {
        Spark.port(port)
        Spark.staticFiles.location("static")

        Spark.before(
            {_, res ->res.header("access-control-allow-origin", "*")},
        )


        Spark.get("/rates") { req, res ->
            val headerMap = mutableMapOf<String, String>()
            req.headers().forEach {
                headerMap[it] = req.headers(it)
            }
            val respHeaderMap = mutableMapOf<String, String>()
            val list = Hello.getExchangeRateList(headerMap, respHeaderMap)
            respHeaderMap.forEach {
//                res.header(it.key, it.value)
            }
            res.type("application/json; charset=utf-8")

            Hello.objectMapper.writeValueAsString(list)//            "Hello world!"

        }
        println("Listening on http://localhost:$port/")
        println("Get rates in json format: http://localhost:$port/rates")


    }

}