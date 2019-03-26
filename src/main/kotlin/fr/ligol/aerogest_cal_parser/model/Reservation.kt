package fr.ligol.aerogest_cal_parser.model

import fr.ligol.aerogest_cal_parser.utils.DateAdapter
import java.util.*
import javax.xml.bind.annotation.*
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Reservation")
data class Reservation(
        @field:XmlJavaTypeAdapter(DateAdapter::class) @field:XmlElement val from: Date? = null,
        @field:XmlJavaTypeAdapter(DateAdapter::class) @field:XmlElement val to: Date? = null,
        @field:XmlElement val description: String? = null,
        @field:XmlElement val destination: String? = null,
        @field:XmlElement val tempsDeVol: String? = null,
        @field:XmlElement val type: String? = null,
        @field:XmlElementWrapper(name="ressources") @field:XmlElement(name = "Ressource") val ressources: List<Ressource>? = null
)

