package com.example.arduinobluetooth.presentation.appscreens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.arduinobluetooth.R
import com.example.arduinobluetooth.Screen
import com.example.arduinobluetooth.ui.theme.ArduinoBluetoothTheme


@OptIn(ExperimentalGraphicsApi::class)
@Composable
fun LoginScreen(navController: NavController){

    // need to fetch all authorized users

    val context = LocalContext.current


    val textFieldColors = androidx.compose.material.TextFieldDefaults.outlinedTextFieldColors(
        leadingIconColor = Color(context.resources.getColor(R.color.icure_green)),
        focusedBorderColor = Color(context.resources.getColor(R.color.icure_green)),
        unfocusedBorderColor = Color(context.resources.getColor(R.color.icure_green)),
        cursorColor = Color(context.resources.getColor(R.color.icure_green)),
        focusedLabelColor =Color(context.resources.getColor(R.color.icure_green)),
    )


    var username by rememberSaveable() { mutableStateOf("") }
    var password by rememberSaveable() { mutableStateOf("") }
    //val uiState by userViewModel.uiState.collectAsState()



 /*   LaunchedEffect(uiState.loggedIn){
        if (uiState.loggedIn){
            navController.navigate(Screen.MenuScreen.route)
        }
    }*/

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(Color(context.resources.getColor(R.color.icure_white)))
                .padding(16.dp)){
                
                
                Image(
                    painter = painterResource(R.drawable.icure_logo),
                    contentDescription ="icure logo",
                    modifier = Modifier.fillMaxWidth()
                )



                OutlinedTextField(
                    value = username,
                    onValueChange = { username=it },
                    label = {Text("Nom d'utilisateur")},
                    textStyle = LocalTextStyle.current.copy(color = Color.Gray),
                    colors = textFieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),

                    )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password=it },
                    label = {Text("Mot de passe")},
                    textStyle = LocalTextStyle.current.copy(color = Color.Gray),
                    colors = textFieldColors,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),

                    )

                Button(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(context.resources.getColor(R.color.icure_green)),
                        contentColor = Color.White
                    ),
                    onClick = {
                        //userViewModel.authentificate(orderViewModel, Login(username,password));
                        navController.navigate(Screen.BlueScreen.route)
                        Log.i("Credentials","${username} ${password}")

                    }){
                    Text(text="Envoyer")
                }
            }



    }
}


@Preview(showBackground = true)
@Composable
fun LoginPreview() {

    ArduinoBluetoothTheme {
        val navController = rememberNavController()

        LoginScreen(navController = navController)


    }
}
