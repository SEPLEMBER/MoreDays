# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ==========================================
# Правила для Apache POI и его зависимостей
# Игнорируем отсутствующие в Android десктопные классы (java.awt, osgi и т.д.)
# ==========================================
-dontwarn java.awt.**
-dontwarn org.osgi.**
-dontwarn aQute.bnd.**
-dontwarn edu.umd.cs.findbugs.**
-dontwarn org.apache.logging.log4j.**

# Сохраняем основные классы POI и связанные схемы, чтобы они не были удалены при минификации
-keep class org.apache.poi.** { *; }
-keep class org.openxmlformats.schemas.** { *; }
-keep class schemasMicrosoftComVml.** { *; }
-keep class schemasMicrosoftComOfficeExcel.** { *; }
