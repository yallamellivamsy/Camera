package application.android.com.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*
import android.os.CountDownTimer
import android.R.attr.button
import android.app.Activity
import android.content.ContentValues
import android.hardware.Camera
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import androidx.camera.core.*
import androidx.camera.core.impl.PreviewConfig
import androidx.camera.view.CameraView
import application.android.com.camera.R.attr.lensFacing
import org.w3c.dom.Text




class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.java.simpleName
    var isRecording = false
    var recordingPause = false
    var countDownTimer: CountDownTimer? = null
    val chronometer: Chronometer ?= null
    var recordingTimer: Long = 0
    var videoRecordingFilePath: String = "NULL"
    var CAMERA_PERMISSION = Manifest.permission.CAMERA
    var RECORD_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO


    var RC_PERMISSION = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)

        //val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val recordFiles = ContextCompat.getExternalFilesDirs(this, Environment.DIRECTORY_MOVIES)
        val storageDirectory = recordFiles[0]
        val imageCaptureFilePath = "${storageDirectory.absoluteFile}/${System.currentTimeMillis()}.jpg"
        val root = File(Environment.getExternalStorageDirectory().name);
        camera_view.layoutParams.width = width
        camera_view.layoutParams.height = (width * 16 / 9)
        camera_view.requestLayout();


        val flipCamera = findViewById(R.id.flip_camera) as LinearLayout
        flipCamera.setOnClickListener{
            if(isRecording){
            }else {
                when (camera_view.cameraLensFacing) {
                    0 -> {
                        camera_view.cameraLensFacing = 1
                    }
                    1 -> {
                        camera_view.cameraLensFacing = 0
                    }
                }
            }

        }
        videos.setOnClickListener {
            val intent = Intent(this, VideosActivity::class.java).apply {
                putExtra("EXTRA_MESSAGE", videoRecordingFilePath)
            }
            startActivity(intent)
        }

        if (checkPermissions()) startCameraSession() else requestPermissions()

        video_record.setOnClickListener {
            videoRecordingFilePath = "${storageDirectory.absoluteFile}/${System.currentTimeMillis()}.mp4"
            if (isRecording) {
                resetRecording()
                camera_view.stopRecording()
            } else {
                checkFilePath(storageDirectory)
                startRecording()
                recordVideo(videoRecordingFilePath)
            }
        }
        flash.setOnClickListener {
            //camera_view.isTorchOn
            //camera_view.
            if(camera_view.isTorchOn){
                flash.setImageResource(R.drawable.flash_off)
                camera_view.enableTorch(false)
            }else {
                flash.setImageResource(R.drawable.flash_on)
                camera_view.enableTorch(true)
            }
        }

    }
    private fun openFrontFacingCamera(): Camera? {
        var cameraCount = 0
        var cam: Camera? = null
        val cameraInfo = Camera.CameraInfo()
        cameraCount = Camera.getNumberOfCameras()
        for (camIdx in 0 until cameraCount) {
            Camera.getCameraInfo(camIdx, cameraInfo)
            if (cameraInfo.facing === Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx)
                } catch (e: RuntimeException) {
                    Log.e(ContentValues.TAG, "Camera failed to open: " + e.localizedMessage)
                }

            }
        }

        return cam
    }
    private fun checkFilePath( storageDirectory: File){
        val file = File("${storageDirectory.absoluteFile}")
        val files = file.list()

        for (i in 0..(files.size-1)){
            if(videoRecordingFilePath == "${file}/${files[i]}"){
                videoRecordingFilePath.dropLast(4)
                videoRecordingFilePath += "(${i+1}).mp4"
            }
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA_PERMISSION, RECORD_AUDIO_PERMISSION), RC_PERMISSION)
    }

    private fun checkPermissions(): Boolean {
        return ((ActivityCompat.checkSelfPermission(this, CAMERA_PERMISSION)) == PackageManager.PERMISSION_GRANTED
                && (ActivityCompat.checkSelfPermission(this, CAMERA_PERMISSION)) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            RC_PERMISSION -> {
                var allPermissionsGranted = false
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false
                        break
                    } else {
                        allPermissionsGranted = true
                    }
                }
                if (allPermissionsGranted) startCameraSession() else permissionsNotGranted()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startCameraSession() {
        camera_view.bindToLifecycle(this)

    }

    private fun permissionsNotGranted() {
        AlertDialog.Builder(this).setTitle("Permissions required")
            .setMessage("These permissions are required to use this app. Please allow Camera and Audio permissions first")
            .setCancelable(false)
            .setPositiveButton("Grant") { dialog, which -> requestPermissions() }
            .show()
    }

    private fun recordVideo(videoRecordingFilePath: String) {
        camera_view.startRecording(File(videoRecordingFilePath), ContextCompat.getMainExecutor(this), object: VideoCapture.OnVideoSavedCallback {
            override fun onVideoSaved(file: File) {
                Toast.makeText(this@MainActivity, "Recording Saved", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onVideoSaved $videoRecordingFilePath")
            }

            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                Toast.makeText(this@MainActivity, "Recording Failed", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "onError $videoCaptureError $message")
                resetRecording()
            }

        })
    }
    private fun resetRecording(){
        isRecording = false
        recordingTimer = 0
        timer.stop()
        timer.visibility = View.INVISIBLE
        //onRecording.visibility = View.INVISIBLE
        flip_camera.visibility = View.VISIBLE
        videos.visibility = View.VISIBLE
        video_record.setBackgroundResource(R.drawable.record_stop)

    }
    private fun startRecording(){
        isRecording = true
        timer.setBase(SystemClock.elapsedRealtime() + recordingTimer)
        timer.visibility = View.VISIBLE
        flip_camera.visibility = View.INVISIBLE
        videos.visibility = View.INVISIBLE
        //onRecording.setBackgroundResource(R.drawable.pause)
        timer.start()
        video_record.setBackgroundResource(R.drawable.record_on)
        //onRecording.visibility = View.VISIBLE
    }


    private fun captureImage(imageCaptureFilePath: String) {
        camera_view.takePicture(File(imageCaptureFilePath), ContextCompat.getMainExecutor(this), object: ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Toast.makeText(this@MainActivity, "Image Captured", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onImageSaved $imageCaptureFilePath")
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(this@MainActivity, "Image Capture Failed", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "onError $exception")
            }
        })
    }
}
