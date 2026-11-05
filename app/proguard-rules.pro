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
# Игнорируем отсутствующие в Android десктопные классы
# ==========================================

# 1. Базовые десктопные классы и логирование (из вашего исходного файла)
-dontwarn java.awt.**
-dontwarn org.osgi.**
-dontwarn aQute.bnd.**
-dontwarn edu.umd.cs.findbugs.**
-dontwarn org.apache.logging.log4j.**

# 2. Классы обработки изображений и UI (Java SE, отсутствуют в Android)
-dontwarn javax.imageio.**
-dontwarn javax.swing.**

# 3. XML Crypto и StAX (урезаны или отсутствуют в Android, POI ссылается на них для подписи документов)
-dontwarn javax.xml.crypto.**
-dontwarn javax.xml.stream.**

# 4. Saxon XSLT/XQuery процессор (используется POI для сложного XPath, в Android не нужен)
-dontwarn net.sf.saxon.**

# 5. Apache Batik (рендеринг SVG, полностью зависит от java.awt, в Android не работает)
-dontwarn org.apache.batik.**
-dontwarn org.w3c.dom.svg.**

# 6. PDFBox Graphics2D (используется POI для экспорта слайдов в PDF, требует java.awt)
-dontwarn de.rototor.pdfbox.**
-dontwarn org.apache.pdfbox.**

# 7. Zstd сжатие (иногда тянется как опциональная зависимость commons-compress)
-dontwarn com.github.luben.zstd.**

# ==========================================
# Сохраняем основные классы POI, чтобы они не были удалены или обфусцированы
# ==========================================
-keep class org.apache.poi.** { *; }
-keep class org.openxmlformats.schemas.** { *; }
-keep class schemasMicrosoftComVml.** { *; }
-keep class schemasMicrosoftComOfficeExcel.** { *; }

# Дополнительно: сохраняем enum-ы и классы с нативными методами, 
# которые часто использует POI для внутренней рефлексии
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

# Сохраняем имена полей и методов для корректной работы рефлексии внутри POI
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
