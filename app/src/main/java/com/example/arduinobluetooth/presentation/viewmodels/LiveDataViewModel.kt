package com.example.arduinobluetooth.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arduinobluetooth.bluetooth.BluetoothConfigData
import com.example.arduinobluetooth.mqtt.ILiveData
import com.example.arduinobluetooth.mqtt.IMqttController
import com.example.arduinobluetooth.mqtt.LiveSession
import com.example.arduinobluetooth.mqtt.SensorDataContent
import kotlinx.coroutines.flow.SharingStarted
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


    override fun setupMqtt(deviceSymmetricKey: ByteArray,topic : String) {
        mqttController.setupMqtt(deviceSymmetricKey,topic)
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