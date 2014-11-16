## Design document for the beacons Android app
***
For general information about the features and goals of this app, read the [README](https://github.com/sander-m/beacons-android/blob/master/README.md)

### APIs, frameworks and related documentation
* Android Studio
* Android SDK (API 18 and higher to support BLE)
* [Retrofit](http://square.github.io/retrofit/), a very nice REST client library.
* [android-beacon-library](https://github.com/AltBeacon/android-beacon-library), for scanning beacons.
* REST API from Minor Programmeren
* Android [Material design](https://developer.android.com/design/material/index.html)

### Coding style
* According to [Google Android Style Guidelines](http://source.android.com/source/code-style.html)

### Code design
Note: this may change at any time during development.
* Activities
  * `MainActivity`  
  * `SettingsActivity`  
* Fragments
  * `AssistantListFragment`
  * `StudentListFragment`
  * `BeaconListFragment`
  * `HelpFragment`
  * `ChooseCourseFragment`
  * `LoginFragment`
* Adapters
  * `AssistantListAdapter`
  * `StudentListAdapter`
  * `BeaconListAdapter`
* Other classes
  * `BeaconTracker`
  * `BeaconApi`
  * `BeaconApiClient`
  * `CancelableCallback`

### UI
Select course  
<img src="https://github.com/sander-m/beacons-android/blob/master/doc/screenshots/screenshot_select_course.png" alt"Login" width="300px">  

Enter corresponding pairing code  
<img src="https://github.com/sander-m/beacons-android/blob/master/doc/screenshots/screenshot_pairing_code.png" alt"Login" width="300px"> 

Navigation menu  
<img src="https://github.com/sander-m/beacons-android/blob/master/doc/screenshots/screenshot_nav_drawer.png" alt"Navigation menu/drawer" width="300px">  

Scan beacons  
<img src="https://github.com/sander-m/beacons-android/blob/master/doc/screenshots/screenshot_scan_beacons.png" alt"Scan beacons" width="300px">  

Settings  
<img src="https://github.com/sander-m/beacons-android/blob/master/doc/screenshots/screenshot_settings.png" alt"Settings" width="300px">  
