package com.example.arduinobluetooth.presentation.viewmodels.mock

import android.util.Log
import com.example.arduinobluetooth.bluetooth.BluetoothConfigData
import com.example.arduinobluetooth.mqtt.Content
import com.example.arduinobluetooth.mqtt.ILiveData
import com.example.arduinobluetooth.mqtt.LiveSession
import com.example.arduinobluetooth.mqtt.MeasureDetails

import com.example.arduinobluetooth.mqtt.MeasureValue
import com.example.arduinobluetooth.mqtt.SensorDataContent
import kotlinx.coroutines.flow.MutableStateFlow

class MockLiveDataViewModel : ILiveData {
    companion object INFO {
        const val TAG = "Mock Live Data ViewModel"
    }

    override val liveData: MutableStateFlow<LiveSession> = createMockLiveSession()



    fun createMockLiveSession(): MutableStateFlow<LiveSession> {
        return MutableStateFlow(
            LiveSession(
                connected = true,
                subscribed = true,
                liveSensorData = mutableListOf(
                    SensorDataContent(Content(MeasureValue(MeasureDetails("22","°C","temperature")))),
                    SensorDataContent(Content(MeasureValue(MeasureDetails("21.8","°C","temperature")))),
                    SensorDataContent(Content(MeasureValue(MeasureDetails("21.6","°C","temperature")))),
                    SensorDataContent(Content(MeasureValue(MeasureDetails("21.3","°C","temperature")))),
                    SensorDataContent(Content(MeasureValue(MeasureDetails("20.9","°C","temperature"))))
                )
            )
        )
    }


    override fun setupMqtt(deviceSymmetricKey: ByteArray, topic:String) {
        Log.i(TAG,"setup Mqtt")
    }


    override fun closeMqttConnection(){
        Log.i(TAG,"closeMqttConnection")
    }
    override fun subscribe() {
        Log.i(TAG,"subscribing")
    }

    override fun unsubscribe() {
        Log.i(TAG,"unsubscribing")
    }



}
