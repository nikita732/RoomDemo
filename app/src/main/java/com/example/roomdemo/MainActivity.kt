package com.example.roomdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roomdemo.ui.theme.RoomDemoTheme
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.roomdemo.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoomDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val owner = LocalViewModelStoreOwner.current
                    owner?.let {
                        val viewModel: MainViewModel = viewModel(
                            it,
                            "MainViewModel",
                            MainViewModelFactory(
                                LocalContext.current.applicationContext
                                        as Application
                            )
                        )
                        ScreenSetup(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenSetup(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val allProducts by viewModel.allProducts.observeAsState(listOf())
    val searchResults by viewModel.searchResults.observeAsState(listOf())

    MainScreen(
        modifier = modifier,
        allProducts = allProducts,
        searchResults = searchResults,
        viewModel = viewModel
    )
}



@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    allProducts: List<Product>,
    searchResults: List<Product>,
    viewModel: MainViewModel
) {
    var productName by remember { mutableStateOf("") }
    var productQuantity by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }

    val onProductTextChange = { text : String ->
        productName = text
    }

    val onQuantityTextChange = { text : String ->
        productQuantity = text
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        CustomTextField(
            title = "Product Name",
            textState = productName,
            onTextChange = onProductTextChange,
            keyboardType = KeyboardType.Text
        )
        CustomTextField(
            title = "Quantity",
            textState = productQuantity,
            onTextChange = onQuantityTextChange,
            keyboardType = KeyboardType.Number
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Button(onClick = {
                if (productQuantity.isNotEmpty()) {
                    viewModel.insertProduct(
                        Product(
                            id = 0, // Будет сгенерирован автоматически
                            productName = productName,
                            quantity = productQuantity.toInt()
                        )
                    )
                    searching = false
                }
            }) {
                Text("Add")
            }
            Button(onClick = {
                searching = true
                viewModel.findProduct(productName)
            }) {
                Text("Search")
            }
            Button(onClick = {
                searching = false
                viewModel.deleteProduct(productName)
            }) {
                Text("Delete")
            }
            Button(onClick = {
                searching = false
                productName = ""
                productQuantity = ""
            }) {
                Text("Clear")
            }
        }
    }
}
@Composable
fun TitleRow(head1: String, head2: String, head3: String) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(
            text = head1,
            color = Color.White,
            modifier = Modifier.weight(0.1f)
        )
        Text(
            text = head2,
            color = Color.White,
            modifier = Modifier.weight(0.2f)
        )
        Text(
            text = head3,
            color = Color.White,
            modifier = Modifier.weight(0.2f)
        )
    }
}

@Composable
fun ProductRow(id: Int, name: String, quantity: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(
            text = id.toString(),
            modifier = Modifier.weight(0.1f)
        )
        Text(
            text = name,
            modifier = Modifier.weight(0.2f)
        )
        Text(
            text = quantity.toString(),
            modifier = Modifier.weight(0.2f)
        )
    }
}

@Composable
fun CustomTextField(
    title: String,
    textState: String,
    onTextChange: (String) -> Unit,
    keyboardType: KeyboardType
) {
    OutlinedTextField(
        value = textState,
        onValueChange = { onTextChange(it) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        singleLine = true,
        label = { Text(title) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        textStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    RoomDemoTheme {
        ScreenSetup(modifier = Modifier, viewModel = MainViewModel(Application()))
    }
}

@Preview
@Composable
fun PreviewTitleRow() {
    RoomDemoTheme {
        TitleRow("ID", "Name", "Quantity")
    }
}

@Preview
@Composable
fun PreviewProductRow() {
    RoomDemoTheme {
        ProductRow(1, "Apple", 10)
    }
}

@Preview
@Composable
fun PreviewCustomTextField() {
    RoomDemoTheme {
        Column(Modifier.padding(16.dp)) {
            CustomTextField(
                title = "Product Name",
                textState = "",
                onTextChange = {},
                keyboardType = KeyboardType.Text
            )
        }
    }
}

class MainViewModelFactory(val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application) as T
    }
}