package com.example.arduinobluetooth.login

import com.example.arduinobluetooth.presentation.viewmodels.LoginUIState
import com.icure.sdk.model.Contact
import com.icure.sdk.model.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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