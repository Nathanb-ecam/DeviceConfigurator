package com.example.arduinobluetooth.utils

enum class BluetoothState {
    INIT,
    CONNECTED,
    READY_TO_CONFIGURE,
    DISCONNECTED,
    CONFIGURED // the device disconnected since its has been configured
}