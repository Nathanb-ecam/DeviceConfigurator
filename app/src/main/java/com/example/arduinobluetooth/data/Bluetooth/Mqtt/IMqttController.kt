package com.example.arduinobluetooth.data.Bluetooth.Mqtt

import android.content.Context
import android.util.Log
import com.example.arduinobluetooth.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.io.FileInputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

interface IMqttController {
        val rtData : StateFlow<LiveSession>
        fun createSSLSocketFactory(caCertPath: String): SSLSocketFactory
        fun getMqttClientOptions() : MqttConnectOptions

        fun setupMqtt()
        fun createMqttClient(context: Context, brokerURL : String, clientId : String ="kotlin_client" ) : MqttAndroidClient?

        fun connectBroker(mqttClient: MqttAndroidClient?,options : MqttConnectOptions)
        fun subscribe(topic: String, qos: Int = 1)

        fun unsubscribe(topic : String)

        fun closeMqttConnection()
}
