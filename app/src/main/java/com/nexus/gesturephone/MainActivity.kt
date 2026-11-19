
package com.nexus.gesturephone

import android.app.Activity
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var status: TextView

    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.rgb(15, 18, 30))
        }

        val title = TextView(this).apply {
            text = "NEXUS"
            textSize = 38f
            setTextColor(Color.rgb(0, 200, 255))
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "GESTURE PHONE"
            textSize = 20f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        status = TextView(this).apply {
            text = "\nدر حال بررسی مجوز دوربین..."
            textSize = 16f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(status)

        setContentView(layout)

        checkCameraPermission()
    }

    private fun checkCameraPermission() {

        if (checkSelfPermission(Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {

            status.text = "\nمجوز دوربین فعال است\n\nآماده راه‌اندازی دوربین جلو"

        } else {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == CAMERA_PERMISSION_REQUEST) {

            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                status.text = "\nمجوز دوربین با موفقیت دریافت شد!\n\nآماده راه‌اندازی دوربین جلو"
                status.setTextColor(Color.rgb(0, 220, 130))

            } else {

                status.text = "\nمجوز دوربین رد شد!\n\nبرای استفاده از قابلیت‌های حرکتی باید دسترسی دوربین را فعال کنی."
                status.setTextColor(Color.rgb(255, 100, 100))
            }
        }
    }
}
