package com.example.arduinobluetooth.utils

enum class BluetoothState {
    CONNECTED,
    READY_TO_CONFIGURE,
    DISCONNECTED,
    CONFIGURED // the device is disconnected since its configured
}