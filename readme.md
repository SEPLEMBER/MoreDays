# moreDays

## Decisions
After playing with flows, repositories, livedata and reading about viewmodel life cycles, the following architecture seems to be the best for this app:
- daos return flows for lists and simple objects for one shot read 
  - an observer attached to such a flow, will listen to all changes of all involved tables of the corresponding query, hence observers will be attached to queries adressing one table only
- as daos are interfaces already and the data will be stored on the phone only, there is no need for repository classes -- they are useful, if the data comes from an api AND room
- even if a device is rotated and ondestroy of an activity/fragment is called, a viewmodel remains, hence use livedata in viewmodels, let them be updated
- most viewmodels of this app will need constructor parameters, therefore we need  viewmodel factory classes, alternatively we could use hilt (still thinking about it). However looking at the changes https://github.com/android/sunflower/commit/cd57be5ad686aaad30b3e560d9cd858c952ff5bf it seems much more to do than to gain ...


## ToDos
- bugs
  - trophy nicht in db eingetragen!
  - change date --> circles updated, goals not! add activity, no effect!
  - challenge progress not updated in home view!
  - journal: datum ändern auf gestern (Calender nicht sichtbar)
- ux
  - Schrift zu groß, für alle definieren, insb. journal
  - Onboarding nicht alles lesbar, Bilder 30% der Größe
- Tests entkoppeln
- Setting Startseite umsetzen/prüfen

- optional
  - rename/organize laoyut
  - for schleifen nochmal durchgehen, wo vl. map o.ä. verwendbar?
- Import/Export
  - Permissions: https://github.com/googlesamples/easypermissions
  - zu importierende Ziele etc. zunächst in Map sammeln und und key als uid für activities verwenden, später nach bulk insert update 
- Verschlüsseln
  - https://github.com/hanjoongcho/aaf-easydiary
  - biometrics https://developer.android.com/jetpack/androidx/releases/biometric#1.0.1

## Vision

## naming convention
- ids in layouts
  - typeName, e.g. textViewPoints, imageButtonColor
  - fab (floatingActionButton)
  - better https://jeroenmols.com/blog/2016/03/07/resourcenaming/
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
- implemented features
- added splash as described in https://www.bignerdranch.com/blog/splash-screens-the-right-way/
- added leakcanary to debugImplementation and run it, see https://github.com/square/leakcanary

## Lessons learned
- Dependency Injection (DI): a class should not create the dependencies, it needs, those dependencies should be provided through the constructor
  - a more generic approach should use dagger and hilt, see https://blog.mindorks.com/dagger-hilt-tutorial
- ViewModelProviders.Factory: gives a new instance if it is needed and only if it is needed, hence use it; if the ViewModel has a parameter in the constructor (see DI above), implement your own factory
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
### Binding and ViewModels
- https://developer.android.com/topic/libraries/view-binding
- ***needs a generic layout tag in layout files***, see https://developer.android.com/topic/libraries/data-binding/expressions
- the viewModel should not know the context, hence it provides the data without internationalization, however this may be done in the view, e.g.
```
android:text="@{@plurals/watering_next(viewModel.wateringInterval, viewModel.wateringInterval)}"
<!-- or -->
android:text="@{journalViewModel.getFormattedDate(@string/date_ymd)}"
```
- binding of edittext to number is a bit tricky, workaround use one way binding, see https://stackoverflow.com/questions/38982604/two-way-databinding-in-edittext
  - `` android:text="@={`` + goalViewModel.goal.points}" `
  - do not forget to fetch the value before saving
  - better use number picker, as it is supported for two way binding, see https://developer.android.com/topic/libraries/data-binding/two-way#java
  - do not forget the = for two way binding, e.g. ```android:text="@={goalViewModel.goal.name}" ```
  - buttons/events: ```android:onClick="@{() -> viewModel.onCounterButtonPressed()}"```
  - good article https://www.bignerdranch.com/blog/two-way-data-binding-on-android-observing-your-view-with-xml/
  - spinner is just a hassle to bind to: after binding there is an autoselection of the first item in the list, hence any preselection needs a hack
    - https://code.luasoftware.com/tutorials/android/android-two-way-data-binding-with-spinner/ 
    - https://wamae.medium.com/using-android-livedata-in-spinners-room-428de4238847 
    - https://stackoverflow.com/questions/2562248/how-to-keep-onitemselected-from-firing-off-on-a-newly-instantiated-spinner
  - https://github.com/android/databinding-samples/tree/main/TwoWaySample
- ViewModels should not reference view/Android stuff and ViewModel shouldn’t be referenced in any object that can outlive the activity/fragment
### Navigation
- up button https://developer.android.com/codelabs/kotlin-android-training-add-navigation/index.html#7
- conditional https://developer.android.com/codelabs/kotlin-android-training-add-navigation/index.html#5
- handle back button in MainActivity
- advanced navigation see https://github.com/android/architecture-components-samples/tree/main/NavigationAdvancedSample/app/src/main/java/com/example/android/navigationadvancedsample
- current problem: fragments and corresponding viewmodels are recreated and recreated, whenever one selects an item in the bottom navigation; the navigationadvancedsample offers a workaround, I wait for the update of navigation component ;-)
- if you pass parameters in the navigation graph they must implement Parcelable or Serializable

### Room & coroutines
You can use the suspend keyword to make your DAO queries asynchronous using Kotlin coroutines. Suspend function is a function that could be started, paused, and resume. One of the most important points to remember about the suspend functions is that they are only allowed to be called from a coroutine or another suspend function. The official documentation says that coroutines are lightweight threads. By lightweight, it means that creating coroutines doesn’t allocate new threads. Instead, they use predefined thread pools and smart scheduling. Use launch to start a new coroutine as fire & forget; use async to return -- see https://developer.android.com/kotlin/coroutines-adv
- use flow at repository and dao level for observable lists, use simple nullable data (Type?) for one shot async read (suspend), e.g. `suspend fun getById(id: Long): Goal?`
- use LieveData or MutableLiveData at viewmodel level -- in case of MutableLiveData use getters/setters
- Prepopulate Room Database: https://developer.android.com/training/data-storage/room/prepopulate
  - start with an empty db of the current schema
  - add data using the app as wanted
  - copy database: View > Tool Windows > Device File Explorer; make sure, you download the complete databases folder (found at /data/data/de.wuapps.moredays/databases) 
    - edit/view db use https://sqlitebrowser.org/dl/ or use https://developer.android.com/studio/inspect/database?utm_source=android-studio
    - create assets folder (see https://www.geeksforgeeks.org/assets-folder-in-android/ )
      - open project in Android mode (not project)
      -   app > right-click > New > Folder > Asset Folder 
    - https://developer.android.com/training/data-storage/room/prepopulate 
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
    - setValue for MainThread
    - postValue for BackgroundThread

### Dagger & Hilt -- Dependency injection 
Less boiler plate code, better testing, e.g. no ViewModelFactories only because there are additional parameters.
- https://developer.android.com/training/dependency-injection
- codelab https://developer.android.com/codelabs/android-hilt?hl=nb#1
- from view model factories to di with hilt: https://medium.com/mobile-app-development-publication/injecting-viewmodel-with-dagger-hilt-54ca2e433865
- very good summary of several features of hilt https://youtu.be/B56oV3IHMxg
- not used in this app, as we do not use any web services and hence do not need repositories for abstraction; dao and hilt do not work together right now

## Tests
Usually, you write one assert per test. However the test preparation is relativ complex, hence I decided to integrate all tests of one method into one test.

## Links
- android developer
  - https://developer.android.com/guide
  - https://developer.android.com/samples
- list of libraries
  - https://github.com/wasabeef/awesome-android-ui
- UX: fonts, colors...
  - https://uxplanet.org/ui-design-resources-to-kickstart-your-project-ce2bea7a3740
  - https://undraw.co/illustrations oder https://www.drawkit.io/
  - https://blog.spoongraphics.co.uk/articles/25-classic-fonts-that-will-last-a-whole-design-career

## Thanks 