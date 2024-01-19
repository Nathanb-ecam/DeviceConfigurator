package com.example.arduinobluetooth.data

import android.bluetooth.BluetoothDevice

data class MyBluetoothDevice(val device : BluetoothDevice, var rssi : Int) {

}
