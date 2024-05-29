package com.example.arduinobluetooth.presentation.viewmodels.mock

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.arduinobluetooth.bluetooth.BluetoothConfigData
import com.example.arduinobluetooth.login.ILoginViewModel
import com.example.arduinobluetooth.presentation.viewmodels.DeviceDataStatus
import com.example.arduinobluetooth.presentation.viewmodels.LoginUIState
import com.icure.sdk.model.Contact
import com.icure.sdk.model.DecryptedContact
import com.icure.sdk.model.DecryptedPatient
import com.icure.sdk.model.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockLoginViewModel : ILoginViewModel,ViewModel() {


    override val _uiState = MutableStateFlow(
        LoginUIState(
            true,
            DeviceDataStatus.INIT,
            BluetoothConfigData(
                "abdcef1234-abdcef1234",
                "a34ef12cd-abdcef1234",
                "password",
                ByteArray(0),
                ""
            )
        )
    )
    override val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()
    override suspend fun apiInitialize(apiUrl: String, username: String, password: String) {
        Log.i("MOCK LOGIN","apiInitalize")
    }

    override suspend fun createPatient(patient : DecryptedPatient): DecryptedPatient? {
        Log.i("MOCK LOGIN","createpatient")
        return null
    }

    override suspend fun createContact(patient: DecryptedPatient): DecryptedContact? {
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