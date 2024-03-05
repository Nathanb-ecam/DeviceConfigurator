package com.example.arduinobluetooth


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.arduinobluetooth.data.Bluetooth.BluetoothControllerImpl
import com.example.arduinobluetooth.data.Bluetooth.BottomNavItem
import com.example.arduinobluetooth.data.Bluetooth.Mqtt.MqttController
import com.example.arduinobluetooth.presentation.viewmodels.BluetoothViewModel
import com.example.arduinobluetooth.presentation.viewmodels.LoginViewModel
import com.example.arduinobluetooth.presentation.Navigation
import com.example.arduinobluetooth.presentation.viewmodels.LiveDataViewModel
import com.example.arduinobluetooth.ui.theme.ArduinoBluetoothTheme
import com.example.arduinobluetooth.utils.BLEPermissions


class MainActivity : ComponentActivity(){
    private lateinit var blePermissions: BLEPermissions

    private lateinit var bluetoothController : BluetoothControllerImpl
    private lateinit var mqttController : MqttController

    private lateinit var blueViewModel : BluetoothViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var liveDataViewModel: LiveDataViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        blePermissions = BLEPermissions(this)
        blePermissions.requestPermissions()


        setContent {

            ArduinoBluetoothTheme {
                val navController = rememberNavController()
                bluetoothController = BluetoothControllerImpl(applicationContext)
                mqttController = MqttController(applicationContext)
                blueViewModel  = viewModel{ BluetoothViewModel(bluetoothController) }
                loginViewModel  = viewModel{ LoginViewModel(applicationContext) }
                liveDataViewModel  = viewModel{ LiveDataViewModel(applicationContext,mqttController) }


                val bottomNavItems = listOf(
                    BottomNavItem(
                        name="Device list",
                        route = Screen.BlueScreen.route,
                        icon = Icons.Default.Search
                    ),
                    BottomNavItem(
                        name="Live data",
                        route = Screen.DeviceLiveDataScreen.route,
                        icon = Icons.Outlined.List
                    ),
                    BottomNavItem(
                        name="Help",
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
                        liveDataViewModel = liveDataViewModel,
                        loginViewModel = loginViewModel,
                        bottomNavItems = bottomNavItems
                    )
                }
            }
        }
    }
}






