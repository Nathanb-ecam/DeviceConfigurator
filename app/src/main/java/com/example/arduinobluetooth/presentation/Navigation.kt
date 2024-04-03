package com.example.arduinobluetooth.presentation



import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.material.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.arduinobluetooth.R
import com.example.arduinobluetooth.mqtt.ILiveData
import com.example.arduinobluetooth.presentation.appscreens.BluetoothScreen
import com.example.arduinobluetooth.presentation.appscreens.DeviceDetailScreen
import com.example.arduinobluetooth.presentation.appscreens.DeviceLiveDataScreen
import com.example.arduinobluetooth.presentation.appscreens.HelpScreen
import com.example.arduinobluetooth.presentation.appscreens.LoginScreen
import com.example.arduinobluetooth.presentation.viewmodels.BluetoothViewModel
import com.example.arduinobluetooth.presentation.viewmodels.LiveDataViewModel
import com.example.arduinobluetooth.presentation.viewmodels.LoginViewModel


@Composable
fun Navigation(navController : NavHostController, bluetoothViewModel: BluetoothViewModel,liveDataViewModel: LiveDataViewModel, loginViewModel: LoginViewModel, bottomNavItems : List<BottomNavItem>){
    NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController,loginViewModel = loginViewModel)
        }

        composable(route = Screen.BlueScreen.route) {
            ScreenScaffolder(navController = navController, bottomNavItems = bottomNavItems) {
                BluetoothScreen(navController = navController, loginViewModel = loginViewModel, blueViewModel = bluetoothViewModel)
            }
        }
        composable(
            route = Screen.DeviceDetailScreen.route+"/{deviceAddress}",
            arguments = listOf(
                navArgument("deviceAddress"){
                    type = NavType.StringType
                    defaultValue = "Not found"
                },
            )
        ) {
            val deviceAddress = it.arguments?.getString("deviceAddress")
            ScreenScaffolder(navController = navController, bottomNavItems = bottomNavItems) {
                DeviceDetailScreen(navController = navController,blueViewModel = bluetoothViewModel,loginViewModel = loginViewModel, deviceAddress = deviceAddress)
            }
        }

        composable(route = Screen.DeviceLiveDataScreen.route) {
            ScreenScaffolder(navController = navController, bottomNavItems = bottomNavItems) {
                DeviceLiveDataScreen(
                    navController = navController,
                    liveDataViewModel = liveDataViewModel as ILiveData,
                    loginViewModel = loginViewModel
                )
            }
        }

        composable(route = Screen.HelpScreen.route) {
            ScreenScaffolder(navController = navController, bottomNavItems = bottomNavItems) {
                HelpScreen(navController = navController,loginViewModel = loginViewModel)
            }
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


@Composable
fun ScreenScaffolder(
    navController: NavHostController,
    bottomNavItems: List<BottomNavItem>,
    content: @Composable (PaddingValues) -> Unit
) {

    Scaffold(
        /*topBar = {
            TopNavigationBar(navController = navController)
        },*/
        bottomBar = {
            BottomNavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.1f),

                items = bottomNavItems,
                navController = navController ,
                onItemClicked ={navController.navigate(it.route)}
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}


@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClicked : (BottomNavItem)->Unit
){
    val context = LocalContext.current
    val backStackEntry = navController.currentBackStackEntryAsState()
    // if we are currently on the loginscreen, we don't want to have bottom bar
    if (backStackEntry.value?.destination?.route != Screen.LoginScreen.route){
        BottomNavigation(
            modifier = Modifier,
            backgroundColor = Color(context.resources.getColor(R.color.icure_black)),
            elevation = 5.dp
        ){
            items.forEach{item->
                val selected = item.route == backStackEntry.value?.destination?.route
                BottomNavigationItem(
                    selected = selected,
                    onClick = { onItemClicked(item) },
                    selectedContentColor = Color(context.resources.getColor(R.color.icure_green)),
                    unselectedContentColor = Color(context.resources.getColor(R.color.icure_white)),
                    icon = {
                        Icon(imageVector = item.icon, contentDescription =item.name )
                    }
                )
            }
        }
    }

}


