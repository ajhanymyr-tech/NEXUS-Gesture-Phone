
package com.nexus.gesturephone

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var status: TextView
    private lateinit var cameraButton: Button

    private var cameraProvider: ProcessCameraProvider? = null
    private var isCameraRunning = false

    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createInterface()
        checkCameraPermission()
    }

    private fun createInterface() {

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.rgb(15, 18, 30))
        }

        val title = TextView(this).apply {
            text = "NEXUS"
            textSize = 34f
            setTextColor(Color.rgb(0, 200, 255))
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "GESTURE PHONE"
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        status = TextView(this).apply {
            text = "در حال آماده‌سازی دوربین..."
            textSize = 15f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 16)
        }

        previewView = PreviewView(this).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }

        cameraButton = Button(this).apply {
            text = "شروع دوربین"
            isEnabled = false

            setOnClickListener {
                if (isCameraRunning) {
                    stopCamera()
                } else {
                    startCamera()
                }
            }
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(status)

        layout.addView(
            previewView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        layout.addView(
            cameraButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        setContentView(layout)
    }

    private fun checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            cameraButton.isEnabled = true
            startCamera()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        }
    }

    private fun startCamera() {

        if (isCameraRunning) return

        status.text = "در حال راه‌اندازی دوربین جلو..."

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            try {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(
                            previewView.surfaceProvider
                        )
                    }

                val cameraSelector =
                    CameraSelector.DEFAULT_FRONT_CAMERA

                provider.unbindAll()

                provider.bindToLifecycle(
                    this@MainActivity,
                    cameraSelector,
                    preview
                )

                isCameraRunning = true

                status.text = "دوربین جلو فعال است"
                status.setTextColor(Color.rgb(0, 220, 130))

                cameraButton.text = "توقف دوربین"

            } catch (e: Exception) {

                status.text = "خطا در راه‌اندازی دوربین: ${e.message}"
                status.setTextColor(Color.RED)

                isCameraRunning = false
                cameraButton.text = "تلاش مجدد"
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopCamera() {

        cameraProvider?.unbindAll()

        isCameraRunning = false

        status.text = "دوربین متوقف شد"
        status.setTextColor(Color.LTGRAY)

        cameraButton.text = "شروع دوربین"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == CAMERA_PERMISSION_REQUEST) {

            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                cameraButton.isEnabled = true
                startCamera()

            } else {
                status.text = "دسترسی دوربین داده نشد"
                status.setTextColor(Color.RED)
            }
        }
    }

    override fun onDestroy() {
        cameraProvider?.unbindAll()
        super.onDestroy()
    }
}
