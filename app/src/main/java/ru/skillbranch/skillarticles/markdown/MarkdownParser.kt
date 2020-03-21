package ru.skillbranch.skillarticles.markdown

import java.util.regex.Pattern

object MarkdownParser {

    private val LINE_SEPARATOR = System.getProperty("line.separator") ?: "\n"

    //group regex
    private const val UNORDERED_LIST_ITEM_GROUP = "(^[*+-] .+$)"
    private const val HEADER_GROUP = "(^#{1,6} .+?$)"
    private const val QUOTE_GROUP = "(^> .+?$)"
    private const val ITALIC_GROUP = "((?<!\\*)\\*[^*].*?[^*]?\\*(?!\\*)|(?<!_)_[^_].*?[^_]?_(?!_))"
    private const val BOLD_GROUP =
        "((?<!\\*)\\*{2}[^*].*?[^*]?\\*{2}(?!\\*)|(?<!_)_{2}[^_].*?[^_]?_{2}(?!_))"
    private const val STRIKE_GROUP = "((?<!~)~{2}[^~].*?[^~]?~{2}(?!~))"
    private const val RULE_GROUP = "(^_{3}|-{3}|\\*{3}$)"
    private const val INLINE_GROUP = "((?<!`)`[^`\\s].*?[^`\\s]?`(?!`))"
    private const val LINK_GROUP = "(\\[[^\\[\\]]*?]\\(.+?\\)|^\\[*?]\\(.*?\\))"
    private const val BLOCK_CODE_GROUP = "(^```[\\s\\S]+?```$)"
    private const val ORDER_LIST_GROUP = "(^[\\d]{1,2}\\.\\s.+?$)"

    //result regex
    private const val MARKDOWN_GROUPS = "$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP|$QUOTE_GROUP|" +
            "$ITALIC_GROUP|$BOLD_GROUP|$STRIKE_GROUP|$RULE_GROUP|$INLINE_GROUP|$LINK_GROUP|" +
            "$BLOCK_CODE_GROUP|$ORDER_LIST_GROUP"

    private val elementsPattern by lazy { Pattern.compile(MARKDOWN_GROUPS, Pattern.MULTILINE) }

    /**
     * parse markdown text to elements
     */
    fun parse(string: String): MarkdownText {
        val elements = mutableListOf<Element>()
        elements.addAll(findElements(string))
        return MarkdownText(elements)
    }

    /**
     * clear markdown text to string without markdown characters
     */
    fun clear(string: String?): String? {
        return clearElements(string)
    }

    /**
     * find markdown elements in markdown text
     */
    private fun findElements(string: CharSequence): List<Element> {
        val parents = mutableListOf<Element>()
        val matcher = elementsPattern.matcher(string)
        var lastStartIndex = 0

        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()

            // if something is found then everything before - TEXT
            if (lastStartIndex < startIndex) {
                parents.add(Element.Text(string.subSequence(lastStartIndex, startIndex)))
            }

            // found text
            var text: CharSequence

            //groups range for iterate by groups (1..9) or (1..11) optionally
            val groups = 1..11
            var group = -1
            for (gr in groups) {
                if (matcher.group(gr) != null) {
                    group = gr
                    break
                }
            }

            when (group) {
                //NOT FOUND -> BREAK
                -1 -> break@loop

                //UNORDERED LIST
                1 -> {
                    //text without "*. "
                    text = string.subSequence(startIndex.plus(2), endIndex)

                    // find inner elements
                    val subs = findElements(text)
                    val element = Element.UnorderedListItem(text, subs)
                    parents.add(element)

                    // next find start from position "endIndex" (last regex character)
                    lastStartIndex = endIndex
                }

                //HEADER
                2 -> {
                    //text without "{#} "
                    val reg = "^#{1,6}".toRegex().find(string.subSequence(startIndex, endIndex))
                    val level = reg!!.value.length

                    text = string.subSequence(startIndex.plus(level.inc()), endIndex)
                    val element = Element.Header(level, text)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //QUOTE
                3 -> {
                    //text without "> "
                    text = string.subSequence(startIndex.plus(2), endIndex)

                    // find inner elements
                    val subs = findElements(text)
                    val element = Element.Quote(text, subs)
                    parents.add(element)

                    // next find start from position "endIndex" (last regex character)
                    lastStartIndex = endIndex
                }

                //ITALIC
                4 -> {
                    //text without "*{}*"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())

                    // find inner elements
                    val subs = findElements(text)
                    val element = Element.Italic(text, subs)
                    parents.add(element)

                    // next find start from position "endIndex" (last regex character)
                    lastStartIndex = endIndex
                }

                //BOLD
                5 -> {
                    //text without "**{}**"
                    text = string.subSequence(startIndex.plus(2), endIndex.minus(2))

                    // find inner elements
                    val subs = findElements(text)
                    val element = Element.Bold(text, subs)
                    parents.add(element)

                    // next find start from position "endIndex" (last regex character)
                    lastStartIndex = endIndex
                }

                //STRIKE
                6 -> {
                    //text without "~~{}~~"
                    text = string.subSequence(startIndex.plus(2), endIndex.minus(2))

                    // find inner elements
                    val subs = findElements(text)
                    val element = Element.Strike(text, subs)
                    parents.add(element)

                    // next find start from position "endIndex" (last regex character)
                    lastStartIndex = endIndex
                }

                //RULE
                7 -> {
                    //text without "***" insert empty character
                    val element = Element.Rule()
                    parents.add(element)

                    // next find start from position "endIndex" (last regex character)
                    lastStartIndex = endIndex
                }

                //INLINE CODE
                8 -> {
                    //text without "`{}`"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())

                    // find inner elements
                    val subs = findElements(text)
                    val element = Element.InlineCode(text, subs)
                    parents.add(element)

                    // next find start from position "endIndex" (last regex character)
                    lastStartIndex = endIndex
                }

                //LINK
                9 -> {
                    //full text for regex
                    text = string.subSequence(startIndex, endIndex)
                    // find inner elements
                    val (title: String, link: String) = "\\[(.*)]\\((.*)\\)".toRegex().find(text)!!.destructured

                    val element = Element.Link(link, title)
                    parents.add(element)

                    // next find start from position "endIndex" (last regex character)
                    lastStartIndex = endIndex
                }
                //10 -> BLOCK CODE - optionally
                10 -> {
                    text = string.subSequence(startIndex.plus(3), endIndex.minus(3)).toString()

                    if (text.contains(LINE_SEPARATOR)) {
                        for ((index, line) in text.lines().withIndex()) {
                            when (index) {
                                text.lines().lastIndex -> parents.add(
                                    Element.BlockCode(
                                        Element.BlockCode.Type.END,
                                        line
                                    )
                                )
                                0 -> parents.add(
                                    Element.BlockCode(
                                        Element.BlockCode.Type.START,
                                        line + LINE_SEPARATOR
                                    )
                                )
                                else -> parents.add(
                                    Element.BlockCode(
                                        Element.BlockCode.Type.MIDDLE,
                                        line + LINE_SEPARATOR
                                    )
                                )
                            }
                        }
                    } else parents.add(Element.BlockCode(Element.BlockCode.Type.SINGLE, text))

                    lastStartIndex = endIndex
                }

                //11 -> NUMERIC LIST
                11 -> {
                    val reg =
                        "^[\\d{1,2}.].".toRegex().find(string.subSequence(startIndex, endIndex))
                    val order = reg!!.value

                    text =
                        string.subSequence(startIndex.plus(order.length.inc()), endIndex).toString()

                    val subs = findElements(text)
                    val element = Element.OrderedListItem(order, text.toString(), subs)
                    parents.add(element)

                    lastStartIndex = endIndex
                }
            }

        }

        if (lastStartIndex < string.length) {
            val text = string.subSequence(lastStartIndex, string.length)
            parents.add(Element.Text(text))
        }
        return parents
    }

    private fun clearElements(string: CharSequence?): String? {
        if (string == null) {
            return null
        }

        val builder = StringBuilder()
        val matcher = elementsPattern.matcher(string)
        var lastStartIndex = 0

        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()

            if (lastStartIndex < startIndex) {
                builder.append(string.subSequence(lastStartIndex, startIndex))
            }

            var text: CharSequence

            //groups range for iterate by groups (1..9) or (1..11) optionally
            val groups = 1..11
            var group = -1
            for (gr in groups) {
                if (matcher.group(gr) != null) {
                    group = gr
                    break
                }
            }

            when (group) {
                //NOT FOUND -> BREAK
                -1 -> break@loop

                //UNORDERED LIST
                1 -> {
                    //text without "*. "
                    text = string.subSequence(startIndex.plus(2), endIndex)

                    val subs = clearElements(text)
                    builder.append(subs)

                    lastStartIndex = endIndex
                }

                //HEADER
                2 -> {
                    //text without "{#} "
                    val reg = "^#{1,6}".toRegex().find(string.subSequence(startIndex, endIndex))
                    val level = reg!!.value.length

                    text = string.subSequence(startIndex.plus(level.inc()), endIndex)
                    builder.append(text)

                    lastStartIndex = endIndex
                }

                //QUOTE
                3 -> {
                    //text without "> "
                    text = string.subSequence(startIndex.plus(2), endIndex)

                    val subs = clearElements(text)
                    builder.append(subs)

                    lastStartIndex = endIndex
                }

                //ITALIC
                4 -> {
                    //text without "*{}*"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())

                    val subs = clearElements(text)
                    builder.append(subs)

                    lastStartIndex = endIndex
                }

                //BOLD
                5 -> {
                    //text without "**{}**"
                    text = string.subSequence(startIndex.plus(2), endIndex.minus(2))

                    val subs = clearElements(text)
                    builder.append(subs)

                    lastStartIndex = endIndex
                }

                //STRIKE
                6 -> {
                    //text without "~~{}~~"
                    text = string.subSequence(startIndex.plus(2), endIndex.minus(2))

                    val subs = clearElements(text)
                    builder.append(subs)

                    lastStartIndex = endIndex
                }

                //RULE
                7 -> {
                    //text without "***" insert empty character
                    builder.append(" ")
                    lastStartIndex = endIndex
                }

                //INLINE
                8 -> {
                    //text without "`{}`"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    builder.append(text)
                    lastStartIndex = endIndex
                }

                //LINK
                9 -> {
                    //full text for regex
                    text = string.subSequence(startIndex, endIndex)
                    val (title: String, link: String) = "\\[(.*)]\\((.*)\\)".toRegex()
                        .find(text)!!.destructured
                    builder.append(title)

                    lastStartIndex = endIndex
                }
                //10 -> BLOCK CODE - optionally
                10 -> {
                    text = string.subSequence(startIndex + 3, endIndex - 3)
                    builder.append(text)
                    lastStartIndex = endIndex
                }

                //11 -> NUMERIC LIST
                11 -> {
                    val order = "^[\\d]".toRegex().find(string.subSequence(startIndex, endIndex))
                    text = string.subSequence(startIndex + order!!.value.length + 2, endIndex)
                    builder.append(text)
                    lastStartIndex = endIndex
                }
            }

        }

        if (lastStartIndex < string.length) {
            builder.append(string.subSequence(lastStartIndex, string.length))
        }

        return builder.toString()
    }
}

data class MarkdownText(val elements: List<Element>)

sealed class Element {
    abstract val text: CharSequence
    abstract val elements: List<Element>

    data class Text(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class UnorderedListItem(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Header(
        val level: Int = 1,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Quote(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Italic(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Bold(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Strike(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Rule(
        override val text: CharSequence = " ", //for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class InlineCode(
        override val text: CharSequence, //for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Link(
        val link: String,
        override val text: CharSequence, //for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class OrderedListItem(
        val order: String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class BlockCode(
        val type: Type = Type.MIDDLE,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element() {
        enum class Type { START, END, MIDDLE, SINGLE }
    }

}