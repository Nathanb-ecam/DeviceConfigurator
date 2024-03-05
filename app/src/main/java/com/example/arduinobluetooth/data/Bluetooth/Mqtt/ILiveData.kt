package com.example.arduinobluetooth.data.Bluetooth.Mqtt


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

interface ILiveData {
    val liveData : StateFlow<LiveSession>
    fun setupMqtt()

    fun subscribe()

    fun unsubscribe()

    fun closeMqttConnection()



}