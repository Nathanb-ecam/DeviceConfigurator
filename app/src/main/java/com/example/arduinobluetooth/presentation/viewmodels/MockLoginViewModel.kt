package com.example.arduinobluetooth.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.arduinobluetooth.data.Bluetooth.BluetoothConfigData
import com.example.arduinobluetooth.data.Bluetooth.MyBluetoothDevice
import com.icure.sdk.model.Contact
import com.icure.sdk.model.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockLoginViewModel : ILoginViewModel,ViewModel() {


    override val _uiState = MutableStateFlow(
        LoginUIState(
            true,
            true,
            BluetoothConfigData(
                "abdcef1234-abdcef1234",
                "a34ef12cd-abdcef1234",
                "password",
                "1111111111111111111111111111111111111111111111111111111111111111"
            )
        )
    )
    override val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()
    override suspend fun apiInitialize(apiUrl: String, username: String, password: String) {
        Log.i("MOCK LOGIN","apiInitalize")
    }

    override suspend fun createPatient(firstName: String, lastName: String): Patient? {
        Log.i("MOCK LOGIN","createpatient")
        return null
    }

    override suspend fun createContact(patient: Patient): Contact? {
        Log.i("MOCK LOGIN","createContact")
        return null
    }

    override suspend fun getDeviceConfigData() {
        Log.i("MOCK LOGIN","getDeviceData")
    }

    override suspend fun handleKeyStorage() {
        Log.i("MOCK LOGIN","handleKeyStorage")
    }


}