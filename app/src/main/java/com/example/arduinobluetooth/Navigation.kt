package com.example.arduinobluetooth.presentation



import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material3.Scaffold
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
import com.example.arduinobluetooth.R
import com.example.arduinobluetooth.Screen
import com.example.arduinobluetooth.data.BottomNavItem
import com.example.arduinobluetooth.presentation.BluetoothScreen
import com.example.arduinobluetooth.presentation.BluetoothViewModel


@Composable
fun Navigation(navController : NavHostController, bluetoothViewModel: BluetoothViewModel, bottomNavItems : List<BottomNavItem>){
    NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }

        composable(route = Screen.BlueScreen.route) {
            ScreenScaffolder(navController = navController, bottomNavItems = bottomNavItems) {
                BluetoothScreen(navController = navController, blueViewModel = bluetoothViewModel)
            }

        }

        composable(route = Screen.HelpScreen.route) {
            ScreenScaffolder(navController = navController, bottomNavItems = bottomNavItems) {
                HelpScreen(navController = navController)
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
                items = bottomNavItems,
                navController = navController ,
                onItemClicked ={navController.navigate(it.route)}
            )
        }
    ) { innerPadding ->
        // Content of the screen goes here
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
    if (backStackEntry.value?.destination?.route !=Screen.LoginScreen.route){
        BottomNavigation(
            modifier = Modifier,
            backgroundColor = Color.DarkGray,
            elevation = 5.dp
        ){
            items.forEach{item->
                val selected = item.route == backStackEntry.value?.destination?.route
                BottomNavigationItem(
                    selected = selected,
                    onClick = { onItemClicked(item) },
                    selectedContentColor = Color(context.resources.getColor(R.color.black)),
                    unselectedContentColor = Color.Gray,
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon_path),
                            contentDescription = item.name
                        )
                    }
                )
            }
        }
    }

}


