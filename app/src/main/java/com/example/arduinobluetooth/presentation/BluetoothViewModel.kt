package com.example.arduinobluetooth.presentation

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arduinobluetooth.data.BluetoothController


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

import kotlin.text.Typography.dagger



class BluetoothUiState(
    val scannedDevices: MutableList<BluetoothDevice>
)


class BluetoothViewModel (
    private val bluetoothController: BluetoothController
): ViewModel() {




    private val _uiState = MutableStateFlow(BluetoothUiState(
        mutableStateListOf<BluetoothDevice>(),

    ));
    val uiState : StateFlow<BluetoothUiState> = _uiState.asStateFlow();

    fun startScan(context: Context) {
        bluetoothController.scanLeDevice(context)
    }

    fun stopScan(context: Context) {
        bluetoothController.stopScanLeDevice(context)
    }
}