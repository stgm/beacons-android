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
De app is gebaseerd op een voorstel van de Minor Programmeren om een app te maken die het makkelijker maakt voor studenten om assistentie te krijgen bij practicum en waarbij de aanwezigheid van studenten kan worden getracked. De location tracking moet plaatsvinden in het gebouw en in een kleine range, waar bluetooth beacons geschikt voor zijn en GPS bijvoorbeeld niet.

De app zou grofweg de volgende functionaliteiten moeten hebben:
* Een Login scherm om je aan te melden bij de minor. Elke student krijgt een unieke identifier toegewezen.
* Een overzichtscherm waarbij een lijst van assistenten, waar een benadering van de locatie kan worden weergegeven.
* Een 'help' scherm, waarbij de student een oproep kan doen om assistentie te krijgen tijdens een practicum. Assistenten krijgen hier dan een melding van op hun telefoon.
* In het 'assistenten' deel van de app kan een assistent zien wie er wel/niet aanwezig is.
* Todo: eventuele andere nuttige features
