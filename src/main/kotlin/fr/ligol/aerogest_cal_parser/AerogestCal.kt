package fr.ligol.aerogest_cal_parser

import net.fortuna.ical4j.util.MapTimeZoneCache
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AerogestCal

fun main(args: Array<String>) {
    System.setProperty("net.fortuna.ical4j.timezone.cache.impl", MapTimeZoneCache::class.java.name)

    runApplication<AerogestCal>(*args)
}
