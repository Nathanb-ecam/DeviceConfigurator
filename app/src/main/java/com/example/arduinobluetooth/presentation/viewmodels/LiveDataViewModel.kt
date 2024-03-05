package com.example.arduinobluetooth.presentation.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arduinobluetooth.data.Bluetooth.Mqtt.ILiveData
import com.example.arduinobluetooth.data.Bluetooth.Mqtt.IMqttController
import com.example.arduinobluetooth.data.Bluetooth.Mqtt.LiveSession
import com.example.arduinobluetooth.data.Bluetooth.Mqtt.SensorDataContent
import com.example.arduinobluetooth.utils.BluetoothState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn



class LiveDataViewModel(
    val context : Context,
    val mqttController: IMqttController
) : ViewModel(), ILiveData {

    companion object MQTT_INFO{
        const val topic = "icure_nano_topic/+"
    }


    override val liveData = mqttController.rtData.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        LiveSession(false, false, mutableListOf<SensorDataContent>())
    )


    override fun setupMqtt() {
        mqttController.setupMqtt()
    }

    override fun subscribe(){
        mqttController.subscribe(topic)
    }

    override fun unsubscribe(){
        mqttController.unsubscribe(topic)
    }

    override fun closeMqttConnection(){
        mqttController.closeMqttConnection()
    }


}