package application.android.com.camera

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView



class VideoPlayer : AppCompatActivity() {

    val displayMetrics = DisplayMetrics()
    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        val path = intent.getStringExtra("path");
        //Toast.makeText(this,"${path}", Toast.LENGTH_LONG).show()
        val uri = Uri.parse(path)
        val videoView: VideoView = findViewById(R.id.videoView) // initiate a video view
        videoView.setVideoURI(uri)
        val mediaController = MediaController(this)
        videoView.setMediaController(mediaController)
        //videoView.getLayoutParams().width = width;
        //videoView.getLayoutParams().height = width * 16 / 9;
        //videoView.requestLayout();
        videoView.start()
    }
}
