package com.example.arduinobluetooth.presentation.appscreens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.arduinobluetooth.mqtt.ILiveData
import com.example.arduinobluetooth.mqtt.LiveSession
import com.example.arduinobluetooth.mqtt.SensorDataContent
import com.example.arduinobluetooth.presentation.Screen
import com.example.arduinobluetooth.presentation.uiComponents.iCureButton
import com.example.arduinobluetooth.presentation.uiComponents.Popup
import com.example.arduinobluetooth.presentation.uiComponents.iCureProgressIndicator
import com.example.arduinobluetooth.presentation.uiComponents.iCureTextStyles

import com.example.arduinobluetooth.presentation.viewmodels.LoginViewModel
import com.example.arduinobluetooth.presentation.viewmodels.mock.MockLiveDataViewModel
import com.example.arduinobluetooth.ui.theme.ArduinoBluetoothTheme
import com.icure.kryptom.utils.hexToByteArray
import com.icure.kryptom.utils.toHexString


@Composable
fun DeviceLiveDataScreen(
    navController: NavController,
    liveDataViewModel : ILiveData = viewModel(),
    loginViewModel: LoginViewModel

) {
    val loginState by loginViewModel.uiState.collectAsState()
    val liveData by liveDataViewModel.liveData.collectAsState()
    val context = LocalContext.current



    if(!liveData.connected){
        liveDataViewModel.setupMqtt(loginState.deviceConfigData.key)
    }




    DisposableEffect(Unit) {
        onDispose {
            liveDataViewModel.unsubscribe()
        }
    }

    when(liveData.connected && liveData.subscribed){
        true ->{
            DeviceSensorData(liveData)
        }
        false ->{
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





@Composable
fun DeviceSensorData(liveData : LiveSession){
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f)
    ) {
        itemsIndexed(liveData.liveSensorData) { index, sensorData ->

            DeviceDataCard(sensorData)
     /*       Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp))*/


        }
    }
}


@Composable
fun DeviceDataCard(sensorData : SensorDataContent){
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxHeight(0.7f)
            .height(100.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                val value =sensorData.content.measures.value.value
                val unit =sensorData.content.measures.value.unit.toString()
                Text(
                    value + unit,
                    color = Color(context.resources.getColor(R.color.icure_green))
                    )

            }
            Text(
            sensorData.content.measures.value.comment.toString()
                ,style = iCureTextStyles.pHint()
                ,maxLines = 2
                ,overflow = TextOverflow.Ellipsis
            )
        }

    }

}

@Preview(showBackground = true)
@Composable
fun DeviceLiveDataPreview() {
    val context = LocalContext.current
    ArduinoBluetoothTheme {

        val navController = rememberNavController()


        val liveDataViewModel : ILiveData = MockLiveDataViewModel()
        val loginViewModel = viewModel{ LoginViewModel(context) }

        DeviceLiveDataScreen(
            navController = navController,
            liveDataViewModel = liveDataViewModel,
            loginViewModel = loginViewModel
        )


    }
}









