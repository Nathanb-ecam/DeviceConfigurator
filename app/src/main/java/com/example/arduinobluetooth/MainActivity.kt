package com.example.arduinobluetooth


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.arduinobluetooth.data.BluetoothControllerImpl
import com.example.arduinobluetooth.data.BottomNavItem
import com.example.arduinobluetooth.presentation.BluetoothViewModel
import com.example.arduinobluetooth.presentation.LoginViewModel
import com.example.arduinobluetooth.presentation.Navigation
import com.example.arduinobluetooth.ui.theme.ArduinoBluetoothTheme
import com.example.arduinobluetooth.utils.BLEPermissions


class MainActivity : ComponentActivity(){
    private lateinit var blePermissions: BLEPermissions
    private lateinit var bluetoothController : BluetoothControllerImpl
    private lateinit var blueViewModel : BluetoothViewModel
    private lateinit var loginViewModel: LoginViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        blePermissions = BLEPermissions(this)
        blePermissions.requestPermissions()


        setContent {

            ArduinoBluetoothTheme {
                val navController = rememberNavController()
                bluetoothController = BluetoothControllerImpl(applicationContext)
                blueViewModel  = viewModel{BluetoothViewModel(bluetoothController)}
                loginViewModel  = viewModel{LoginViewModel(applicationContext)}


                val bottomNavItems = listOf(
                    BottomNavItem(
                        name="Device list",
                        route = Screen.BlueScreen.route,
                        icon = Icons.Default.Search
                    ),
                    BottomNavItem(
                        name="Item's",
                        route = Screen.HelpScreen.route,
                        icon = Icons.Default.Settings
                    ),
                )
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    //color = MaterialTheme.colorScheme.background
                    color= Color(applicationContext.resources.getColor(com.example.arduinobluetooth.R.color.icure_white))
                ) {
                    Navigation(
                        navController=navController,
                        bluetoothViewModel = blueViewModel,
                        loginViewModel = loginViewModel,
                        bottomNavItems = bottomNavItems
                    )
                }
            }
        }
    }
}






