package com.example.arduinobluetooth.presentation.uiComponents


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.arduinobluetooth.R


@Composable
fun Popup(
    buttonColors : ButtonColors = ButtonDefaults.buttonColors(contentColor = Color.White),
    alertTitle : String,
    buttonText :String,
    onPopupClose : ()-> Unit,
    alertIcon : ImageVector? = null,
    iconColor : Color = Color.Unspecified,
    modifier : Modifier = Modifier
){
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { },
        //shape = CircleShape,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                alertIcon?.let {
                    Icon(
                        imageVector = alertIcon,
                        contentDescription = "lo",
                        tint = iconColor,
                        modifier = Modifier.size(80.dp))
                }
            }
        },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                /*horizontalArrangement = Arrangement.Center*/
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(text= alertTitle, style = iCureTextStyles.h2(), fontWeight = FontWeight.Normal)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onPopupClose,
                        shape = RectangleShape,
                        colors = buttonColors,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = buttonText)
                    }
                }
            }
        }
    )
}