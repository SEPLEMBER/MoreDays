# moreDays

## Decisions
After playing with flows, repositories, livedata and reading about viewmodel life cycles, the following architecture seems to be the best for this app:
- daos return flows for lists and simple objects for one shot read 
  - an observer attached to such a flow, will listen to all changes of all involved tables of the corresponding query, hence observers will be attached to queries adressing one table only
- as daos are interfaces already and the data will be stored on the phone only, there is no need for repository classes -- they are useful, if the data comes from an api AND room
- even if a device is rotated and ondestroy of an activity/fragment is called, a viewmodel remains, hence use livedata in viewmodels, let them be updated
- most viewmodels of this app will need constructor parameters, therefore we need  viewmodel factory classes


## ToDos
- maybe use https://github.com/greenrobot/EventBus
- Tests hinzufügen https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-basics#0 und https://speakerdeck.com/alexzhukovich/writing-robust-android-ui-tests 
- DB-Tests
  - https://speakerdeck.com/alexzhukovich/let-us-play-in-room-with-kotlin?slide=49
- Hilfe mit https://github.com/KeepSafe/TapTargetView
- für Onboarding auswählen: https://undraw.co/illustrations oder https://www.drawkit.io/
- Hintergrund vl. von https://www.svgbackgrounds.com/#flat-mountains
- instead of preferences use https://developer.android.com/jetpack/androidx/releases/datastore#version_100_2 see https://developer.android.com/codelabs/android-preferences-datastore?hl=zh-cn#0
- https://github.com/googlesamples/easypermissions
- permission to export pictures ... see https://github.com/TeamAmaze/AmazeFileManager
- implement trophy made using https://github.com/gabriel-TheCode/AestheticDialogs
- lock & export/import see https://github.com/hanjoongcho/aaf-easydiary
- permissions: dexter https://github.com/karumi/dexter
- in case push notifications are wanted https://github.com/OneSignal/OneSignal-Android-SDK
- add credentials with https://github.com/AllanWang/KAU/tree/dev/about#readme
- journal image picker https://android-arsenal.com/details/1/8021 or https://android-arsenal.com/details/1/8208 (with crop) or https://android-arsenal.com/details/1/7697
- use https://github.com/DanielMartinus/Konfetti to show trophy
- onboarding see https://thesimplycoder.com/346/android-intro-slider-and-splash-screen-tutorial/
- tutorial date picker https://thesimplycoder.com/226/android-date-picker-kotlin-tutorial/
- glide with circle crop and recycler view (-->journal) https://thesimplycoder.com/114/android-recyclerview-tutorial-using-kotlin/
- biometrics https://developer.android.com/jetpack/androidx/releases/biometric#1.0.1
- scaleentry with latest value (entries as live data in viewmodel and observer -> set currentvalue in viewmodel)
- select nice layout of https://codecanyon.net
- maybe circle animation similar to https://github.com/GIGAMOLE/ArcProgressStackView/blob/master/arcprogressstackview/src/main/java/devlight/io/library/ArcProgressStackView.java
- where to store current date and similar data: activity scope? or nav as in https://developer.android.com/guide/navigation/navigation-programmatic#share_ui-related_data_between_destinations_with_viewmodel
- use curved bottom navigation https://github.com/oneHamidreza/MeowBottomNavigation
- add animation see https://github.com/AllanWang/KAU/blob/master/core/src/main/kotlin/ca/allanwang/kau/utils/AnimUtils.kt

## Vision

## naming convention
- ids in layouts
  - typeName, e.g. textViewPoints, imageButtonColor
  - fab (floatingActionButton)
- strings
  - lbl_
  - 
## Walkthrough
- new kotlin project based on template bottom navigation
- added fragments (template with ViewModel): Activity, Challenge, Goal, Journal, Trophy; und ActivityList, TrophyList... and adapter classes and packages (organize by feature)
- added dependencies
- code is similar to 
  - https://github.com/google-developer-training/android-kotlin-fundamentals-apps/tree/master/TrackMySleepQualityFinal
  - https://github.com/android/sunflower 
- added view binding https://developer.android.com/topic/libraries/view-binding
- added dao/entity classes
- added components to navigation graph
- adapted bottom navigation (menu and mainactivity) 
- added libraries
- downloaded fonts from https://fonts.google.com/
- implement adapters https://developer.android.com/guide/topics/ui/layout/recyclerview, drag&drop: 


## Lessons learned
- Dependency Injection (DI): a class should not create the dependencies, it needs, those dependencies should be provided through the constructor
  - a more generic approach should use dagger and hilt, see https://blog.mindorks.com/dagger-hilt-tutorial
- ViewModelProviders.Factory: gives a new instance if it is needed and only if it is needed, hence use it; if the ViewModel has a parameter in the constructor (see DI above), implement your own factory
- to handle drawables use ImageDecoder
- custom views see https://developer.android.com/codelabs/advanced-andoid-kotlin-training-custom-views and https://developer.android.com/training/custom-views/create-view

## Kotlin
- https://kotlinlang.org/docs/reference
- Java to Kotlin converter: https://try.kotlinlang.org/ 
- it : in lambdas with one parameter only, this may be ommitted and is called it 
- Int? : Int with possible null value
- Any : datatype
- open class : may be extended
- data class : simple data class
- scope functions let, run, with, apply, and also: https://kotlinlang.org/docs/reference/scope-functions.html  
  - apply and also return the context object.
  - let, run, and with return the lambda result.

- code examples
- The primary constructor cannot contain any code. Initialization code can be placed in initializer blocks, which are prefixed with the init keyword.
- companion object : equivalent of static methods 
```
fun getStringLength(obj: Any): Int? {
    if (obj !is String) return null

    // `obj` is automatically cast to `String` in this branch
    return obj.length
}
```
```
fun describe(obj: Any): String =
    when (obj) {
        1          -> "One"
        "Hello"    -> "Greeting"
        is Long    -> "Long"
        !is String -> "Not a string"
        else       -> "Unknown"
    }
```
```
for (x in 1..10 step 2) {
    print(x)
for (item in items) {
    println(item)
}

val s = "abc"
println("$s.length is ${s.length}") // prints "abc.length is 3"

val positives = list.filter { x -> x > 0 }

//extension functions
fun String.spaceToCamelCase() { ... }

//singleton
object Resource {

// not null shorthand
files?.size
// excecute if not null
value?.let {
```

## Architecture components
- https://developer.android.com/jetpack/guide
- https://developer.android.com/topic/libraries/architecture/index.html
- https://github.com/android/architecture-components-samples
### Fragments
- getSupportFragmentManager is associated with an activity. Consider it as a FragmentManager for your activity.
- getChildFragmentManager is associated with fragments
```
getSupportFragmentManager().beginTransaction()
                           .add(detailFragment, "detail")
                           // Add this transaction to the back stack
                           .addToBackStack(null)
                           .commit();
```
### Binding
- https://developer.android.com/topic/libraries/view-binding
- ***needs a generic layout tag in layout files***, see https://developer.android.com/topic/libraries/data-binding/expressions
- binding of edittext to number is a bit tricky, workaround use one way binding, see https://stackoverflow.com/questions/38982604/two-way-databinding-in-edittext
  - `` android:text="@={`` + goalViewModel.goal.points}" `
  - do not forget to fetch the value before saving
  - better use number picker, as it is supported for two way binding, see https://developer.android.com/topic/libraries/data-binding/two-way#java
  - do not forget the = for two way binding, e.g. ```android:text="@={goalViewModel.goal.name}" ```
  - buttons/events: ```android:onClick="@{() -> viewModel.onCounterButtonPressed()}"```
  - spinner is just a hassle to bind to: after binding there is an autoselection of the first item in the list, hence any preselection needs a hack
    - https://code.luasoftware.com/tutorials/android/android-two-way-data-binding-with-spinner/ 
    - https://wamae.medium.com/using-android-livedata-in-spinners-room-428de4238847 
    - https://stackoverflow.com/questions/2562248/how-to-keep-onitemselected-from-firing-off-on-a-newly-instantiated-spinner

### Navigation
- up button https://developer.android.com/codelabs/kotlin-android-training-add-navigation/index.html#7
- conditional https://developer.android.com/codelabs/kotlin-android-training-add-navigation/index.html#5
- handle back button in MainActivity
- advanced navigation see https://github.com/android/architecture-components-samples/tree/main/NavigationAdvancedSample/app/src/main/java/com/example/android/navigationadvancedsample

### Room & coroutines
You can use the suspend keyword to make your DAO queries asynchronous using Kotlin coroutines. Suspend function is a function that could be started, paused, and resume. One of the most important points to remember about the suspend functions is that they are only allowed to be called from a coroutine or another suspend function. The official documentation says that coroutines are lightweight threads. By lightweight, it means that creating coroutines doesn’t allocate new threads. Instead, they use predefined thread pools and smart scheduling. Use launch to start a new coroutine as fire & forget; use async to return -- see https://developer.android.com/kotlin/coroutines-adv
- use flow at repository and dao level for observable lists, use simple nullable data (Type?) for one shot async read (suspend), e.g. `suspend fun getById(id: Long): Goal?`
- use LieveData or MutableLiveData at viewmodel level
Prepopulate Room Database: https://developer.android.com/training/data-storage/room/prepopulate
- hint to implement runOnUIThread (see https://github.com/AllanWang/KAU/blob/master/core/src/main/kotlin/ca/allanwang/kau/utils/ContextUtils.kt)
```
/**
 * Created by Allan Wang on 2017-06-03.
 */
fun Context.runOnUiThread(f: Context.() -> Unit) {
    if (ContextHelper.looper === Looper.myLooper()) f() else ContextHelper.handler.post { f() }
}
```

#### Live Data vs. Flow
- see article https://medium.com/android-dev-hacks/exploring-livedata-and-kotlin-flow-7c8d8e706324
  - Livedata is used to observe data without having any hazel to handle lifecycle problems. Whereas Kotlin flow is used for continuous data integration and it also simplified the asynchronous programming.
  - Live data is an observable data holder class, Live data is lifecycle aware
  - use setvalue/postvalue to notify observers of changed value

### Dagger & Hilt -- Dependency injection 
Less boiler plate code, better testing, e.g. no ViewModelFactories only because there are additional parameters.
- https://developer.android.com/training/dependency-injection
- codelab https://developer.android.com/codelabs/android-hilt?hl=nb#1
- from view model factories to di with hilt: https://medium.com/mobile-app-development-publication/injecting-viewmodel-with-dagger-hilt-54ca2e433865
- very good summary of several features of hilt https://youtu.be/B56oV3IHMxg

## Links
- list of libraries
  - https://github.com/wasabeef/awesome-android-ui
- UX: fonts, colors...
  - https://uxplanet.org/ui-design-resources-to-kickstart-your-project-ce2bea7a3740

## Thanks 