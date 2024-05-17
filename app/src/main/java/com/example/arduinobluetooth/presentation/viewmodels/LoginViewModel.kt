package com.example.arduinobluetooth.presentation.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.arduinobluetooth.R
import com.example.arduinobluetooth.bluetooth.BluetoothConfigData
import com.example.arduinobluetooth.login.ILoginViewModel
import com.example.arduinobluetooth.storage.MySharedPreferences
import com.icure.kryptom.crypto.RsaAlgorithm
import com.icure.kryptom.crypto.defaultCryptoService
import com.icure.kryptom.utils.hexToByteArray
import com.icure.kryptom.utils.toHexString
import com.icure.sdk.api.IcureApi
import com.icure.sdk.auth.UsernamePassword

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


enum class DeviceDataStatus{
    READY,
    ERROR,
    INIT
}
data class LoginUIState(
    val apiInitalized : Boolean,
    val deviceDataStatus: DeviceDataStatus,
    val deviceConfigData: BluetoothConfigData
)


class LoginViewModel(
    private val context: Context
) : ILoginViewModel,ViewModel() {

    override val _uiState = MutableStateFlow(LoginUIState(false,DeviceDataStatus.INIT,
        BluetoothConfigData("","","", ByteArray(0),"")
    ))
    override val uiState : StateFlow<LoginUIState> = _uiState.asStateFlow();
    

    private var privateKey =context.getString(R.string.privkey)

    private var icureApi : IcureApi? = null
    private var storage = VolatileStorageFacade()
    private var dataOwnerId = context.getString(R.string.dataOwnerId)
    private var cidToTestDecrypt = context.getString(R.string.cidToTestDecrypt)



    private var cid : String? = null
    private var userId : String? = null
    private var userPassword : String? = null
    private var symmetricKey : String? = null

    private val sharedPreferences: MySharedPreferences = MySharedPreferences(context)
    init {
        cid = sharedPreferences.cid
/*        Log.i("STORED",sharedPreferences.cid.toString())
        if(cid == null) cid = defaultCryptoService.strongRandom.randomUUID()
        sharedPreferences.cid = cid
        Log.i("STORING",sharedPreferences.cid.toString())*/

    }

    override suspend fun apiInitialize(apiUrl : String, username : String, password : String) {
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

    override suspend fun createPatient(patient : Patient) : Patient? {
        try{
            val createdPatient = icureApi!!.patient.encryptAndCreate(
                icureApi!!.patient.initialiseEncryptionMetadata(
                    patient
                )
            )
            return createdPatient

        }catch (e : Exception){
            e.printStackTrace()
            updateDataStatus(DeviceDataStatus.ERROR)
            Log.i("Patient","Error while craeting a patient")
        }
        return null
    }


    override suspend fun createContact(patient : Patient) : Contact?{
        try{

            val createdContact = icureApi!!.contact.encryptAndCreate(
                icureApi!!.contact.initialiseEncryptionMetadata(
                    Contact(
                        id = sharedPreferences.cid!!,
                        descr = "ASCASDFASDASDQWEQW",
                    ),
                    patient
                )
            )
            return createdContact
        }catch (e : Exception){
            cid = defaultCryptoService.strongRandom.randomUUID()
            sharedPreferences.cid = cid
            e.printStackTrace()
            Log.i("Contact","Error while creating contact")
        }
        return null
    }

    @OptIn(InternalIcureApi::class)
    override suspend fun getDeviceConfigData() {
        icureApi?.let {

            val patient = createPatient(
                Patient(id = UUID.randomUUID().toString(), firstName = "Jean", lastName = "Jacques", note = "Yeet")
            )
            if(patient == null){
                Log.i("ICURE API","Couldn't get patient")
                updateDataStatus(DeviceDataStatus.ERROR)
                return
            }

            val createdContact = createContact(patient)
            if(createdContact == null){
                Log.i("ICURE API","Couldn't create contact")
                updateDataStatus(DeviceDataStatus.ERROR)
                return
            }


            createdContact.let {
                symmetricKey = getContactSymmetricKey(createdContact)
                /*symmetricKey = icureApi!!.contact.getEncryptionKeyOf(createdContact).s*/
                val byteArrayKey = hexToByteArray(symmetricKey!!)


                /*Log.i("KEY", symmetricKey.toString())*/
                symmetricKey?.let {
                    try {
                        val currentState = _uiState.value;
                        val topic = context.getString(R.string.topic)
                        // users configured a new device , a new cid has to be used
                        val newCid = defaultCryptoService.strongRandom.randomUUID()
                        sharedPreferences.cid = newCid

                        val bluetoothConfigData = BluetoothConfigData(newCid,userId!!,userPassword!!,byteArrayKey,topic) // testKey!!
                        _uiState.value = currentState.copy(deviceConfigData = bluetoothConfigData,deviceDataStatus = DeviceDataStatus.READY)
                        Log.i("ICURE DATA CONFIG","Got device config data")
                        Log.i("ICURE DATA CONFIG", sharedPreferences.cid.toString())
                        /*testDecryption()*/
                    }catch (e : Exception){
                        updateDataStatus(DeviceDataStatus.ERROR)
                        Log.i("ICURE DATA CONFIG","Couldn't get device config data")
                    }
                }
            }?:Log.i("ICURE API","Couldn't get contact ")
        }?: Log.i("ICURE API", "Icure API not itialized")
    }

    suspend fun getContactSymmetricKey(contact: Contact) : String?{
        icureApi?.let{
            return icureApi!!.contact.getEncryptionKeyOf(contact).s
        }?: return null

    }

    suspend fun getContactById(contactId : String) : Contact?{
        icureApi?.let{
            return icureApi!!.contact.getAndDecrypt(contactId)
        }?: return null
    }
    fun updateDataStatus(status : DeviceDataStatus){
        val currentState = _uiState.value;
        _uiState.value = currentState.copy(deviceDataStatus = status)
    }

    @OptIn(InternalIcureApi::class)
    override suspend fun handleKeyStorage(){
        val icureFacade = IcureStorageFacade(
            JsonAndBase64KeyStorage(storage),
            storage,
            DefaultStorageEntryKeysFactory,
            defaultCryptoService,
            false
        )
        icureFacade.saveEncryptionKeypair(
            dataOwnerId,
            defaultCryptoService.rsa.loadKeyPairPkcs8(RsaAlgorithm.RsaEncryptionAlgorithm.OaepWithSha256, hexToByteArray(privateKey)),
            true
        )
    }



    suspend fun testDecryption(){
        icureApi?.let {
            try {
                val retrievedContact1 = icureApi!!.contact.getAndDecrypt(cidToTestDecrypt)

                Log.i("Test Decryption",retrievedContact1.toString())

            }catch (e : Exception){
                e.printStackTrace()
                Log.i("Test Decryption",e.toString())
            }

        }

    }
}