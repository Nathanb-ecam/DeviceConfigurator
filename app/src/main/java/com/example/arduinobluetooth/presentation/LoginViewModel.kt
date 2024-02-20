package com.example.arduinobluetooth.presentation

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import com.example.arduinobluetooth.R
import com.example.arduinobluetooth.data.BluetoothConfigData
import com.example.arduinobluetooth.utils.BluetoothState
import com.icure.kryptom.crypto.RsaAlgorithm
import com.icure.kryptom.crypto.defaultCryptoService
import com.icure.kryptom.utils.hexToByteArray
import com.icure.sdk.api.IcureApi
import com.icure.sdk.auth.UsernamePassword

import com.icure.sdk.model.HexString
import com.icure.sdk.model.Patient
import com.icure.sdk.model.Contact
import com.icure.sdk.storage.IcureStorageFacade
import com.icure.sdk.storage.impl.DefaultStorageEntryKeysFactory
import com.icure.sdk.storage.impl.JsonAndBase64KeyStorage
import com.icure.sdk.storage.impl.VolatileStorageFacade
import com.icure.sdk.utils.InternalIcureApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


import java.util.UUID

data class LoginUIState(
    val apiInitalized : Boolean,
    val deviceDataReady: Boolean,
    val deviceConfigData: BluetoothConfigData
)


class LoginViewModel(
    val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUIState(false,false,BluetoothConfigData("","","","")))
    val uiState : StateFlow<LoginUIState> = _uiState.asStateFlow();
    


    private var privateKey ="308204be020100300d06092a864886f70d0101010500048204a8308204a40201000282010100d6a61b68a0b0a1bb11a903a5ecfd39af4cec602ec6c75c45b689c19663da481a879926176f2da6f9a88b9de8dd70b10170b0050a65b122e72c3eb1d579b48ebf3d399259be711505a16c3ca06511836ad1504900dbec5ca14b6775431176f0eae3ad5149111c7fc827429b871647f7d0b9e4907802e2b5f292e7516c86ba4eb7e07112deda4c25284b33fa6dd6d05a5551fd53dae64eb17bf5225c2b330e47aea88a97a887ff086f5deb90b93fb20de03eed721d0fec5121e98c1255e8564ae2ad3ef7636a794435d509c0bb2a65a297d79aeb76bbbc097d27a0386c99d84f02b5654150241610f3d95bbd5ea3de69d1a3fc1c6772b0e765c09542b35173588d02030100010282010005e53a0b7d9e5c52cf015d60494a0b338d6150a2f842b17e18ad5febc9824d48ad40bf931532a3cb679dae109f4461183108249b4c7fb8fdb0716860fc9ec0ecefb98d1379ef912c3e6ac29f85271dfce912d36e7a9bdb4f72cabca5c2ed0960ea2ff073994cb715c03e4c1741bd68490b5203c5c80e8200cf981adc6836e5c28a3f1bd3d84b752f01d98979ebfc59d2245bddeb730f58948c11096bb0f24bdc353b6ab5cc1907d13c02e0562aa2112f33e14e9d07b829c9efdb6c8b5a556db3ea9bf8468ebc7631222d96ad8cbac2c6b0901dfe1bf31644561b53289b5b0f00a2555a96ea425001bd3d0bcbedc6955c0bcb8b1b40bb2aafb3da09c4da77650102818100eacde3860902f79d13eadad77245e011bf38c6b7d7c2bf7e74f79964f388258c79c9dfb9343997d493f13b445e40cec97dbd4b99c8740868d6a5c71a6c919e538cc45c58b2e5be9f9de50af4b8caa5ba5ecd696cf9f7e7fecd78d8c2f8e95cb11611f1a981c959cf0154bc6ea3def07dd6fbb9f603de55759c829ed5f36ad9a902818100ea06720e46937df9b7c6f1959dacec418e7ff5e8e40d225c69c14e0c7425e05aea8a1d064098827ca98913512fea721bb926b18ee4e1e206356883284d021bdeba1858b0f92351974cf16368a94ff6f84dbca58fbd0b7edf943a893382d4713989fe6a59b69ae65ab61118b132d734afcbec0f8e782647930d11d738ecc9fe450281806a4a477bae6aa4727bc8adc627999004189ef7fdd2f2fac2a0ea508b0d5f0d38590d54743f3aef0b30e95b9de858c80318236facaebc2fee5d746ffbfb37b407384acf4eebb4eaa4d0d2153290d053535984fcfb72f091199c23df63eb4fa0411daf83c8ead29e1002173cb18d06e7b0df77d647929abbeee0a994835f8c77f902818100b12241d046613b17f99f6e9a45676ec7cc1954c37b61e9a13336ea6188c46685ca42d1e73a9b7520f09ddd6e352b6a454ba65afd02876ab23258b1f59e1c3b86212b6e6e3d951b1bbf54725ef4d09ee9a0c56a375aeac9946738cb429f0337d44a7bb479c53a87a31f6ddf57b858a5fb6e097658eade668c8a76f78591ccee5502818100ca27933c75dbc1044c9c4a8d173f425fa17f0ce9eeded84da86cf19a93a3499cff1698bb9b2278f2059839dd29e89dbd6c865300b5880d800a247c1f388558bc5f8334d84bdecc23fb125dabc09dc047e9a883ce1064946fd41013d9fc50484e7331ec89bc18b46a8a39df09c9ec2de719d8a4ae27dd7a87b4a265a751772f2c"

    private var icureApi : IcureApi? = null
    private var storage = VolatileStorageFacade()
    private var dataOwnerid = context.getString(R.string.dataOwnerId)


    private var patient : Patient? = null
    private var contact : Contact? = null



    private val cid = defaultCryptoService.strongRandom.randomUUID()
    private var userId : String? = null
    private var userPassword : String? = null
    private var symmetricKey : String? = null


    suspend fun apiInitialize(apiUrl : String, username : String, password : String) {
        handleKeyStorage()
        userId = username
        userPassword = password
        try {
            icureApi = IcureApi.initialise(
                apiUrl,
                UsernamePassword(username, password),
                storage
            )
            val currentState =_uiState.value
            _uiState.value = currentState.copy(apiInitalized = true)
            Log.i("ICURE API INIT", "Api successfuly initalized")
        }catch(e : Exception){
            Log.i("ICURE API INIT",e.toString())
        }
    }

    suspend fun createPatient(firstName: String, lastName : String) : Patient? {
        try{
            val createdPatient = icureApi!!.patient.encryptAndCreate(
                icureApi!!.patient.initialiseEncryptionMetadata(
                    Patient(
                        id = UUID.randomUUID().toString(),
                        firstName = firstName,
                        lastName = lastName,
                        note = "Yeet"
                    )
                )
            )
            createdPatient?.let {
                val retrievedPatient = icureApi!!.patient.getAndDecrypt(createdPatient.id)
                patient = retrievedPatient
                return retrievedPatient
            }
        }catch (e : Exception){
            Log.i("Patient","Error while craeting a patient")
        }


        return null
    }


    suspend fun createContact(patient : Patient) : Contact?{
        try{
            val createdContact = icureApi!!.contact.encryptAndCreate(
                icureApi!!.contact.initialiseEncryptionMetadata(
                    Contact(
                        id = cid,
                        descr = "ASCASDFASDASDQWEQW",
                    ),
                    patient
                )
            )
            createdContact?.let {
                val retrievedContact = icureApi!!.contact.getAndDecrypt(createdContact.id)
                contact = retrievedContact
                return  retrievedContact
            }
        }catch (e : Exception){
            Log.i("Contact","Error while creating contact")
        }

        return null
    }

    @OptIn(InternalIcureApi::class)
    suspend fun getDeviceConfigData() {
        icureApi?.let {
            val patient = createPatient("Jean","Jacques")
            if(patient == null){
                Log.i("ICURE API","Couldn't get patient")
                return
            }

            val createdContact = createContact(patient)
            if(createdContact == null){
                Log.i("ICURE API","Couldn't create contact")
                return
            }


            createdContact.let {
                symmetricKey = icureApi!!.contact.getEncryptionKeyOf(createdContact).s
                /*Log.i("KEY", symmetricKey.toString())*/
                symmetricKey?.let {
                    try {
                        val currentState = _uiState.value;
                        val bluetoothConfigData = BluetoothConfigData(cid,userId!!,userPassword!!,symmetricKey!!) // testKey!!
                        _uiState.value = currentState.copy(deviceConfigData = bluetoothConfigData,deviceDataReady = true)
                        Log.i("ICURE DATA CONFIG","Got device config data")
                        testDecryption()
                    }catch (e : Exception){
                        Log.i("ICURE DATA CONFIG","Couldn't get device config data")
                    }
                }
            }?:Log.i("ICURE API","Couldn't get contact ")
        }
    }


    @OptIn(InternalIcureApi::class)
    suspend fun handleKeyStorage(){
        val icureFacade = IcureStorageFacade(
            JsonAndBase64KeyStorage(storage),
            storage,
            DefaultStorageEntryKeysFactory,
            defaultCryptoService,
            false
        )
        icureFacade.saveEncryptionKeypair(
            dataOwnerid,
            defaultCryptoService.rsa.loadKeyPairPkcs8(RsaAlgorithm.RsaEncryptionAlgorithm.OaepWithSha256, hexToByteArray(privateKey)),
            true
        )
    }



    suspend fun testDecryption(){
        icureApi?.let {
            try {

                val cidToCheck = context.getString(R.string.cidToTestDecrypt)
                val retrievedContact = icureApi!!.contact.getAndDecrypt(cidToCheck)
                Log.i("Test Decryption",retrievedContact.toString())


            }catch (e : Exception){
                Log.i("Test Decryption",e.toString())
            }

        }

    }



}