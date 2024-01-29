package com.example.arduinobluetooth.presentation.appscreens



import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Check
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
import com.example.arduinobluetooth.data.MyBluetoothDevice
import com.example.arduinobluetooth.h1
import com.example.arduinobluetooth.h3
import com.example.arduinobluetooth.p
import com.example.arduinobluetooth.pHint
import com.example.arduinobluetooth.presentation.BluetoothViewModel
import com.example.arduinobluetooth.utils.BluetoothState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn


@SuppressLint("MissingPermission")
@Composable
fun DeviceDetailScreen(
    navController: NavController,
    blueViewModel : BluetoothViewModel,
    deviceAddress : String?,
    ) {

    val context = LocalContext.current

    //Log.i("RECEIVED",deviceAddress!!)

    val device = blueViewModel.getDeviceByAddress(deviceAddress)

    val connectionState by blueViewModel.connectionState.collectAsState()


    val buttonDefaults = ButtonDefaults.buttonColors(
        containerColor = Color(context.resources.getColor(R.color.icure_green)),
        contentColor = Color.White
    )

    val icon = if(connectionState == BluetoothState.CONFIGURED) Icons.TwoTone.Check else Icons.TwoTone.Close
    val iconColor = if(connectionState == BluetoothState.CONFIGURED) Color.Green else Color.Red

    val activeStates = listOf(BluetoothState.READY_TO_CONFIGURE,BluetoothState.CONFIGURED)
    if(connectionState == BluetoothState.READY_TO_CONFIGURE){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center

        ) {
            if (device != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .fillMaxHeight(0.5f)
                        .padding(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        val deviceName = device.device.name ?: "No Name"

                        Row(horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Device : ${deviceName}", style = h1)
                            Icon(imageVector = icon, contentDescription = "lo", tint = iconColor)

                        }

                        if (deviceAddress != null) {
                            Text(text = deviceAddress, style = h3)
                        }


                        Button(
                            onClick = {
                                blueViewModel.testDeviceConnection()
                            },
                            colors = buttonDefaults,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(text = "Toggle builtin led")
                        }
                        Button(
                            onClick = {
                                blueViewModel.configureArduinoDevice()
                            },
                            colors = buttonDefaults,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(text = "Configure")
                        }
                    }
                }
            } else {
                Text(text = "Device not found")
            }
        }
    }
    else if(connectionState == BluetoothState.CONFIGURED){
        DeviceSuccessfullyConfigured(navController = navController, buttonColors = buttonDefaults)
    }
    else{
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.wrapContentSize()
            )
        }
    }





}






@Composable
fun DeviceSuccessfullyConfigured(navController: NavController,buttonColors : ButtonColors){
    val icon = Icons.TwoTone.Check

    AlertDialog(
        // onDismissRequest callback
        modifier = Modifier
            .padding(8.dp),
        onDismissRequest = { },
        //shape = CircleShape,
        title = {
            Text(text = "Device successfully configured")
/*            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(imageVector = icon, contentDescription = "lo", tint = Color.Green)
            }*/
        },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Column{
                    Button(
                        onClick = {
                            // Perform navigation when button is clicked
                            navController.navigate(Screen.BlueScreen.route)
                        },
                        colors = buttonColors,
                    ) {
                        Text("OK")
                    }
                }

            }
        }
    )
}
