package com.example.arduinobluetooth.presentation.appscreens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.arduinobluetooth.Screen
import com.example.arduinobluetooth.data.Bluetooth.Mqtt.ILiveData
import com.example.arduinobluetooth.presentation.viewmodels.LiveDataViewModel
import com.example.arduinobluetooth.presentation.viewmodels.mock.MockLiveDataViewModel
import com.example.arduinobluetooth.ui.theme.ArduinoBluetoothTheme


@Composable
fun DeviceLiveDataScreen(
    navController: NavController,
    liveDataViewModel : ILiveData = viewModel()
) {
    val liveData by liveDataViewModel.liveData.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()

    /*if(backStackEntry?.destination?.route != Screen.DeviceDetailScreen.route ) liveDataViewModel.unsubscribe()*/


    liveDataViewModel.setupMqtt()



    DisposableEffect(Unit) {
        onDispose {
            liveDataViewModel.unsubscribe()
        }
    }







    Column {
        Text(text="Live data")


        LazyColumn(modifier = Modifier.fillMaxHeight(0.85f)) {
            itemsIndexed(liveData.liveSensorData) { index, sensorData ->
                Row{
                    Text(sensorData.content.measures.value.value.toString())
                    Text(sensorData.content.measures.value.unit.toString())
                    Text(sensorData.content.measures.value.comment.toString())
                }

            }
        }


        liveData.let {
            Text(text = "Connected : ${it.connected}")
            Text(text = "Subscribed : ${it.subscribed}")

        } ?: Text(text = "No data available")

        }
}



@Preview(showBackground = true)
@Composable
fun DeviceLiveDataPreview() {
    val context = LocalContext.current
    ArduinoBluetoothTheme {

        val navController = rememberNavController()


        val liveDataViewModel : ILiveData = MockLiveDataViewModel()

        DeviceLiveDataScreen(
            navController = navController,
            liveDataViewModel = liveDataViewModel
        )


    }
}









