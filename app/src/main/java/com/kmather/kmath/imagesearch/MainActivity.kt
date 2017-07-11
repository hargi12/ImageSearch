package com.kmather.kmath.imagesearch

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File

class MainActivity : AppCompatActivity() {
    var mStorageRef: StorageReference? = null
    var mAuth: FirebaseAuth? = null

    val GALLERY_PERMISSIONS_REQUEST = 0
    val GALLERY_IMAGE_REQUEST = 1
    val CAMERA_PERMISSIONS_REQUEST = 2
    val CAMERA_IMAGE_REQUEST = 3
    val FILE_NAME = "temp.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mStorageRef = FirebaseStorage.getInstance().reference
        mAuth = FirebaseAuth.getInstance()


        fab.setOnClickListener { view ->

            val builder = AlertDialog.Builder(this@MainActivity)
            builder
                    .setMessage(R.string.dialog_select_prompt)
                    .setPositiveButton(R.string.dialog_select_gallery, DialogInterface.OnClickListener { dialog, which ->
                        startGalleryChooser()
                    })
                    .setNegativeButton(R.string.dialog_select_camera, DialogInterface.OnClickListener { dialog, which ->
                        startCamera()
                    })
            builder.create().show()


            //var imgPath : String = "10900005_10152564129286536_2275526967453916269_o.jpg"
            //searchImage(imgPath)
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
        //base_url += "http://storage.googleapis.com/imagesearch-9b5e7.appspot.com" //"http://storage.googleapis.com/avid-toolbox-5658/"

        val uri = Uri.parse(base_url + imgPath)
        var i : Intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(i);
    }

    fun uploadImage(uri : Uri, imgName: String) {
        var bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri)
        var byteStream : ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteStream)

        var riversRef : StorageReference  = mStorageRef!!.child("avid-toolbox-5658/$imgName")
        riversRef.putBytes( byteStream.toByteArray() )
                .addOnProgressListener( {taskSnapshot: UploadTask.TaskSnapshot ->
                    //Toast.makeText(applicationContext, "Updating", Toast.LENGTH_LONG).show()
                })
                .addOnSuccessListener( {taskSnapshot : UploadTask.TaskSnapshot ->
                        // Get a URL to the uploaded content
                    taskSnapshot.metadata!!.downloadUrl
                        var downloadUrl : Uri? = taskSnapshot.getDownloadUrl()
                        Log.i("KEVIN", downloadUrl.toString())
                        Log.i("KEVIN", taskSnapshot.metadata!!.downloadUrl.toString())
                        Toast.makeText(applicationContext, "Upload worked", Toast.LENGTH_LONG).show()
                        searchImage(downloadUrl.toString())
                })
                .addOnFailureListener({exception : Exception ->
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(applicationContext, "Upload failed", Toast.LENGTH_LONG).show()
                })
    }

    fun startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST)
        }


    }

    fun startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoUri = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", getCameraFile())
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST)
        }
    }

    fun getCameraFile(): File {
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(dir, FILE_NAME)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.getCurrentUser()
        mAuth!!.signInAnonymously().addOnSuccessListener(this, OnSuccessListener<AuthResult>() {

            fun onSuccess(authResult : AuthResult) {
                // do your stuff
            }
        })
        .addOnFailureListener(this, OnFailureListener() {

            fun onFailure( exception: Exception) {
                Log.e("TAG", "signInAnonymously:FAILURE", exception);
            }
        });
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            uploadImage(data.data, data.data.path)
            data.data.path
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val photoUri = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", getCameraFile())
            uploadImage(photoUri, photoUri.lastPathSegment)

        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSIONS_REQUEST -> if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                startCamera()
            }
            GALLERY_PERMISSIONS_REQUEST -> if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                startGalleryChooser()
            }
        }
    }
}
