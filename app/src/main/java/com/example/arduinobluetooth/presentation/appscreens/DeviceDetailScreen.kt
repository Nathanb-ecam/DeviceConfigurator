package com.example.arduinobluetooth.presentation.appscreens



import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.CheckCircle
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.arduinobluetooth.R
import com.example.arduinobluetooth.Screen
import com.example.arduinobluetooth.data.BluetoothConfigData
import com.example.arduinobluetooth.data.MyBluetoothDevice
import com.example.arduinobluetooth.h1
import com.example.arduinobluetooth.h3
import com.example.arduinobluetooth.p
import com.example.arduinobluetooth.pHint
import com.example.arduinobluetooth.presentation.BluetoothViewModel
import com.example.arduinobluetooth.presentation.LoginViewModel
import com.example.arduinobluetooth.presentation.uiComponents.Popup
import com.example.arduinobluetooth.utils.BluetoothState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@SuppressLint("MissingPermission")
@Composable
fun DeviceDetailScreen(
    navController: NavController,
    blueViewModel : BluetoothViewModel,
    loginViewModel: LoginViewModel,
    deviceAddress : String?,
    ) {

    val context = LocalContext.current
    val device = blueViewModel.getDeviceByAddress(deviceAddress)
    val connectionState by blueViewModel.connectionState.collectAsState()
    val loginUIState = loginViewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()
    val delayBeforeStopConnecting = 10000L
    val buttonDefaults = ButtonDefaults.buttonColors(
        containerColor = Color(context.resources.getColor(R.color.icure_green)),
        contentColor = Color.White
    )
    var unableToConnect by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center)
    {
        if(unableToConnect){
            Popup(
                buttonColors = buttonDefaults,
                alertTitle = "Unable to connect",
                buttonText ="Close",
                onPopupClose = {
                    navController.navigate(Screen.BlueScreen.route)
                               },
                /*alertIcon = Icons.Outlined.,*/
                modifier = Modifier
                    .width(300.dp)
                    /*.padding(16.dp)*/
                    .wrapContentWidth(Alignment.CenterHorizontally)
                ,
                iconColor = Color.Red
            )
        }


        when (connectionState) {
            BluetoothState.READY_TO_CONFIGURE -> {
                LaunchedEffect(key1 = false){
                    loginViewModel.getDeviceConfigData()
                }


                Log.i("deviceDataReady",loginUIState.value.deviceDataReady.toString())
                if(loginUIState.value.deviceDataReady){
                    device?.let{
                        val deviceConfigData = loginUIState.value.deviceConfigData
                        DeviceConfiguration(device,deviceConfigData,blueViewModel,buttonDefaults,connectionState)
                    }
                }else{
                    Text("Preparing device data ...")
                    Log.i("CONFIG", "Missing informations to configure the device")
                }

            }
            BluetoothState.CONFIGURED -> {
                Popup(
                    buttonColors = buttonDefaults,
                    alertTitle = "Device successfully configured",
                    buttonText = "Close",
                    onPopupClose = {
                        blueViewModel.updateConnectionState(BluetoothState.DISCONNECTED)
                        navController.navigate(Screen.BlueScreen.route)

                                   },
                    alertIcon = Icons.TwoTone.CheckCircle,
                    modifier = Modifier
                        .width(300.dp)
                        .padding(16.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    iconColor = Color(context.getColor(R.color.icure_green))

                )
            }
            else -> {
                LaunchedEffect(Unit) {
                    delay(delayBeforeStopConnecting)
                    unableToConnect = true
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(context.resources.getColor(R.color.icure_green)),
                        modifier = Modifier.wrapContentSize(),
                    )
                }
            }
        }
    }


}


@SuppressLint("MissingPermission")
@Composable
fun DeviceConfiguration(device:MyBluetoothDevice, deviceConfigData : BluetoothConfigData, blueViewModel: BluetoothViewModel, buttonDefaults: ButtonColors, connectionState : BluetoothState){
    val icon = if(connectionState == BluetoothState.CONNECTED) Icons.TwoTone.Check else Icons.TwoTone.Close
    val iconColor = if(connectionState == BluetoothState.CONNECTED) Color.Green else Color.Red
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val deviceName = device.name ?: "Undefined"

                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Device : ${deviceName}", style = h1)
                    Icon(imageVector = icon, contentDescription = "lo", tint = iconColor)

                }

                if (device.address != null) {
                    Text(text = device.address, style = h3)
                }

                Button(
                    onClick = {
                        blueViewModel.testDeviceConnection()
                    },
                    colors = buttonDefaults,
                    modifier = Modifier
                        .fillMaxWidth()
                        /*.padding(vertical = 8.dp)*/
                ) {
                    Text(text = "Toggle builtin led")
                }
                Button(
                    onClick = {
                        blueViewModel.configureArduinoDevice(configData = deviceConfigData)
                    },
                    colors = buttonDefaults,
                    modifier = Modifier
                        .fillMaxWidth()
                        /*.padding(vertical = 8.dp)*/
                ) {
                    Text(text = "Configure")
                }
            }
        }

    }
}






