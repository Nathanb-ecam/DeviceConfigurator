package com.example.arduinobluetooth.data.Bluetooth.Mqtt

import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection

import com.example.arduinobluetooth.R
import com.example.arduinobluetooth.data.Bluetooth.MyBluetoothDevice
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.io.ByteArrayInputStream
import java.io.EOFException
import java.io.FileInputStream
import java.io.InputStream
import java.security.KeyFactory
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory


data class LiveSession(
    val connected : Boolean,
    val subscribed: Boolean,
    val liveSensorData : MutableList<SensorDataContent>
)

class MqttController(val context: Context) : IMqttController {

    private val caCertStream = context.resources.openRawResource(R.raw.ca)
    private val clientCertStream = context.resources.openRawResource(R.raw.client_cert)
    private val clientKeyStream = context.resources.openRawResource(R.raw.client_key)

    private var options : MqttConnectOptions? = null

    private var mqttClient  : MqttAndroidClient? = null
    companion object MQTT{
        const val TAG = "MQTT"
        const val topic = "icure_nano_topic/+"
    }


    private val _rtData = MutableStateFlow(
        LiveSession(false, false, mutableListOf<SensorDataContent>())
    )

    override val rtData = _rtData.asStateFlow()


    private fun getPrivateKey(inputStream: InputStream): PrivateKey {
        val keyFactory = KeyFactory.getInstance("RSA")
        val encodedKeyBytes = inputStream.readBytes()
        val keySpec = PKCS8EncodedKeySpec(encodedKeyBytes)
        return keyFactory.generatePrivate(keySpec)
    }

    override fun createSSLSocketFactory(oneWaySSL : Boolean): SSLSocketFactory? {
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")

            val caCert = certificateFactory.generateCertificate(caCertStream) as X509Certificate
            val clientCert = certificateFactory.generateCertificate(clientCertStream) as X509Certificate
            val privateKey = getPrivateKey(clientKeyStream)

            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null, null)
            keyStore.setCertificateEntry("caCert", caCert)
            keyStore.setCertificateEntry("clientCert", clientCert)
            keyStore.setKeyEntry("clientKey", privateKey, charArrayOf(), arrayOf(clientCert))

            val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(keyStore, charArrayOf())


            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustManagerFactory.trustManagers, null)



            val hostnameVerifier = HostnameVerifier { hostname, _ ->
                hostname == context.resources.getString(R.string.broker_DN) //|| hostname.endsWith(".mosquitto.org")
            }
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier)

            return sslContext.socketFactory
        }catch (e:Exception){
            e.printStackTrace()
        }


        return null


    }
    override fun getMqttClientOptions() : MqttConnectOptions{

        val mqttConnectOptions = MqttConnectOptions().apply {
            userName = "iCureIoTUser"
            password = "iCureIoTPassword".toCharArray()
            /*isAutomaticReconnect = true*/
            isCleanSession = true
            /*socketFactory =createSSLSocketFactory()*/

        }
        return mqttConnectOptions
    }


    override fun setupMqtt(){
        //1 Create client
        //2 connect client to broker
        //3 subscribe to topic
        if(mqttClient == null){
            val broker = context.getString(R.string.broker_url)
            mqttClient = createMqttClient(context,broker,MqttAsyncClient.generateClientId())
        }

        if(!_rtData.value.connected){
            if(options == null){
                options = getMqttClientOptions()

            }
            options?.let {
                connectBroker(mqttClient,options!!)
            }

        }else{
            if(!_rtData.value.subscribed) {
                subscribe(topic)
            }
        }






    }



    override fun connectBroker(mqttClient: MqttAndroidClient?,options : MqttConnectOptions){
        try {
            mqttClient?.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.i(TAG, "Connection success")
                    _rtData.update { currentState ->
                        currentState.copy(connected = true)
                    }
                    Log.i(TAG, "Attempting to subscribe ... ")
                    subscribe(topic,1)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.i(TAG, "Connection failure")
                    exception?.printStackTrace()
                    _rtData.update { currentState ->
                        currentState.copy(connected = false)
                    }
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }


    override fun createMqttClient(context: Context, brokerURL : String, clientId : String ) : MqttAndroidClient?{
        return try {
            val mqttClient = MqttAndroidClient(context, brokerURL , clientId)

            mqttClient.setCallback(object : MqttCallback {
                val mapper = ObjectMapper()
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.i(TAG, "Receive message: ${message.toString()} from topic: $topic")
                    _rtData.value = _rtData.value.copy(
                        liveSensorData = _rtData.value.liveSensorData.toMutableList().apply {
                            val payload = message?.payload ?: return
                            try {
                                val packet = mapper.readValue(payload, DevicePacket::class.java)
                                add(packet.data)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    )
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.i(TAG, "Connection lost ${cause.toString()}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {

                }
            })
            mqttClient
        } catch (e : Exception){
            Log.i(TAG,e.toString())
            null
        }
    }


    override fun subscribe(topic: String, qos: Int) {
        try {
            mqttClient?.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Subscribed to $topic")
                    _rtData.update {currentState ->
                        currentState.copy(subscribed = true)
                    }
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    try {
                        // Code that might throw NullPointerException
                        Log.d(TAG, "Failed to subscribe $topic")
                        _rtData.update { currentState ->
                            currentState.copy(subscribed = false)
                        }

                    } catch (e: NullPointerException) {
                        closeMqttConnection()
                        setupMqtt()
                        Log.e(TAG, "NullPointerException occurred: ${e.message}")
                    }
                }
            })
        }/*catch(e : EOFException){

            setupMqtt()
        }*/
        catch (e: Exception) {

            e.printStackTrace()
        }
    }


    override fun unsubscribe(topic: String){
        try {
            mqttClient?.unsubscribe(topic, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    _rtData.update { currentState->
                        currentState.copy(subscribed = false)
                    }
                    Log.i(TAG, "Unsubscribe successful: ok")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.i(TAG,"Failed to unsubscribe")
                }
            })

        } catch (e : Exception){
            Log.i("MQTT","Unsubscribe error")
            e.printStackTrace()

        }
    }

    override fun closeMqttConnection(){
        Log.i(TAG,"Should close the conneciton")
        _rtData.update {currentState->
            currentState.copy(connected = false, subscribed = false)

        }
        mqttClient = null
    }

}

