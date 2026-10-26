
package com.smartautoclicker.app

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.graphics.Color
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    private lateinit var wordInput: EditText
    private lateinit var status: TextView
    private lateinit var intervalInput: EditText

    private val prefs by lazy {
        getSharedPreferences("settings", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(13, 17, 23)
        window.navigationBarColor = Color.rgb(13, 17, 23)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 28, 28, 28)
            setBackgroundColor(Color.rgb(13, 17, 23))
        }

        fun addLabel(text: String, size: Float) {
            root.addView(TextView(this).apply {
                this.text = text
                textSize = size
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                setPadding(0, 12, 0, 12)
            })
        }

        fun addButton(text: String, action: () -> Unit) {
            root.addView(Button(this).apply {
                this.text = text
                isAllCaps = false
                setOnClickListener { action() }
            }, LinearLayout.LayoutParams(-1, -2))
        }

        addLabel("النقر التلقائي", 26f)
        addLabel("اكتب الكلمة المطلوب البحث عنها", 16f)

        wordInput = EditText(this).apply {
            hint = "مثال: تخطي"
            setHintTextColor(Color.GRAY)
            setTextColor(Color.WHITE)
            textSize = 18f
            singleLine = true
            setText(prefs.getString("word", ""))
        }

        root.addView(wordInput)

        addLabel("الفاصل بين النقرات (مللي ثانية)", 16f)
        intervalInput = EditText(this).apply {
            hint = "0 إلى 86400000 (24 ساعة)"
            setHintTextColor(Color.GRAY)
            setTextColor(Color.WHITE)
            textSize = 18f
            singleLine = true
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(prefs.getLong("interval_ms", 1000L).toString())
        }
        root.addView(intervalInput)
        addLabel("0 = أسرع تنفيذ تسمح به أحداث النظام، و86400000 = 24 ساعة", 12f)

        status = TextView(this).apply {
            text = "الحالة: متوقف"
            textSize = 16f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setPadding(0, 18, 0, 18)
        }

        addButton("حفظ الكلمة") {
            saveWord()
        }

        addButton("إعدادات إمكانية الوصول") {
            startActivity(
                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            )
        }

        addButton("تشغيل البحث") {
            if (!saveWord()) return@addButton

            if (isServiceEnabled()) {
                status.text = "الخدمة مفعّلة — البحث عن الكلمة جارٍ"
            } else {
                status.text =
                    "فعّل خدمة النقر التلقائي من إعدادات إمكانية الوصول"
                startActivity(
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                )
            }
        }

        addButton("إيقاف") {
            status.text =
                "لإيقاف النقر، عطّل خدمة النقر التلقائي من إعدادات إمكانية الوصول"
        }

        root.addView(status)

        setContentView(ScrollView(this).apply {
            addView(root)
        })
    }

    private fun saveWord(): Boolean {
        val word = wordInput.text.toString().trim()
        val intervalText = intervalInput.text.toString().trim()
        val interval = intervalText.toLongOrNull()

        if (word.isEmpty()) {
            wordInput.error = "اكتب الكلمة أولًا"
            return false
        }
        if (interval == null || interval !in 0L..86_400_000L) {
            intervalInput.error = "أدخل قيمة من 0 إلى 86400000 مللي ثانية"
            return false
        }

        prefs.edit()
            .putString("word", word)
            .putLong("interval_ms", interval)
            .apply()
        status.text = "تم الحفظ — الفاصل: $interval مللي ثانية"
        return true
    }

    private fun isServiceEnabled(): Boolean {
        val manager = getSystemService(ACCESSIBILITY_SERVICE)
                as android.view.accessibility.AccessibilityManager

        return manager.getEnabledAccessibilityServiceList(
            android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        ).any {
            it.resolveInfo.serviceInfo.packageName == packageName &&
            it.resolveInfo.serviceInfo.name.endsWith("AutoClickService")
        }
    }
}