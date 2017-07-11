package com.kmather.kmath.imagesearch

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

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
            var imgPath : String = "http://3.bp.blogspot.com/-q_eJ32mE8yk/TxEpLykOtPI/AAAAAAAADHY/wHwJBzJMlS0/s1600/photo.JPG"
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
        base_url += "https://storage.googleapis.com/avid-toolbox-5658/"

        val uri = Uri.parse(base_url + imgPath)
        var i : Intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(i);
    }

    fun uploadImg(bitmap: Bitmap) {
        var byteStream : ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream)

        var riversRef : StorageReference  = mStorageRef.child("images/rivers.jpg");
    }
}
