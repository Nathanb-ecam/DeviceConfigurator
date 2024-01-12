package com.example.arduinobluetooth

sealed class Screen(val route:String) {
    object BlueScreen : Screen("blue")
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