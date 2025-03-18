package ru.patterns.core.service.currency.serialization

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty


data class CurrencyItem(
    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    val id: String,

    @JacksonXmlProperty(localName = "Name")
    val name: String,

    @JacksonXmlProperty(localName = "EngName")
    val engName: String,

    @JacksonXmlProperty(localName = "Nominal")
    val nominal: String,

    @JacksonXmlProperty(localName = "ParentCode")
    val parentCode: String,

    @JacksonXmlProperty(localName = "ISO_Num_Code")
    val isoNumCode: String,

    @JacksonXmlProperty(localName = "ISO_Char_Code")
    val isoCharCode: String
)