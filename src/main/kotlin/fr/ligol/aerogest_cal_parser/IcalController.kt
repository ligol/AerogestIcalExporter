package fr.ligol.aerogest_cal_parser;

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("ical")
class IcalController {

    @RequestMapping(produces = ["text/calendar"])
    fun calendar(@RequestParam("firstname") firstname: String, @RequestParam("lastname") lastname: String): String {
        return IcalGenerator.getIcal("$lastname $firstname")
    }
}