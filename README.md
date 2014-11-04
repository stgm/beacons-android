beacons-android
===============

Android app that uses Estimote beacons for teaching assistance.
Minimum required API level is 18 (Android 4.3+). The app requires Bluetooth Low Energy support.
This project is built with Android Studio

##### Android Studio:
https://developer.android.com/sdk/installing/studio.html

##### Estimote SDK for Android:
https://github.com/Estimote/Android-SDK

### Het concept
De app is gebaseerd op een voorstel van de Minor Programmeren om een app te maken die het makkelijker maakt voor studenten om assistentie te krijgen bij practicum en waarbij de aanwezigheid van studenten kan worden geregistreerd. De app scant naar iBeacons en stuurt de beaconinformatie door naar een server. De app is door zowel assistenten als studenten te gebruiken, met een andere feature-set.

De app zou grofweg de volgende functionaliteiten moeten hebben:
* Een **Login** scherm om je aan te melden bij de minor. Elke student krijgt een unieke identifier toegewezen.
* Een **overzichtscherm** met een lijst van assistenten en per assistent de dichtsbijzijnde beacon. Zo kunnen studenten zien waar ze een assistent kunnen opzoeken. 
* Een **help** scherm, waarbij de student een oproep kan doen om assistentie te krijgen tijdens een practicum. Hier kan de student ook een specifieke vraag of probleembeschrijving typen Deze oproep wordt vervolgens door de server verwerkt en assistenten krijgen hier dan een melding van op hun telefoon via een push notification.

### Features voor assistenten:
* Een **studentenoverzicht**: lijst van alle studenten die aanwezig zijn en de beacon die het dichts bij de student is. Ook staat hier de tijd geregistreerd waarop de student is binnengekomen.
* Een **vragenoverzicht**, een lijst met alle vragen gesteld door studenten, waarbij de vragen die behandeld zijn afgestreept kunnen worden zodat door de assistenten makkelijk bij te houden is welke vragen nog behandeld moeten worden en welke vragen in het verleden gesteld zijn.
