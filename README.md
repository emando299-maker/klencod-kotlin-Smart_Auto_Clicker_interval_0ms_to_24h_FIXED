# Smart Auto Clicker — Klencod Android Project

هذا مشروع Android قابل للتعديل باستخدام Kotlin وGradle.

## الملفات الأساسية
- `settings.gradle` و`build.gradle`: إعدادات بناء المشروع.
- `app/build.gradle`: إعدادات تطبيق Android.
- `app/src/main/AndroidManifest.xml`: أذونات ومكونات التطبيق.
- `app/src/main/java/com/smartautoclicker/app/MainActivity.kt`: الواجهة الرئيسية.
- `app/src/main/java/com/smartautoclicker/app/AutoClickService.kt`: خدمة إمكانية الوصول.
- `app/src/main/res/`: موارد التطبيق.

## فتح المشروع
1. فك ضغط الملف.
2. افتح المجلد الذي يحتوي مباشرةً على `settings.gradle` في Klencod IDE.
3. انتظر مزامنة/تهيئة Gradle ثم جرّب Build.

يتطلب بناء Gradle تنزيل بعض مكونات Android عند عدم توفرها محليًا. لم يتم تأكيد نجاح البناء في بيئة هذه الحزمة.
