package com.example.arduinobluetooth.presentation.viewmodels

import android.util.Log
import com.example.arduinobluetooth.R
import com.example.arduinobluetooth.data.Bluetooth.BluetoothConfigData
import com.icure.kryptom.crypto.RsaAlgorithm
import com.icure.kryptom.crypto.defaultCryptoService
import com.icure.kryptom.utils.hexToByteArray
import com.icure.sdk.api.IcureApi
import com.icure.sdk.auth.UsernamePassword
import com.icure.sdk.model.Contact
import com.icure.sdk.model.Patient
import com.icure.sdk.storage.IcureStorageFacade
import com.icure.sdk.storage.impl.DefaultStorageEntryKeysFactory
import com.icure.sdk.storage.impl.JsonAndBase64KeyStorage
import com.icure.sdk.storage.impl.VolatileStorageFacade
import com.icure.sdk.utils.InternalIcureApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

interface ILoginViewModel {

    val _uiState : MutableStateFlow<LoginUIState>
    val uiState : StateFlow<LoginUIState>
    suspend fun apiInitialize(apiUrl : String, username : String, password : String)



    suspend fun createPatient(firstName: String, lastName : String) : Patient?


    suspend fun createContact(patient : Patient) : Contact?


    suspend fun getDeviceConfigData()



    suspend fun handleKeyStorage()



    suspend fun testDecryption(){}
}