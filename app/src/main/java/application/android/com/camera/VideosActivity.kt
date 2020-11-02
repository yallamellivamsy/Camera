package application.android.com.camera

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import android.widget.VideoView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_videos.*
import androidx.recyclerview.widget.RecyclerView
import java.io.File


class VideosActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var recyclerViewAdapter: RecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videos)
        val videoPath = intent.getStringExtra("EXTRA_MESSAGE")
        //Toast.makeText(this, "${videoPath}", Toast.LENGTH_LONG).show()
        val recordFiles = ContextCompat.getExternalFilesDirs(this, Environment.DIRECTORY_MOVIES)
        val storageDirectory = recordFiles[0]
        val file = File("${storageDirectory.absoluteFile}")
        val files = file.list()

        if(files.size !=0) {
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView?.setLayoutManager(LinearLayoutManager(this));
            recyclerView?.setHasFixedSize(true);
            recyclerView?.setItemViewCacheSize(20);
            recyclerView?.setNestedScrollingEnabled(false);

            recyclerViewAdapter = RecyclerViewAdapter(this);

            recyclerView?.setAdapter(recyclerViewAdapter);
        }else{
            Toast.makeText(this,"No Videos Found",Toast.LENGTH_LONG).show()
        }
    }
}
