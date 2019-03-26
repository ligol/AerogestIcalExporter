package fr.ligol.aerogest_cal_parser.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ressource")
data class Ressource(
        @field:XmlElement val ressourceName: String? = null,
        @field:XmlElement val type: Int? = null
)