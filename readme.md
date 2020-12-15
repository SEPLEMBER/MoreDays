# moreDays

## ToDos
- genau prüfen, welche Daten beobachtet werden müssen: https://developer.android.com/codelabs/android-room-with-a-view-kotlin#6
- Tests hinzufügen https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-basics#0
## Vision

## Walkthrough
- Neues Kotlin-Projekt auf Basis der Bottom-Navigation angelegt
- Folgende Fragments auf Basis des Templates Fragment mit ViewModel hinzugefügt: Activity, Challenge, Goal, Journal, Trophy; und ActivityList, TrophyList... und die zugehörigen Adapterklassen und Layouts
- mit https://try.kotlinlang.org/ die DB-Java-Dateien nach Kotlin konvertiert
- einige Anpassungen analog zu https://github.com/android/sunflower vorgenommen
- Navigationsgraph über GUI erstellt und Bottom-Navigation (Menu und Binding in MainActivity) angepasst

## Kotlin
- it : bei Lambdas mit nur einem Parameter kann dieser weggelassen werden und heißt it
- ? : darf null sein
- damit von einer Klasse geerbt werden kann braucht es als erstes Schlüsselwort open
- Code-Beispiele mit Besonderheiten der Sprache
  - Referenz https://kotlinlang.org/docs/reference
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
```

## Architektur-Komponenten
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

## Thanks 