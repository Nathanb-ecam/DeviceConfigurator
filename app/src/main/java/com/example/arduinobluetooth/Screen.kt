package com.example.arduinobluetooth

sealed class Screen(val route:String) {
    object BlueScreen : Screen("blue")
    object DeviceDetailScreen : Screen("device")

    object DeviceLiveDataScreen : Screen("device-live-data")
    object HelpScreen : Screen("help")
    object LoginScreen : Screen("login")


    fun withArgs(vararg args :String):String{
        return buildString {
            append(route)
            args.forEach {arg->
                append("/$arg")
            }
        }
    }
}