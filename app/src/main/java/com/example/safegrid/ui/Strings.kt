package com.example.safegrid.ui

enum class Lang(val code: String, val label: String) {
    EN("en", "English"),
    ZU("zu", "isiZulu"),
    AF("af", "Afrikaans");

    companion object {
        fun from(code: String): Lang = entries.firstOrNull { it.code == code } ?: EN
    }
}

enum class S {
    Tagline, GetStarted, WelcomeBack, SignInContinue, Email, Password, Login, ContinueGoogle,
    NoAccount, CreateAccount, JoinCommunity, FullName, Back, Home, Power, Map, Sos, Settings,
    GoodMorning, GoodAfternoon, GoodEvening, PowerStatus, StageLabel, NoLoadshedding, NextOutage,
    ViewSchedule, CommunitySafety, IncidentsNearby, StayAware, ViewMap, Report, Emergency,
    SosButton, GuardianPoints, CommunityHelper, ScheduleTitle, YourArea, CurrentStage,
    OfflineSchedule, ShowingCached, LastSynced, Refresh, ApiResponse, CommunityMap, ReportIncident,
    IncidentCategory, Crime, Suspicious, Traffic, Describe, AddPhoto, PhotoAttached,
    LocationDetected, LocationHelp, SubmitReport, SosReady, SosActivated, SosHelp, SosSent,
    Contacts, Mom, Dad, Watch, Language, Notifications, NotifSub, Radius, OfflineData,
    PendingSync, LogOut, CloudStatus, PasswordNotice, HashPreview, ErrorEmail, ErrorPassword,
    ErrorName, ErrorDescription, FirebaseMissing, SignedInAs, AuthService, NoSqlDatabase,
    BlobStorage, SyncNow, SavedOffline, SavedCloud, PhotoUploaded, CancelSos, SourceLive,
    SourceCache, Area, Save, ReportSent, NoneScheduled, NotSignedIn, EmptyIncidents,
    TestNotification, NotificationSent, GoogleUnavailable, NotSynced, ApiLoading, ApiFailed,
    CloudReady, CloudNotReady, Uid, Provider, LastWrite, PleaseWait, Today, Tomorrow, Day3, Day4,
    EstimatedNote, SettingsSaved, SosRecorded, ErrorEmailUsed, ErrorBadLogin, ErrorNetwork,
    ErrorGeneric, PasswordStorage, UsersCollection, IncidentsCollection, AlertsCollection,
    Synced, Pending, WorkingOffline, Connected, StageUnknown, ConfigureSteps, RadiusValue,
    PointsValue, NoPhotoYet, ProviderPassword, ProviderGoogle
}

object Strings {
    private fun row(en: String, zu: String, af: String) = mapOf(
        Lang.EN to en,
        Lang.ZU to zu,
        Lang.AF to af
    )

    private val table: Map<S, Map<Lang, String>> = mapOf(
        S.Tagline to row(
            "Stay Powered.\nStay Safe.\nStay Connected.",
            "Hlala unamandla.\nHlala uphephile.\nHlala uxhunyiwe.",
            "Bly aangeskakel.\nBly veilig.\nBly verbind."
        ),
        S.GetStarted to row("GET STARTED", "QALA", "BEGIN"),
        S.WelcomeBack to row("Welcome back", "Siyakwamukela futhi", "Welkom terug"),
        S.SignInContinue to row(
            "Sign in to continue to SafeGrid",
            "Ngena ukuze uqhubeke ku-SafeGrid",
            "Meld aan om met SafeGrid voort te gaan"
        ),
        S.Email to row("Email address", "Ikheli le-imeyili", "E-posadres"),
        S.Password to row("Password", "Iphasiwedi", "Wagwoord"),
        S.Login to row("LOGIN", "NGENA", "MELD AAN"),
        S.ContinueGoogle to row("Continue with Google", "Qhubeka nge-Google", "Gaan voort met Google"),
        S.NoAccount to row(
            "Don't have an account? Register",
            "Awunayo i-akhawunti? Bhalisa",
            "Het jy nie 'n rekening nie? Registreer"
        ),
        S.CreateAccount to row("Create account", "Dala i-akhawunti", "Skep rekening"),
        S.JoinCommunity to row(
            "Join your community on SafeGrid",
            "Joyina umphakathi wakho ku-SafeGrid",
            "Sluit by jou gemeenskap op SafeGrid aan"
        ),
        S.FullName to row("Full name", "Igama eliphelele", "Volle naam"),
        S.Back to row("Back", "Emuva", "Terug"),
        S.Home to row("Home", "Ikhaya", "Tuis"),
        S.Power to row("Power", "Amandla", "Krag"),
        S.Map to row("Map", "Imephu", "Kaart"),
        S.Sos to row("SOS", "SOS", "SOS"),
        S.Settings to row("Settings", "Izilungiselelo", "Instellings"),
        S.GoodMorning to row("Good morning,", "Sawubona ekuseni,", "Goeiemôre,"),
        S.GoodAfternoon to row("Good afternoon,", "Sawubona emini,", "Goeiemiddag,"),
        S.GoodEvening to row("Good evening,", "Sawubona kusihlwa,", "Goeienaand,"),
        S.PowerStatus to row("POWER STATUS", "ISIMO SAMANDLA", "KRAGSTATUS"),
        S.StageLabel to row("Loadshedding stage %d", "Isigaba sokucisha ugesi %d", "Beurtkrag fase %d"),
        S.NoLoadshedding to row("No loadshedding", "Akukho ukucisha ugesi", "Geen beurtkrag"),
        S.NextOutage to row("NEXT OUTAGE", "UKUCISHA OKULANDELAYO", "VOLGENDE ONDERBREKING"),
        S.ViewSchedule to row("View schedule", "Buka uhlelo", "Sien skedule"),
        S.CommunitySafety to row("COMMUNITY SAFETY", "UKUPHEPHA KOMPHAKATHI", "GEMEENSKAPVEILIGHEID"),
        S.IncidentsNearby to row("%d incidents nearby", "%d izehlakalo eduze", "%d voorvalle naby"),
        S.StayAware to row(
            "Stay aware of recent activity.",
            "Qaphela umsebenzi wakamuva.",
            "Bly bewus van onlangse aktiwiteit."
        ),
        S.ViewMap to row("View map", "Buka imephu", "Sien kaart"),
        S.Report to row("Report", "Bika", "Rapporteer"),
        S.Emergency to row("EMERGENCY", "ISIPHUTHUM", "NOODGEVAL"),
        S.SosButton to row("SOS EMERGENCY", "I-SOS YESIPHUTHUM", "SOS-NOODGEVAL"),
        S.GuardianPoints to row("Guardian points", "Amaphuzu omlondolozi", "Beskermpunte"),
        S.CommunityHelper to row("Community helper", "Umsizi womphakathi", "Gemeenskapshelper"),
        S.ScheduleTitle to row("Loadshedding schedule", "Uhlelo lokucisha ugesi", "Beurtkragskedule"),
        S.YourArea to row("YOUR AREA", "INDAWO YAKHO", "JOU AREA"),
        S.CurrentStage to row("Current stage: %d", "Isigaba samanje: %d", "Huidige fase: %d"),
        S.OfflineSchedule to row("OFFLINE SCHEDULE", "UHLELO OLUGCINIWE", "VANLYN-SKEDULE"),
        S.ShowingCached to row(
            "Saved in Room on this phone. It syncs when you reconnect.",
            "Kugcinwe ku-Room kule foni. Kuvumelaniswa uma uxhuma futhi.",
            "In Room op hierdie foon gestoor. Dit sinkroniseer as jy weer koppel."
        ),
        S.LastSynced to row("Last synced: %s", "Kugcine ukuvumelanisa: %s", "Laas gesinkroniseer: %s"),
        S.Refresh to row("Refresh from API", "Vuselela ku-API", "Verfris vanaf API"),
        S.ApiResponse to row("API response", "Impendulo ye-API", "API-antwoord"),
        S.CommunityMap to row("Community safety", "Ukuphepha komphakathi", "Gemeenskapsveiligheid"),
        S.ReportIncident to row("Report an incident", "Bika isehlo", "Rapporteer 'n voorval"),
        S.IncidentCategory to row("Incident category", "Isigaba sesehlo", "Voorvalkategorie"),
        S.Crime to row("Crime", "Ubugebengu", "Misdaad"),
        S.Suspicious to row("Suspicious", "Okusolisayo", "Verdagsaam"),
        S.Traffic to row("Traffic", "Ithrafikhi", "Verkeer"),
        S.Describe to row("Describe what happened", "Chaza okwenzekile", "Beskryf wat gebeur het"),
        S.AddPhoto to row("Add photo", "Engeza isithombe", "Voeg foto by"),
        S.PhotoAttached to row("Photo attached", "Isithombe sinamathiselwe", "Foto aangeheg"),
        S.LocationDetected to row("Location attached", "Indawo inamathiselwe", "Ligging aangeheg"),
        S.LocationHelp to row(
            "Your area or GPS position is stored with the report.",
            "Indawo yakho noma i-GPS igcinwa nombiko.",
            "Jou area of GPS-posisie word saam met die verslag gestoor."
        ),
        S.SubmitReport to row("SUBMIT REPORT", "THUMELA UMBIKO", "STUUR VERSLAG"),
        S.SosReady to row("EMERGENCY SOS", "I-SOS YESIPHUTHUM", "NOOD-SOS"),
        S.SosActivated to row("SOS ACTIVATED", "I-SOS IVULIWE", "SOS GEAKTIVEER"),
        S.SosHelp to row(
            "Press the button to alert your contacts and save your location.",
            "Cindezela inkinobho ukuze uxwayise oxhumana nabo futhi ugcine indawo yakho.",
            "Druk die knoppie om jou kontakte te waarsku en jou ligging te stoor."
        ),
        S.SosSent to row(
            "Your emergency contacts are being alerted.",
            "Oxhumana nabo besiphuthuma bayaziswa.",
            "Jou noodkontakte word gewaarsku."
        ),
        S.Contacts to row("Emergency contacts", "Oxhumana nabo besiphuthuma", "Noodkontakte"),
        S.Mom to row("Mom", "Umama", "Ma"),
        S.Dad to row("Dad", "Ubaba", "Pa"),
        S.Watch to row("Neighbourhood watch", "I-Neighbourhood Watch", "Buurtwag"),
        S.Language to row("Language", "Ulimi", "Taal"),
        S.Notifications to row("Notifications", "Izaziso", "Kennisgewings"),
        S.NotifSub to row(
            "Loadshedding and safety alerts",
            "Izexwayiso zokucisha ugesi nokuphepha",
            "Beurtkrag- en veiligheidswaarskuwings"
        ),
        S.Radius to row("Safety alert radius", "Ububanzi bezexwayiso", "Veiligheidswaarskuwingradius"),
        S.OfflineData to row("Offline data", "Idatha engaxhunyiwe", "Vanlyn data"),
        S.PendingSync to row("%d waiting to sync", "%d kulinde ukuvumelanisa", "%d wag om te sinkroniseer"),
        S.LogOut to row("LOG OUT", "PHUMA", "MELD AF"),
        S.CloudStatus to row("CLOUD DATA", "IDATHA YEFU", "WOLKDATA"),
        S.PasswordNotice to row(
            "Your password is hashed with SHA-256 on this phone. Only the hash is sent to Firebase Authentication over HTTPS.",
            "Iphasiwedi yakho i-hashiwe nge-SHA-256 kule foni. I-hash kuphela ethunyelwa ku-Firebase Authentication nge-HTTPS.",
            "Jou wagwoord word met SHA-256 op hierdie foon gehash. Slegs die hash word oor HTTPS na Firebase Authentication gestuur."
        ),
        S.HashPreview to row("Hash that will be stored", "I-hash ezogcinwa", "Hash wat gestoor sal word"),
        S.ErrorEmail to row(
            "Enter a valid email address.",
            "Faka ikheli le-imeyili elisebenzayo.",
            "Voer 'n geldige e-posadres in."
        ),
        S.ErrorPassword to row(
            "Password must be at least 6 characters.",
            "Iphasiwedi kumele okungenani ibe nezinhlamvu eziyisi-6.",
            "Wagwoord moet minstens 6 karakters wees."
        ),
        S.ErrorName to row("Enter your full name.", "Faka igama lakho eliphelele.", "Voer jou volle naam in."),
        S.ErrorDescription to row(
            "Describe the incident in at least 8 characters.",
            "Chaza isehlo okungenani ngezinhlamvu eziyisi-8.",
            "Beskryf die voorval in minstens 8 karakters."
        ),
        S.FirebaseMissing to row(
            "Add google-services.json, then rebuild, to turn on sign-in, Firestore, and photo storage.",
            "Engeza i-google-services.json bese wakha kabusha ukuze uvule ukungena, i-Firestore, kanye nesitoreji sezithombe.",
            "Voeg google-services.json by en bou weer om aanmelding, Firestore en fotoberging aan te skakel."
        ),
        S.SignedInAs to row("Signed in as", "Ungene njenge", "Aangemeld as"),
        S.AuthService to row(
            "Authentication service",
            "Isevisi yokuqinisekisa",
            "Verifikasiediens"
        ),
        S.NoSqlDatabase to row("NoSQL database", "Idatha ye-NoSQL", "NoSQL-databasis"),
        S.BlobStorage to row("Blob storage", "Isitoreji se-blob", "Blob-berging"),
        S.SyncNow to row("Sync now", "Vumelanisa manje", "Sinkroniseer nou"),
        S.SavedOffline to row(
            "Saved on this phone. It will sync when you are online.",
            "Kulondolozwe kule foni. Kuzovumelaniswa uma uku-inthanethi.",
            "Op hierdie foon gestoor. Dit sinkroniseer wanneer jy aanlyn is."
        ),
        S.SavedCloud to row(
            "Saved in Cloud Firestore.",
            "Kulondolozwe ku-Cloud Firestore.",
            "In Cloud Firestore gestoor."
        ),
        S.PhotoUploaded to row(
            "Photo uploaded to Firebase Storage.",
            "Isithombe silayishwe ku-Firebase Storage.",
            "Foto opgelaai na Firebase Storage."
        ),
        S.CancelSos to row("Cancel SOS", "Khansela i-SOS", "Kanselleer SOS"),
        S.SourceLive to row(
            "Source: live Eskom REST API",
            "Umthombo: i-API ye-Eskom ebukhoma",
            "Bron: regstreekse Eskom REST API"
        ),
        S.SourceCache to row(
            "Source: Room cache (offline)",
            "Umthombo: ikheshi le-Room (offline)",
            "Bron: Room-kas (vanlyn)"
        ),
        S.Area to row("Area", "Indawo", "Area"),
        S.Save to row("Save settings", "Londoloza izilungiselelo", "Stoor instellings"),
        S.ReportSent to row("Report submitted.", "Umbiko uthunyelwe.", "Verslag gestuur."),
        S.NoneScheduled to row("No outage scheduled", "Akukho ukucisha okuhleliwe", "Geen onderbreking geskeduleer"),
        S.NotSignedIn to row("Not signed in", "Awungenanga", "Nie aangemeld nie"),
        S.EmptyIncidents to row(
            "No incidents yet. Report the first one.",
            "Azikho izehlakalo okwamanje. Bika eyokuqala.",
            "Nog geen voorvalle nie. Rapporteer die eerste een."
        ),
        S.TestNotification to row("Send test alert", "Thumela isaziso sokuhlola", "Stuur toetswaarskuwing"),
        S.NotificationSent to row(
            "Alert sent. Check your notification shade.",
            "Isaziso sithunyelwe. Hlola izaziso zakho.",
            "Waarskuwing gestuur. Kyk na jou kennisgewings."
        ),
        S.GoogleUnavailable to row(
            "Google sign-in needs google-services.json with a Web client ID, plus your SHA-1 in Firebase.",
            "Ukungena nge-Google kudinga i-google-services.json ene-Web client ID, kanye ne-SHA-1 yakho ku-Firebase.",
            "Google-aanmelding benodig google-services.json met 'n Web-kliënt-ID, plus jou SHA-1 in Firebase."
        ),
        S.NotSynced to row("Not synced yet", "Akukavumelaniswa", "Nog nie gesinkroniseer nie"),
        S.ApiLoading to row("Calling the Eskom API…", "Kubizwa i-API ye-Eskom…", "Eskom API word geroep…"),
        S.ApiFailed to row("API call failed", "Ukubiza i-API kuhlulekile", "API-oproep het misluk"),
        S.CloudReady to row("Firebase project connected", "Iphrojekthi ye-Firebase ixhunyiwe", "Firebase-projek gekoppel"),
        S.CloudNotReady to row(
            "Firebase project not connected",
            "Iphrojekthi ye-Firebase ayixhunyiwe",
            "Firebase-projek nie gekoppel nie"
        ),
        S.Uid to row("User id", "I-id yomsebenzisi", "Gebruiker-id"),
        S.Provider to row("Sign-in provider", "Umhlinzeki wokungena", "Aanmeldverskaffer"),
        S.LastWrite to row("Last cloud write", "Ukubhala kwakamuva efwini", "Laaste wolkskryf"),
        S.PleaseWait to row("Please wait…", "Lindela…", "Wag asseblief…"),
        S.Today to row("Today", "Namuhla", "Vandag"),
        S.Tomorrow to row("Tomorrow", "Kusasa", "Môre"),
        S.Day3 to row("Day 3", "Usuku 3", "Dag 3"),
        S.Day4 to row("Day 4", "Usuku 4", "Dag 4"),
        S.EstimatedNote to row(
            "Times are estimated from the live national stage and stored offline.",
            "Izikhathi zilinganiswa esigabeni sikazwelonke bese zigcinwa ungaxhunyiwe.",
            "Tye word uit die regstreekse nasionale fase beraam en vanlyn gestoor."
        ),
        S.SettingsSaved to row("Settings saved.", "Izilungiselelo zilondoloziwe.", "Instellings gestoor."),
        S.SosRecorded to row(
            "SOS saved to the alerts collection.",
            "I-SOS igciniwe eqoqweni lezexwayiso.",
            "SOS in die waarskuwingsversameling gestoor."
        ),
        S.ErrorEmailUsed to row(
            "That email is already registered. Log in instead.",
            "Le imeyili isivele ibhalisiwe. Ngena kunalokho.",
            "Daardie e-pos is reeds geregistreer. Meld eerder aan."
        ),
        S.ErrorBadLogin to row(
            "Email or password is incorrect.",
            "I-imeyili noma iphasiwedi ayilungile.",
            "E-pos of wagwoord is verkeerd."
        ),
        S.ErrorNetwork to row(
            "Network error. Check your connection and try again.",
            "Iphutha lenethiwekhi. Hlola uxhumano bese uzama futhi.",
            "Netwerkfout. Kontroleer jou verbinding en probeer weer."
        ),
        S.ErrorGeneric to row(
            "Something went wrong. Please try again.",
            "Kukhona okungahambanga kahle. Zama futhi.",
            "Iets het verkeerd geloop. Probeer asseblief weer."
        ),
        S.PasswordStorage to row(
            "Passwords are stored as a SHA-256 hash in Firebase Authentication. The original password is not kept.",
            "Amaphasiwedi agcinwa njenge-hash ye-SHA-256 ku-Firebase Authentication. Iphasiwedi yoqobo ayigcinwa.",
            "Wagwoorde word as 'n SHA-256-hash in Firebase Authentication gestoor. Die oorspronklike wagwoord word nie gehou nie."
        ),
        S.UsersCollection to row("Firestore: users/{uid}", "Firestore: users/{uid}", "Firestore: users/{uid}"),
        S.IncidentsCollection to row(
            "Firestore: incidents/{id}",
            "Firestore: incidents/{id}",
            "Firestore: incidents/{id}"
        ),
        S.AlertsCollection to row("Firestore: alerts/{id}", "Firestore: alerts/{id}", "Firestore: alerts/{id}"),
        S.Synced to row("Synced", "Kuvumelanisiwe", "Gesinkroniseer"),
        S.Pending to row("Waiting to sync", "Kulinde ukuvumelanisa", "Wag om te sinkroniseer"),
        S.WorkingOffline to row("Working offline", "Kusebenza ngaphandle koxhumano", "Werk vanlyn"),
        S.Connected to row("Cloud connected", "Ifu ixhunyiwe", "Wolk gekoppel"),
        S.StageUnknown to row("Stage unknown", "Isigaba asaziwa", "Fase onbekend"),
        S.ConfigureSteps to row(
            "Open Settings to see the authentication service, database, and blob storage.",
            "Vula Izilungiselelo ukuze ubone isevisi yokuqinisekisa, idatha, kanye nesitoreji se-blob.",
            "Maak Instellings oop om die verifikasiediens, databasis en blob-berging te sien."
        ),
        S.RadiusValue to row("%d km", "%d km", "%d km"),
        S.PointsValue to row("%d points • %s", "%d amaphuzu • %s", "%d punte • %s"),
        S.NoPhotoYet to row("No photo uploaded yet", "Asikho isithombe okwamanje", "Nog geen foto opgelaai nie"),
        S.ProviderPassword to row("Email and password", "I-imeyili nephasiwedi", "E-pos en wagwoord"),
        S.ProviderGoogle to row("Google single sign-on", "Ukungena okukodwa kwe-Google", "Google-enkelaanmelding")
    )

    init {
        S.entries.forEach { key ->
            val values = table[key] ?: error("Missing translations for $key")
            Lang.entries.forEach { lang ->
                check(lang in values) { "Missing $lang for $key" }
            }
        }
    }

    fun get(lang: Lang, key: S): String = table.getValue(key).getValue(lang)

    fun format(lang: Lang, key: S, vararg args: Any): String {
        return String.format(java.util.Locale.US, get(lang, key), *args)
    }
}
