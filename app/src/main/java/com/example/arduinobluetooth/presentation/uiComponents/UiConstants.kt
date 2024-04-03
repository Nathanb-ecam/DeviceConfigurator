package com.example.arduinobluetooth.presentation.uiComponents

import android.content.Context
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.arduinobluetooth.R


object iCureTextStyles{

    fun h1() : TextStyle{
        return TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
    fun h2() : TextStyle{
        return TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
    fun h3() : TextStyle{
        return TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }

    fun p() : TextStyle{
        return TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }

    fun pHint() : TextStyle{
        return TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Light,
            fontStyle = FontStyle.Normal,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }


}



object iCureButton {
    @Composable
    fun getButtonColors(context : Context): ButtonColors {
        return ButtonDefaults.buttonColors(
            containerColor = Color(context.resources.getColor(R.color.icure_green)),
            contentColor = Color.White
        )
    }
}


object iCureProgressIndicator{
    @Composable
    fun getCirculatorIndicator(context: Context){
        return CircularProgressIndicator(
            color = Color(context.resources.getColor(R.color.icure_green)),
            modifier = Modifier.wrapContentSize(),
        )
    }
}


object iCureTextFields{
    @Composable
    fun getICureTextFieldColors(context: Context) : TextFieldColors {
        return TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color(context.resources.getColor(R.color.icure_green)),
            cursorColor = Color(context.resources.getColor(R.color.icure_green)),
            focusedLabelColor = Color(context.resources.getColor(R.color.icure_green)),
            leadingIconColor = Color(context.resources.getColor(R.color.icure_green)),
        )
    }

    @Composable
    fun getiCureOutlinedTextFieldColors(context: Context) : TextFieldColors{
        return TextFieldDefaults.outlinedTextFieldColors(
            leadingIconColor = Color(context.resources.getColor(R.color.icure_green)),
            focusedBorderColor = Color(context.resources.getColor(R.color.icure_green)),
            unfocusedBorderColor = Color(context.resources.getColor(R.color.icure_green)),
            cursorColor = Color(context.resources.getColor(R.color.icure_green)),
            focusedLabelColor =Color(context.resources.getColor(R.color.icure_green)),
        )
    }

}

