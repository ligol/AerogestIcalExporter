package fr.ligol.aerogest_cal_parser

import fr.ligol.aerogest_cal_parser.model.ArrayOfReservation
import fr.ligol.aerogest_cal_parser.model.Reservation
import net.fortuna.ical4j.data.CalendarOutputter
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.DateTime
import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.CalScale
import net.fortuna.ical4j.model.property.Description
import net.fortuna.ical4j.model.property.ProdId
import net.fortuna.ical4j.model.property.Version
import net.fortuna.ical4j.util.RandomUidGenerator
import okhttp3.*
import java.io.StringWriter
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.TimeUnit
import javax.xml.bind.JAXBContext

object IcalGenerator {
    private val registry = TimeZoneRegistryFactory.getInstance().createRegistry()
    private val timezone = registry.getTimeZone("Europe/Paris")
    private val tz = timezone.vTimeZone

    private val ug = RandomUidGenerator()

    private val getOkhttpClient = OkHttpClient.Builder()
                .callTimeout(1, TimeUnit.HOURS)
                .connectTimeout(1, TimeUnit.HOURS)
                .readTimeout(1, TimeUnit.HOURS)
                .writeTimeout(1, TimeUnit.HOURS)
                .cookieJar(object: CookieJar {
                    private val cookieStore = HashMap<String, ArrayList<Cookie>>().apply {
                        val url = HttpUrl.get("http://www.aerogest-reservation.com")
                        val aeroClubId = System.getenv("AERO_CLUB_ID")
                        val listOfCookie = ArrayList<Cookie>()
                        Cookie.parse(url, "hasConsent=true")?.let { listOfCookie.add(it) }
                        Cookie.parse(url, "Aerogest-reservation-str=$aeroClubId")?.let { listOfCookie.add(it) }
                        Cookie.parse(url, "ASP.NET_SessionId=vv24lnj2wj0udr44vqj4ocpr")?.let { listOfCookie.add(it) }
                        Cookie.parse(url, "Aerogest-reservation-usr=")?.let { listOfCookie.add(it) }
                        put(url.host(), listOfCookie)
                    }
                    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
                        cookieStore[url.host()]?.addAll(cookies)
                    }

                    override fun loadForRequest(url: HttpUrl): List<Cookie> {
                        val cookies = cookieStore[url.host()]
                        return cookies?: ArrayList()
                    }

                })
                .followRedirects(false)
                .authenticator { _, response ->
                    connect()

                    response.request().newBuilder().build()
                }.build()

    init {
        connect()
    }

    private fun connect() {
        val login = System.getenv("LOGIN")
        val password = System.getenv("PASSWORD")
        val formBody = FormBody.Builder()
                .add("login", login)
                .add("mdp", password)
                .add("conserverconnexion", "true")
                .add("action:LogOn", "Connexion")
                .build()
        val request = Request.Builder()
                .url("http://www.aerogest-reservation.com/Connection")
                .post(formBody)
                .build()
        val authResponse = getOkhttpClient.newCall(request).execute()
        if (!authResponse.isSuccessful && !authResponse.isRedirect) {
            error("Authentication impossible")
        }
    }

    private fun getReservation(): List<Reservation> {
        val aeroClubId = System.getenv("AERO_CLUB_ID")
        val formBody = FormBody.Builder()
                .add("nomaeroclub", aeroClubId)
                .add("applyanonymat", "True")
                .add("limitto30days", "False")
                .build()
        val request = Request.Builder()
                .url("http://www.aerogest-reservation.com/admin/getDailyReservationsByClub")
                .post(formBody)
                .build()

        val response = getOkhttpClient.newCall(request).execute()

        val unmarshaller = JAXBContext.newInstance(ArrayOfReservation::class.java).createUnmarshaller()
        return (unmarshaller.unmarshal(response.body()?.byteStream()) as ArrayOfReservation).reservations?: ArrayList()
    }

    fun getIcal(personName: String): String {
        val calendar = Calendar()
        calendar.properties.add(ProdId("-//$personName Aerogest//FR"))
        calendar.properties.add(Version.VERSION_2_0)
        calendar.properties.add(CalScale.GREGORIAN)

        getReservation().forEach {reservation ->
            if (reservation.ressources?.any { it.ressourceName?.contains(personName) == true } == true) {
                val plane = reservation.ressources.find { it.type == 1 }
                val instructor = reservation.ressources.find { it.type == 2 && it.ressourceName?.contains(personName) != true }

                val planeString = if (plane != null) "sur ${plane.ressourceName}" else ""
                val instructorString = if (instructor != null) "avec ${instructor.ressourceName}" else ""

                val destination = if (!reservation.destination.isNullOrEmpty()) "a destination de ${reservation.destination}" else ""

                val description = Description("${reservation.type?: "Vol"} $instructorString $planeString $destination")

                val event = VEvent(DateTime(reservation.from), DateTime(reservation.to), "${reservation.type?: "Vol"} $instructorString $planeString")
                event.properties.add(tz.timeZoneId)
                val uid = ug.generateUid()
                event.properties.add(uid)
                event.properties.add(description)
                calendar.components.add(event)
            }
        }

        val writer = StringWriter()
        val calendarOutput = CalendarOutputter()
        calendarOutput.output(calendar, writer)
        return writer.toString()
    }
}