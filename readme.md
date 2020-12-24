# moreDays

## Decisions


## ToDos
- genau prüfen, welche Daten beobachtet werden müssen: https://developer.android.com/codelabs/android-room-with-a-view-kotlin#6
- Tests hinzufügen https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-basics#0 und https://speakerdeck.com/alexzhukovich/writing-robust-android-ui-tests 
- DB-Tests
  - https://speakerdeck.com/alexzhukovich/let-us-play-in-room-with-kotlin?slide=49
- Hilfe mit https://github.com/KeepSafe/TapTargetView
- für Onboarding auswählen: https://undraw.co/illustrations oder https://www.drawkit.io/
- Montessori-Zahlen erstellen aus https://www.svgrepo.com/vectors/1/
- Hintergrund vl. von https://www.svgbackgrounds.com/#flat-mountains
- instead of preferences use https://developer.android.com/jetpack/androidx/releases/datastore#version_100_2 see https://developer.android.com/codelabs/android-preferences-datastore?hl=zh-cn#0
- maybe integrate https://github.com/jaredrummler/Cyanea to let the user define the colors by themselves
- https://github.com/googlesamples/easypermissions
- https://github.com/johncarl81/parceler
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
- added view binding https://developer.android.com/topic/libraries/view-binding
- added dao/entity classes similar to 
  - https://github.com/google-developer-training/android-kotlin-fundamentals-apps/tree/master/TrackMySleepQualityFinal
  - https://github.com/android/sunflower 
- einige Datenverbindungsklassen (z.B.) erstellt, vgl. https://itnext.io/how-to-create-m-n-relationship-with-room-and-kotlin-ddbdebf0ee38
```
class RecipeDayPair {
        @Embedded(prefix = "recipe_")
        lateinit var recipe : Recipe

        @Embedded
        var day : Day? = null
    }
```
- added components to navigation graph
- adapted bottom navigation (menu and mainactivity) 
- added libraries
- downloaded fonts from https://fonts.google.com/
- implement adapters https://developer.android.com/guide/topics/ui/layout/recyclerview, drag&drop: 
- added BaseViewModel and let all view models extend this model

## Kotlin
- https://kotlinlang.org/docs/reference
- Java to Kotlin converter: https://try.kotlinlang.org/ 
- it : in lambdas with one parameter only, this may be ommitted and is called it 
- Int? : Int with possible null value
- Any : datatype
- open class : may be extended
- data class : simple data class
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
- needs a generic layout tag in layout files, see https://developer.android.com/topic/libraries/data-binding/expressions
- binding of edittext to number is a bit tricky, workaround use one way binding, see https://stackoverflow.com/questions/38982604/two-way-databinding-in-edittext
  - `` android:text="@={`` + goalViewModel.goal.points}" `
  - do not forget to fetch the value before saving
  - better use number picker, as it is supported for two way binding, see https://developer.android.com/topic/libraries/data-binding/two-way#java
  - be careful with checkboxes -- better do not invert them
### Navigation
- up button https://developer.android.com/codelabs/kotlin-android-training-add-navigation/index.html#7
- conditional https://developer.android.com/codelabs/kotlin-android-training-add-navigation/index.html#5

### Room & coroutines
You can use the suspend keyword to make your DAO queries asynchronous using Kotlin coroutines. Suspend function is a function that could be started, paused, and resume. One of the most important points to remember about the suspend functions is that they are only allowed to be called from a coroutine or another suspend function. The official documentation says that coroutines are lightweight threads. By lightweight, it means that creating coroutines doesn’t allocate new threads. Instead, they use predefined thread pools and smart scheduling. Use launch to start a new coroutine as fire & forget; use async to return -- see https://developer.android.com/kotlin/coroutines-adv
- use flow at repository and dao level for observable lists, use simple nullable data (Type?) for one shot async read (suspend), e.g. `suspend fun getById(id: Long): Goal?`
- use LieveData or MutableLiveData at viewmodel level
#### Live Data vs. Flow
- see article https://medium.com/android-dev-hacks/exploring-livedata-and-kotlin-flow-7c8d8e706324
  - Livedata is used to observe data without having any hazel to handle lifecycle problems. Whereas Kotlin flow is used for continuous data integration and it also simplified the asynchronous programming.
  - Live data is an observable data holder class, Live data is lifecycle aware

## Links
- list of libraries
  - https://github.com/wasabeef/awesome-android-ui
- UX: fonts, colors...
  - https://uxplanet.org/ui-design-resources-to-kickstart-your-project-ce2bea7a3740

## Thanks 