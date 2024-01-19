package com.example.arduinobluetooth.presentation.appscreens


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.arduinobluetooth.R
import com.example.arduinobluetooth.Screen
import com.example.arduinobluetooth.data.BluetoothController
import com.example.arduinobluetooth.h2
import com.example.arduinobluetooth.h3
import com.example.arduinobluetooth.pHint
import com.example.arduinobluetooth.presentation.BluetoothViewModel
import com.example.arduinobluetooth.ui.theme.ArduinoBluetoothTheme


@SuppressLint("MissingPermission")
@Composable
fun BluetoothScreen(
    navController : NavHostController,
    blueViewModel : BluetoothViewModel = viewModel()
){
    val context = LocalContext.current
    val scannedDevices by blueViewModel.scannedDevices.collectAsState()


    val buttonDefaults = ButtonDefaults.buttonColors(
        containerColor = Color(context.resources.getColor(R.color.icure_green)),
        contentColor = Color.White
    )

    val outlinedButtonDefaults = ButtonDefaults.buttonColors(
        containerColor = Color(context.resources.getColor(R.color.icure_white)),
        contentColor = Color(context.resources.getColor(R.color.icure_black)),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f)
            .background(Color(context.resources.getColor(R.color.icure_white))),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ){
        Row(){
            OutlinedButton(
                onClick = { blueViewModel.startScan(context = context)},
                colors = outlinedButtonDefaults,
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 0.dp
                )
            ) {
                Text(text="Search for devices")

            }

            Button(
                onClick = {blueViewModel.deleteSearchResults(context = context)},
                colors = buttonDefaults
            ){
                Text(text = "Clear")
            }
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(Color(context.resources.getColor(R.color.lightGray)))
        )
        LazyColumn(modifier=Modifier.fillMaxSize()){
            itemsIndexed(scannedDevices){index,device->
                //if (device.device.address == "C8:C9:A3:E6:64:92"){
                    //blueViewModel.stopScan(context)

                    if (index != 0) {
                        Divider(
                            color = Color.Gray,
                            thickness = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)){
                        Text(
                            text = device.rssi.toString().substring(1),
                            style = h2, color = Color(context.resources.getColor(R.color.icure_green)),
                            modifier = Modifier.fillMaxHeight().align(Alignment.CenterVertically)
                            )
                        Column (modifier = Modifier.fillMaxHeight().align(Alignment.CenterVertically)){
                            Text(text = device.device.name?:"No name",style = h3,color = Color(context.resources.getColor(R.color.icure_black)))
                            Text(text = device.device.address?:"No adress", style = pHint,color = Color(context.resources.getColor(R.color.icure_black)))

                        }
                        Button(
                            onClick = {
                                blueViewModel.stopScan(context = context)
                                navController.navigate(Screen.DeviceDetailScreen.withArgs(device.device.address))
                                //blueViewModel.connectDevice(context,device.device)
                                      },
                            colors = buttonDefaults,
                            modifier = Modifier.fillMaxHeight().align(Alignment.CenterVertically)
                        ){
                            Text(text="Details")
                        }
                    }
               //}
            }
        }


    }
}



@Preview(showBackground = true)
@Composable
fun BluetoothPreview() {

    val context = LocalContext.current
    val navController = rememberNavController()

    val bluetoothController = BluetoothController(context)
    val blueViewModel  = viewModel{ BluetoothViewModel(bluetoothController) }
    ArduinoBluetoothTheme {

        BluetoothScreen(navController = navController,blueViewModel)


    }
}















