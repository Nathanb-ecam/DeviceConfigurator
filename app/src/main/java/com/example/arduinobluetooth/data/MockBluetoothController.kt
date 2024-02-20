package com.example.arduinobluetooth.data

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.example.arduinobluetooth.utils.BluetoothState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockBluetoothController : IBluetoothController {

    private val _scannedDevices = MutableStateFlow<List<MyBluetoothDevice>>(emptyList())

    private val _connectionState = MutableStateFlow(BluetoothState.INIT)


    init {
        val mockDevices = listOf(
            MyBluetoothDevice("Mock-Mac","AF-DA-AD-DA-DA", -50),
            MyBluetoothDevice("Mock-iCure-device","AF-11-33-DA-DA", -60),
            MyBluetoothDevice("Mock-Headphones","0F-78-DA-3E", -70)
        )
        _scannedDevices.value = mockDevices
        _connectionState.value = BluetoothState.CONNECTED

    }

    override val connectionState: StateFlow<BluetoothState>
        get() = TODO("Not yet implemented")
    override val scannedDevices: StateFlow<List<MyBluetoothDevice>>
        get() = TODO("Not yet implemented")

    override fun scanLeDevice(context: Context) {
        TODO("Not yet implemented")
    }

    override fun stopScanLeDevice(context: Context) {
        TODO("Not yet implemented")
    }

    override fun disconnectDevice() {
        TODO("Not yet implemented")
    }

    override fun deleteSearchResults() {
        TODO("Not yet implemented")
    }

    override fun connectDevice(deviceAddress: String) {
        TODO("Not yet implemented")
    }

    override fun testDeviceConnection() {
        TODO("Not yet implemented")
    }

    override fun configureArduinoDevice(configData: BluetoothConfigData) {
        TODO("Not yet implemented")
    }

    override fun updateConnectedState(state: BluetoothState) {
        TODO("Not yet implemented")
    }

}
