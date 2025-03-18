package ru.patterns.core.service.currency.serialization

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "ValCurs")
data class ValCurs(
    @JacksonXmlProperty(isAttribute = true, localName = "Date")
    val date: String,

    @JacksonXmlProperty(isAttribute = true, localName = "name")
    val name: String,

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Valute")
    val valutes: List<Valute>
)