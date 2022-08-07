package com.example.android.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android.calculator.ui.theme.Background
import com.example.android.calculator.ui.theme.CalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Calculator()
                }
            }
        }
    }
}


@Composable
fun Calculator(viewModel: CalculatorViewModel = viewModel()) {
    val displayContentState = viewModel.displayContent.observeAsState()
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Background)
            .padding(bottom = 10.dp)
    ) {
        Text(
            text = displayContentState.value ?: "",
            fontSize = 46.sp,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
                .wrapContentWidth(Alignment.End)
        )
        val keyData = viewModel.getKeyData()
        keyData.forEachIndexed { i, strings ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                strings.forEachIndexed { j, _ ->
                    val data = keyData[i][j]
                    KeyView(keyValue = data)
                }
            }
        }
    }
}

@Composable
fun KeyView(
    keyValue: Pair<String, Color>,
    viewModel: CalculatorViewModel = viewModel()
) {
    Text(
        text = keyValue.first,
        color = Color.White,
        fontSize = 30.sp,
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }) {
                viewModel.processKey(keyValue.first)
            }
            .width(60.dp)
            .height(60.dp)
            .background(shape = CircleShape, color = keyValue.second)
            .wrapContentSize(align = Alignment.Center)
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CalculatorTheme {
        Calculator()
    }
}