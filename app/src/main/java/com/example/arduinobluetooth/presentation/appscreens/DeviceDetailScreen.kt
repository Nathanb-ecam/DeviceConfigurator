 package com.example.arduinobluetooth.presentation.appscreens



import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonColors
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.arduinobluetooth.R
import com.example.arduinobluetooth.presentation.Screen
import com.example.arduinobluetooth.bluetooth.BluetoothConfigData
import com.example.arduinobluetooth.bluetooth.BluetoothControllerImpl
import com.example.arduinobluetooth.bluetooth.MockBluetoothController
import com.example.arduinobluetooth.bluetooth.MyBluetoothDevice

import com.example.arduinobluetooth.presentation.viewmodels.BluetoothViewModel
import com.example.arduinobluetooth.presentation.uiComponents.Popup
import com.example.arduinobluetooth.login.ILoginViewModel
import com.example.arduinobluetooth.presentation.viewmodels.mock.MockLoginViewModel
import com.example.arduinobluetooth.ui.theme.ArduinoBluetoothTheme
import com.example.arduinobluetooth.bluetooth.BluetoothState
import com.example.arduinobluetooth.presentation.uiComponents.iCureButton
import com.example.arduinobluetooth.presentation.uiComponents.iCureProgressIndicator
import com.example.arduinobluetooth.presentation.uiComponents.iCureTextStyles
import com.example.arduinobluetooth.presentation.viewmodels.DeviceDataStatus
import kotlinx.coroutines.delay


@SuppressLint("MissingPermission")
@Composable
fun DeviceDetailScreen(
    navController: NavController,
    blueViewModel : BluetoothViewModel,
    loginViewModel: ILoginViewModel,
    deviceAddress : String?,
    ) {

    val context = LocalContext.current
    val device = blueViewModel.getDeviceByAddress(deviceAddress) ?: MyBluetoothDevice("Mock","AA:BB:CC:DD",37)
    val connectionState by blueViewModel.connectionState.collectAsState()
    val loginUIState = loginViewModel.uiState.collectAsState()

    val delayBeforeStopConnecting = 20000L



    var unableToConnect by remember { mutableStateOf(false) }
    var probablyNotAnICureDevice by remember { mutableStateOf(false) }

    handleConnectionError(unableToConnect,probablyNotAnICureDevice,navController, context = context)


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center)
    {

        when (connectionState) {
            BluetoothState.READY_TO_CONFIGURE -> {
                LaunchedEffect(key1 = false){
                    loginViewModel.getDeviceConfigData()
                }

                /*Log.i("deviceDataReady",loginUIState.value.deviceDataReady.toString())*/
                when (loginUIState.value.deviceDataStatus) {
                    DeviceDataStatus.READY -> {
                        device.let{
                            val deviceConfigData = loginUIState.value.deviceConfigData
                            DeviceConfiguration(device,deviceConfigData,blueViewModel,iCureButton.getButtonColors(context),connectionState)
                        }
                    }
                    DeviceDataStatus.ERROR -> {
                        Popup(
                            buttonColors = iCureButton.getButtonColors(context),
                            alertTitle = "Erreur lors de la création du contact",
                            buttonText = "Fermer",
                            onPopupClose = {
                                navController.navigate(Screen.BlueScreen.route)
                            }
                        )
                    }
                    else -> {
                        Text("Creation du contact ...")
                        Log.i("CONFIG", "Waiting contact data to configure the device")
                    }
                }

            }
            BluetoothState.CONFIGURED -> {
                Popup(
                    buttonColors = iCureButton.getButtonColors(context),
                    alertTitle = "Configuration réussie",
                    buttonText = "Fermer",
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

            BluetoothState.UNKNOWN_DEVICE->{
                probablyNotAnICureDevice = true
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
                    iCureProgressIndicator.getCirculatorIndicator(context)
                }
            }
        }
    }


}


@SuppressLint("MissingPermission")
@Composable
fun DeviceConfiguration(device: MyBluetoothDevice, deviceConfigData : BluetoothConfigData, blueViewModel: BluetoothViewModel, buttonDefaults: ButtonColors, connectionState : BluetoothState){
   /* val icon = if(connectionState == BluetoothState.CONNECTED) Icons.TwoTone.Check else Icons.TwoTone.Close
    val iconColor = if(connectionState == BluetoothState.CONNECTED) Color.Green else Color.Red*/
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
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {


                Text(
                    text = device.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = iCureTextStyles.h1()
                )



                Text(text = device.address, style = iCureTextStyles.pHint())


                Button(
                    onClick = {
                        blueViewModel.testDeviceConnection()
                    },
                    colors = buttonDefaults,
                    modifier = Modifier
                        .fillMaxWidth()
                        /*.padding(vertical = 8.dp)*/
                ) {
                    Text(text = "Identifier l'appareil")
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
                    Text(text = "Configurer")
                }
            }
        }

    }
}


@Composable
fun handleConnectionError(
    unableToConnect : Boolean,
    probablyNotAnICureDevice : Boolean,
    navController: NavController,
    context : Context
){

    val iCureButtonColors = iCureButton.getButtonColors(context)
    if(unableToConnect){
        Popup(
            buttonColors = iCureButtonColors,
            alertTitle = "Impossible de se connecter",
            buttonText ="Fermer",
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

    if(probablyNotAnICureDevice){
        Popup(
            buttonColors = iCureButtonColors,
            alertTitle = "L'appareil n'est pas d'iCure",
            buttonText ="Fermer",
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
}






@Preview(showBackground = true)
@Composable
fun DeviceDetailPreview() {
    val context = LocalContext.current
    ArduinoBluetoothTheme {
        val mockBluetoothController = MockBluetoothController(BluetoothState.READY_TO_CONFIGURE)
        val navController = rememberNavController()

        val mockLoginViewModel  = viewModel{ MockLoginViewModel() }
        val blueViewModel = viewModel { BluetoothViewModel(mockBluetoothController) }


        DeviceDetailScreen(
            navController = navController,
            blueViewModel = blueViewModel,
            loginViewModel = mockLoginViewModel,
            deviceAddress = "E0:5A:1B:E3:6F:AE"
        )


    }
}


