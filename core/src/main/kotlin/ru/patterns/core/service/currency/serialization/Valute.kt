package ru.patterns.core.service.currency.serialization

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty


data class Valute(
    @JacksonXmlProperty(localName = "ID", isAttribute = true)
    val id: String,

    @JacksonXmlProperty(localName = "NumCode")
    val numCode: String,

    @JacksonXmlProperty(localName = "CharCode")
    val charCode: String,

    @JacksonXmlProperty(localName = "Nominal")
    val nominal: String,

    @JacksonXmlProperty(localName = "Name")
    val name: String,

    @JacksonXmlProperty(localName = "Value")
    val value: String,

    @JacksonXmlProperty(localName = "VunitRate")
    val vunitRate: String,
)