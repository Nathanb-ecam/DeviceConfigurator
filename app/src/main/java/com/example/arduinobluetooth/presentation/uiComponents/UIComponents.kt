package com.example.arduinobluetooth.presentation.uiComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp




@Composable
fun Popup(buttonColors : ButtonColors, alertTitle : String, buttonText :String, onPopupClose : ()-> Unit){

    AlertDialog(
        modifier = Modifier
            .padding(8.dp),
        onDismissRequest = { },
        //shape = CircleShape,
        title = {
            Text(text = alertTitle)
        },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Column{
                    Button(
                        onClick = onPopupClose,
                        colors = buttonColors,
                    ) {
                        Text(text = buttonText)
                    }
                }
            }
        }
    )
}