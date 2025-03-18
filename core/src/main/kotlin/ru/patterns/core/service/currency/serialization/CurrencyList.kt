package ru.patterns.core.service.currency.serialization

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty


data class CurrencyList(
    @JacksonXmlProperty(localName = "name", isAttribute = true)
    val name: String,

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Item")
    val items: List<CurrencyItem>
)