package org.recyclica.backend

enum class RecyclicaField(val fieldName: String, val min: Int, val max: Int, regex: String? = null) {
    USERNAME("username", 3, 16, "^[a-zA-Z_\\-0-9]+$"),
    FULLNAME("fullname", 1, 256),
    PHONE("phone", 1, 15, "^[0-9]+$"),
    EMAIL(
        "email",
        1,
        254,
        "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$"
    ),
    PASSWORD("password", 6, 256),
    TOKEN("token", 32);

    private val regexCompiled = if (regex == null) null else Regex(regex)

    constructor(name: String, length: Int, regex: String? = null) : this(name, length, length, regex)

    fun isValidLength(input: String) = input.length in min..max

    fun check(input: String?): Error? {
        if (input == null || !isValidLength(input))
            return Error.LENGTH
        if (regexCompiled?.matches(input) == false)
            return Error.FORMAT
        return null
    }

    fun checkString(input: String?): String? {
        val err = check(input)
        return if (err == null)
            null
        else "invalid_${fieldName}_${err.value}"
    }

    fun isValid(input: String?) = check(input) == null

    enum class Error(val value: String) {
        FORMAT("format"),
        LENGTH("length")
    }
}