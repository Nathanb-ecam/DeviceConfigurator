package com.example.arduinobluetooth


import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.arduinobluetooth.data.BluetoothController
import com.example.arduinobluetooth.presentation.BluetoothScreen
import com.example.arduinobluetooth.presentation.BluetoothViewModel
import com.example.arduinobluetooth.presentation.Navigation
import com.example.arduinobluetooth.ui.theme.ArduinoBluetoothTheme



class MainActivity : ComponentActivity(){

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
            // Your logic here
            Log.i("BTH","")
        } else {
            // Permission denied
            // Handle accordingly
            Log.i("BTH","Missing permission")
            Toast.makeText(applicationContext,"The app needs permissions",Toast.LENGTH_SHORT)
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)


        setContent {
            ArduinoBluetoothTheme {
                val navController = rememberNavController()
                val bluetoothController = BluetoothController(applicationContext)
                val blueViewModel : BluetoothViewModel = viewModel{BluetoothViewModel(bluetoothController)}

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                    /*    Button(onClick = {

                            //permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }) {
                            Text(text = "Launch permission")
                        }*/
                        Navigation(navController=navController,bluetoothViewModel = blueViewModel)

                    }




                }
            }
        }
    }








}




