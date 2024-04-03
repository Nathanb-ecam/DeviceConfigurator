package com.example.arduinobluetooth.mqtt


import com.example.arduinobluetooth.bluetooth.BluetoothConfigData
import kotlinx.coroutines.flow.StateFlow

interface ILiveData {
    val liveData : StateFlow<LiveSession>
    fun setupMqtt(deviceSymmetricKey: ByteArray)

    fun subscribe()

    fun unsubscribe()

    fun closeMqttConnection()



}