package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
    if (this.isNullOrBlank() or substr.isBlank()) {
        return listOf()
    }
    val template: String
    val search: String
    if (ignoreCase) {
        template = this!!.toLowerCase()
        search = substr.toLowerCase()
    } else {
        template = this!!
        search = substr
    }
    return search
        .toRegex()
        .findAll(template)
        .toList()
        .map { it.range.first }
}