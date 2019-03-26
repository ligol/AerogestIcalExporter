package fr.ligol.aerogest_cal_parser.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ArrayOfReservation")
class ArrayOfReservation(
        @field:XmlElement(name = "Reservation") val reservations: List<Reservation>? = null
)