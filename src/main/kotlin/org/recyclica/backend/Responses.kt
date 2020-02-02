package org.recyclica.backend

import org.joda.time.DateTime

class Response<T>(val data: T? = null, val error: String? = null)

fun responseError(error: String) = Response<Any>(error = error)

fun <T> responseOf(data: T) = Response(data = data)
private val responseEmpty = Response<Any>()
fun responseOf() = responseEmpty