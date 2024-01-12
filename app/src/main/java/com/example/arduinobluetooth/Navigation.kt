package com.example.arduinobluetooth.presentation



import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.arduinobluetooth.Screen
import com.example.arduinobluetooth.presentation.BluetoothScreen
import com.example.arduinobluetooth.presentation.BluetoothViewModel


@Composable
fun Navigation(navController : NavHostController, bluetoothViewModel: BluetoothViewModel){
    NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {
        composable(route = Screen.LoginScreen.route) {

            LoginScreen(navController = navController)

        }

        composable(route = Screen.BlueScreen.route) {

            BluetoothScreen(navController = navController, blueViewModel = bluetoothViewModel)


        }
        /*        composable(route=Screen.DetailScreen.route){
            DetailScreen()
        }*/
        /*        composable(
            route=Screen.DetailScreen.route+ "/{product}"+"/{productType}",
            arguments = listOf(
                navArgument("product"){
                    type = NavType.StringType
                    defaultValue = "Not found"
                },
                navArgument("productType"){
                    type = NavType.StringType
                    defaultValue = "Not found"
                }
            )
        ){
            val stringProduct = it.arguments?.getString("product")!!
            val product = Json.decodeFromString<Product>(stringProduct)
            val stringProductType = it.arguments?.getString("productType")!!
            val productType = Json.decodeFromString<ProductType>(stringProductType)
            DetailScreen(productType,product,appViewModel= appViewModel,orderViewModel=orderViewModel)
        }
    }*/
    }
}








