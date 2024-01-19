package com.example.arduinobluetooth.presentation.appscreens



import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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


@SuppressLint("MissingPermission")
@Composable
fun DeviceDetailScreen(
    navController: NavController,
    blueViewModel : BluetoothViewModel,
    deviceAddress : String?,
    ) {

    val context = LocalContext.current

    Log.i("RECEIVED",deviceAddress!!)
    val device = blueViewModel.getDeviceByAddress(deviceAddress)


    val buttonDefaults = ButtonDefaults.buttonColors(
        containerColor = Color(context.resources.getColor(R.color.icure_green)),
        contentColor = Color.White
    )



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
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val deviceName =device.device.name ?: "No Name"

                    Text(text = "Device : ${deviceName}",style = h1)
                    Text(text = deviceAddress,style=h3)
                    Button(
                        onClick = {
                            blueViewModel.connectDevice(context, device.device)
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



