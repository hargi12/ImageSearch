package com.kmather.kmath.imagesearch

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    var mStorageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mStorageRef = FirebaseStorage.getInstance().reference


        fab.setOnClickListener { view ->
            var imgPath : String = "10900005_10152564129286536_2275526967453916269_o.jpg"
            searchImage(imgPath)
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //        .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun searchImage(imgPath : String) {
        var base_url : String  = "https://www.google.com/searchbyimage?site=search&sa=X&image_url="
        base_url += "http://storage.googleapis.com/avid-toolbox-5658/"
        Log.i("KEVIN", base_url + imgPath)
        val uri = Uri.parse(base_url + imgPath)
        var i : Intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(i);
    }

    fun uploadImg(bitmap: Bitmap, imgName: String) {
        var byteStream : ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream)

        var riversRef : StorageReference  = mStorageRef!!.child("images/$imgName")
        riversRef.putBytes( byteStream.toByteArray() )
                .addOnSuccessListener( OnSuccessListener<UploadTask.TaskSnapshot>() {
                    fun onSuccess(taskSnapshot : UploadTask.TaskSnapshot) {
                        // Get a URL to the uploaded content
                        var downloadUrl : Uri? = taskSnapshot.getDownloadUrl()
                    }
                })
                .addOnFailureListener(OnFailureListener() {
                    fun onFailure(exception : Exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });

    }
}
