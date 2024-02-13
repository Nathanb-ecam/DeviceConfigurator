package com.example.arduinobluetooth.presentation.appscreens


import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.arduinobluetooth.R
import com.example.arduinobluetooth.Screen
import com.example.arduinobluetooth.data.BluetoothController
import com.example.arduinobluetooth.h1
import com.example.arduinobluetooth.h2
import com.example.arduinobluetooth.h3
import com.example.arduinobluetooth.pHint
import com.example.arduinobluetooth.presentation.BluetoothViewModel
import com.example.arduinobluetooth.ui.theme.ArduinoBluetoothTheme
import java.util.logging.Handler


@SuppressLint("MissingPermission")
@Composable
fun BluetoothScreen(
    navController : NavHostController,
    blueViewModel : BluetoothViewModel = viewModel()
){

    val context = LocalContext.current

    val scannedDevices by blueViewModel.scannedDevices.collectAsState()
    val searchText by blueViewModel.searchText.collectAsState()

    blueViewModel.startScan(context = context)
    blueViewModel.disconnectCurrentDevice()


    val buttonDefaults = ButtonDefaults.buttonColors(
        containerColor = Color(context.resources.getColor(R.color.icure_green)),
        contentColor = Color.White
    )


    val textFieldColors = TextFieldDefaults.textFieldColors(
        focusedIndicatorColor = Color(context.resources.getColor(R.color.icure_green)),
        cursorColor = Color(context.resources.getColor(R.color.icure_green)),
        focusedLabelColor = Color(context.resources.getColor(R.color.icure_green)),
        leadingIconColor = Color(context.resources.getColor(R.color.icure_green)),
    )

    if(searchText.isNotEmpty()){
        blueViewModel.stopScan(context = context)
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f)
            .background(Color(context.resources.getColor(R.color.icure_white))),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ){

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchText,
                onValueChange = blueViewModel::onSearchTextChange,
                label = { Text("Rechercher") },
                colors = textFieldColors,
                modifier = Modifier.padding(8.dp)
            )
            Icon(
                modifier=Modifier
                    .clickable {
                        blueViewModel.emptysearchText()
                    },
                imageVector = Icons.Default.Clear,
                contentDescription = "clear"
            )
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
                    Row(

                        modifier = Modifier
                            .fillMaxSize()
                            //.padding(horizontal = 12.dp, vertical = 4.dp)
                            .height(65.dp)
                            .clickable {
                                blueViewModel.stopScan(context = context)
                                blueViewModel.connectDevice(device.device)
                                navController.navigate(Screen.DeviceDetailScreen.withArgs(device.device.address))
                            }
                    ){
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .fillMaxHeight()
                                .width(50.dp)

                        ){


                            Text(
                                text = device.rssi.toString().substring(1),
                                style = h3, color = Color(context.resources.getColor(R.color.icure_green)),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxSize()

                            )

                            //Icon(imageVector = Icons.Default.Clear, contentDescription = "lo")
                        }



                        Column (
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start,
                            modifier =
                            Modifier
                                .fillMaxHeight()
                                //.align(Alignment.CenterVertically)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ){
                            Text(text = device.device.name?:"No name",style = h3,color = Color(context.resources.getColor(R.color.icure_black)))
                            Text(text = device.device.address?:"No adress", style = pHint,color = Color(context.resources.getColor(R.color.icure_black)))

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















