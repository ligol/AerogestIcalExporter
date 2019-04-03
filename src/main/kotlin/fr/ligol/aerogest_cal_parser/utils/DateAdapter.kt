package fr.ligol.aerogest_cal_parser.utils

import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import java.text.SimpleDateFormat
import java.util.Date

import javax.xml.bind.annotation.adapters.XmlAdapter

class DateAdapter : XmlAdapter<String, Date>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm").apply {
        val registry = TimeZoneRegistryFactory.getInstance().createRegistry()
        this.timeZone = registry.getTimeZone("Europe/Paris")
    }

    @Throws(Exception::class)
    override fun marshal(v: Date): String {
        synchronized(dateFormat) {
            return dateFormat.format(v)
        }
    }

    @Throws(Exception::class)
    override fun unmarshal(v: String): Date {
        synchronized(dateFormat) {
            return dateFormat.parse(v)
        }
    }

}
