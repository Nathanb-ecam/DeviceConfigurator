package com.example.arduinobluetooth.utils

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.example.arduinobluetooth.MainActivity

class BLEPermissions(private val activity : MainActivity) {


    private val permissionLauncher: ActivityResultLauncher<Array<String>> =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            handlePermissionsResult(permissions)
        }



    fun requestPermissions(){
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                //Manifest.permission.BLUETOOTH_CONNECT
            )
        )
    }

    private fun handlePermissionsResult(permissions : Map<String,Boolean>){
        val allPermissionsGranted = permissions.all { it.value }

       if (allPermissionsGranted) {
           Log.i("PERMISSIONS","All required permissions are granted")
            //Toast.makeText(activity.applicationContext, "All permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity.applicationContext, "Some permissions are missing", Toast.LENGTH_SHORT).show()
        }
    }



}