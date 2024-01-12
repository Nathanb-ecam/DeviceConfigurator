package com.example.arduinobluetooth.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.arduinobluetooth.R
import com.example.arduinobluetooth.Screen


@OptIn(ExperimentalGraphicsApi::class)
@Composable
fun LoginScreen(navController: NavController){

    // need to fetch all authorized users

    val context = LocalContext.current


/*    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        leadingIconColor = Color(context.resources.getColor(R.color.red)),
        focusedBorderColor = Color(context.resources.getColor(R.color.red)),
        unfocusedBorderColor = Color(context.resources.getColor(R.color.red)),
        cursorColor = Color(context.resources.getColor(R.color.red)),
        focusedLabelColor =Color(context.resources.getColor(R.color.red)),
    )*/


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

        Card(
            modifier = Modifier
                .fillMaxWidth().
                padding(18.dp),
        ){
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)){
                OutlinedTextField(
                    value = username,
                    onValueChange = { username=it },
                    label = {Text("Nom d'utilisateur")},
                    textStyle = LocalTextStyle.current.copy(color = Color.Gray),
                    //colors = textFieldColors,
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
                    //colors = textFieldColors,
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
                        //backgroundColor = Color(context.resources.getColor(R.color.red)),
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

}