package application.android.com.camera

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import android.provider.MediaStore
import android.media.ThumbnailUtils
import android.graphics.Bitmap
import android.hardware.Camera
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat.startActivity
import android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT
import android.hardware.Camera.getNumberOfCameras
import android.util.Log


class RecyclerViewAdapter(myContext: Context) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    val context = myContext

    val recordFiles = ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_MOVIES)
    val storageDirectory = recordFiles[0]
    val file = File("${storageDirectory.absoluteFile}")
    val files = file.list()
    override fun onCreateViewHolder(view: ViewGroup, position: Int): ViewHolder {
        val v = LayoutInflater.from(view.context).inflate(R.layout.custom_view, view, false)
        return ViewHolder(v);
    }
    override fun getItemCount(): Int {
        return files.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
    override fun onBindViewHolder(view: ViewHolder, position: Int) {
        //val id = context.getResources().getIdentifier("drawable/" + userList[position].imageId, null, context.getPackageName())
        //view.thumbnails.setImageResource(R.drawable.record_on)
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)

        //val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val videoPath = "${file}/${files[position]}"
        if(files.size!=0) {
            view.thumbnails.setImageBitmap(createVideoThumbNail(videoPath));
            view.thumbnails.getLayoutParams().width = (width - 100)
            view.thumbnails.getLayoutParams().height = (width - 100) * 16 / 9
            view.thumbnails.setOnClickListener {
                //Toast.makeText(context, "Position:${position}",Toast.LENGTH_LONG).show()
                val intent = Intent(context, VideoPlayer::class.java).apply {
                    putExtra("path", videoPath);
                }
                //startActivity(intent)
                context.startActivity(intent);
            }
        }else{
            Toast.makeText(context, "No Videos recorded",Toast.LENGTH_LONG).show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnails = itemView.findViewById<ImageView>(R.id.thumbnail)
    }

    fun createVideoThumbNail(path: String): Bitmap {
        return ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND)
    }
    private fun requestPermissions(callIntent: Intent):Int {
        if (Build.VERSION.SDK_INT >= 21) {
            if (ActivityCompat.checkSelfPermission(context, "android.permission.CALL_PHONE") != 0) {
                val result = ActivityCompat.requestPermissions(context as Activity,  arrayOf("android.permission.CALL_PHONE"), 1);
                //Toast.makeText(context, "Result:${result}",Toast.LENGTH_LONG).show()
                //context.startActivity(callIntent);
                return 0;
            }

            return 1;
        }

        return 1;
    }


}
