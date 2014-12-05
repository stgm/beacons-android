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
  * `BaseActivity` 
  * `MainActivity`  
  * `SettingsActivity`  
* Fragments
  * `BaseFragment`  
  * `AssistantListFragment`
  * `StudentListFragment`
  * `StudentDetailFragment`
  * `BeaconListFragment`
  * `HelpFragment`
  * `SelectCourseFragment`
  * `LoginFragment`
  * `LoginManagementFragment`
  * `NavigationDrawerFragment`
* Adapters
  * `AssistantListAdapter`
  * `StudentListAdapter`
  * `BeaconListAdapter`
  * `CourseListAdapter`
  * `HelpCourseListAdapter`
  * `LoginListAdapter`
* Api
  * `ApiClient`
  * `CancelableCallback`
  * `BeaconApi`
  * `LoginApi`
  * `MobileApi`
* Other classes
  * `BeaconTracker`
  * `LoginManager`
  * `LoginEntry`
  * `BeaconsApplication`

### UI
Select course  
<img src="https://github.com/sander-m/beacons-android/blob/master/doc/screenshots/screenshot_add_course.png" alt"Login" width="300px">  

Enter corresponding pairing code    
<img src="https://github.com/sander-m/beacons-android/blob/master/doc/screenshots/screenshot_pairing_code.png" alt"Login" width="300px">

Navigation menu    
<img src="https://github.com/sander-m/beacons-android/blob/master/doc/screenshots/screenshot_navigation.png" alt"Navigation menu/drawer" width="300px">

Scan beacons    
<img src="https://github.com/sander-m/beacons-android/blob/master/doc/screenshots/screenshot_beacons.png" alt"Scan beacons" width="300px">  

Student list  
<img src="https://github.com/sander-m/beacons-android/blob/master/doc/screenshots/screenshot_student_list.png" alt"Student list" width="300px">  

Student details screen  
<img src="https://github.com/sander-m/beacons-android/blob/master/doc/screenshots/screenshot_student_detail.png" alt"Student detail" width="300px">  

Ask help   
<img src="https://github.com/sander-m/beacons-android/blob/master/doc/screenshots/screenshot_ask_help.png" alt"Student detail" width="300px">  

Ask help   
<img src="https://github.com/sander-m/beacons-android/blob/master/doc/screenshots/screenshot_login_management.png" alt"Student detail" width="300px">  

Settings    
<img src="https://github.com/sander-m/beacons-android/blob/master/doc/screenshots/screenshot_settings.png" alt"Settings" width="300px">  
