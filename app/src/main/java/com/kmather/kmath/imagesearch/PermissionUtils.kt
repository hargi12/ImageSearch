package com.kmather.kmath.imagesearch;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

object PermissionUtils {
    fun  requestPermission( activity: Activity, requestCode: Int, vararg permissions : String) : Boolean {
        var granted = true
        var permissionsNeeded = ArrayList<String>()

        for (s in permissions) {
            var permissionCheck = ContextCompat.checkSelfPermission(activity, s)
            var hasPermission = (permissionCheck == PackageManager.PERMISSION_GRANTED)
            granted = granted and hasPermission

            if (!hasPermission) permissionsNeeded.add(s)
        }

        if (granted) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    permissionsNeeded.toArray(Array<String>(permissionsNeeded.size, {""})),
                    requestCode);
            return false;
        }
    }

    fun permissionGranted(
            requestCode: Int, permissionCode: Int, grantResults: IntArray) : Boolean {
        if (requestCode == permissionCode) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
