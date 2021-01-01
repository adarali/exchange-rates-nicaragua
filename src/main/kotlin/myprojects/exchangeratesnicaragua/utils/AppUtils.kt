package myprojects.exchangeratesnicaragua.utils

import org.apache.http.cookie.Cookie
import org.apache.http.impl.cookie.BasicClientCookie

object AppUtils {
    fun parseCookie(cookieString: String): Cookie {
        val arr = cookieString.split("; ")
        val index = arr[0].indexOf("=")
        val cookieName = arr[0].substring(0, index)
        val cookieValue = arr[0].substring(index + 1)
        return BasicClientCookie(cookieName, cookieValue)
    }
}