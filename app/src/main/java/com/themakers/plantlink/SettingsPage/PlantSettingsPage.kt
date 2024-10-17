package com.themakers.plantlink.SettingsPage

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import com.themakers.plantlink.Bluetooth.BluetoothViewModel
import com.themakers.plantlink.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class CharacterLimitVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        if (text.length <= 4) {
            return TransformedText(text, OffsetMapping.Identity)
        }
        val filteredText = AnnotatedString(text.text, spanStyles = listOf(
            AnnotatedString.Range(
                SpanStyle(), 0, 3)))
        return TransformedText(filteredText, OffsetMapping.Identity)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantSettingsPage(
    context: Context,
    navController: NavHostController,
    plantViewModel: CurrClickedPlantViewModel,
    btViewModel: BluetoothViewModel
) {
    val lazyListState = rememberLazyListState()
    var minSoilMoisture by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text = plantViewModel.currClickedPlant?.minMoisture ?: "Min", TextRange(0, 3)))
    }
    var maxSoilMoisture by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text = plantViewModel.currClickedPlant?.maxMoisture ?: "Max", TextRange(0, 3)))
    }
    var plantName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text = plantViewModel.currClickedPlant?.plantName ?: ""))
    }

    val service = plantViewModel.currClickedPlant?.device

    val scope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current


    // For drop-down menu
    var mExpanded by remember { mutableStateOf(false) }

    // List of plants for preset
    val mPlants = listOf("Galaxy Petunia", "Plant 2", "Plant 3")

    // Create a string value to store the selected city
    var mSelectedText by remember { mutableStateOf("") }

    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}

    // Up Icon when expanded and down icon when collapsed
    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Scaffold(
        //backgroundColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.secondary
                ),
                title = {
                    Text(
                        text = "Plant Settings",
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 50.dp),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()

                        plantViewModel.currClickedPlant = null
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(40.dp),
                            tint = Color.Black

                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(226, 114, 91, 255),//MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedIconColor = Color(0, 0, 0, 255),
                        indicatorColor = MaterialTheme.colorScheme.background
                    ),
                    selected = false,
                    onClick = {
                        navController.navigate("Home")

                        plantViewModel.currClickedPlant = null
                    },
                    label = {
                        Text(
                            text = "Home",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 15.sp
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Home"
                        )
                    }
                )
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedIconColor = Color(0, 0, 0, 255),
                        indicatorColor = MaterialTheme.colorScheme.background
                    ),
                    selected = false,
                    onClick = {
                        navController.navigate("Settings")

                        plantViewModel.currClickedPlant = null
                    },
                    label = {
                        Text(
                            text = "Settings",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 15.sp
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    }
                )
            }
        }
    ) { padding ->
        Column (
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = painterResource(R.drawable.sharp_psychiatry_24),
                contentDescription = "Big Plant",
                tint = Color(61, 168, 44),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp) // Maybe make float so it goes to a certain percentage
            )
        }

        LazyColumn(
            contentPadding = padding,
            state = lazyListState,
        ) {
            /* TODO: To have an image set for each plant, item would go here to upload/change */

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }

            item {
                Card (
                    shape = MaterialTheme.shapes.small,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Plant Name",
                            color = Color(0, 0, 0, 255),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            modifier = Modifier.fillMaxSize()
                        )
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {
                            OutlinedTextField(
                                value = plantName,
                                onValueChange = {
                                    //if (it.text.length <= 4) { Re-enable if wanting to limit to a length
                                    plantName = it
                                    //}
                                },
                                //visualTransformation = CharacterLimitVisualTransformation(), Re-enable if wanting to limit to a length
                                placeholder = {
                                    Text(
                                        text = plantViewModel.currClickedPlant?.plantName ?: "Plant Name", // Eventually make this the value stored in the arduino
                                        //backgroundColor = MaterialTheme.colorScheme.background,
                                        color = MaterialTheme.colorScheme.secondary,
                                        textAlign = TextAlign.Center
                                    )
                                },
                                modifier = Modifier
                                    .padding(15.dp)
                                    .weight(1f)
                                    .height(65.dp)
                                    .focusRequester(focusRequester),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        btViewModel.gatt!!.writeCharacteristic(service!!.getCharacteristic(
                                            UUID.fromString("b761e2e9-fac9-439c-a321-123d7f404e36")), plantName.text.toByteArray(), BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)


                                        plantViewModel.currClickedPlant?.setName(plantName.text)

                                        focusManager.clearFocus() // Hide keyboard
                                    }
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Ascii,
                                    imeAction = ImeAction.Done
                                ),
                                singleLine = true,
                                shape = MaterialTheme.shapes.small,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedTextColor = Color(255, 255, 255, 255)
                                ),
                                textStyle = TextStyle(
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Card (
                    shape = MaterialTheme.shapes.small,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Soil Moisture Range",
                            color = Color(0, 0, 0, 255),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            modifier = Modifier.fillMaxSize()
                        )
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {
                            OutlinedTextField(
                                value = minSoilMoisture,
                                onValueChange = {
                                    if (it.text.length <= 4) {
                                        minSoilMoisture = it
                                    }
                                },
                                visualTransformation = CharacterLimitVisualTransformation(),
                                placeholder = {
                                    Text(
                                        text = plantViewModel.currClickedPlant?.minMoisture ?: "Min", // Eventually make this the value stored in the arduino
                                        //backgroundColor = MaterialTheme.colorScheme.background,
                                        color = MaterialTheme.colorScheme.secondary,
                                        textAlign = TextAlign.Center
                                    )
                                },
                                modifier = Modifier
                                    .padding(15.dp)
                                    .width(100.dp)
                                    .height(65.dp)
                                    .focusRequester(focusRequester),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        scope.launch {
                                            btViewModel.gatt!!.writeCharacteristic(service!!.getCharacteristic(
                                                UUID.fromString("39aec0bb-21c9-4519-8e32-e25c7523fde9")), minSoilMoisture.text.toByteArray(), BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)


                                            plantViewModel.currClickedPlant?.setMinMoist(minSoilMoisture.text)

                                            delay(250) // Without this, it only updates one

                                            btViewModel.gatt!!.writeCharacteristic(service.getCharacteristic(
                                                UUID.fromString("437fcdb7-74c7-4968-a669-384aa06f20c1")), maxSoilMoisture.text.toByteArray(), BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)


                                            plantViewModel.currClickedPlant?.setMaxMoist(maxSoilMoisture.text)
                                        }

                                        focusManager.clearFocus()
                                    }
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Done
                                ),
                                singleLine = true,
                                shape = MaterialTheme.shapes.small,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedTextColor = Color(255, 255, 255, 255)
                                ),
                                textStyle = TextStyle(
                                    fontSize = 25.sp,
                                    textAlign = TextAlign.Center
                                )
                            )

                            Text(
                                text = "-",
                                color = Color(0, 0, 0, 255),
                                textAlign = TextAlign.Center,
                                fontSize = 60.sp,
                                modifier = Modifier.fillMaxHeight()
                            )

                            OutlinedTextField(
                                value = maxSoilMoisture,
                                onValueChange = {
                                    if (it.text.length <= 4) {
                                        maxSoilMoisture = it
                                    }
                                },
                                visualTransformation = CharacterLimitVisualTransformation(),
                                placeholder = {
                                    Text(
                                        text = plantViewModel.currClickedPlant?.maxMoisture ?: "Max",
                                        //backgroundColor = MaterialTheme.colorScheme.background,
                                        color = MaterialTheme.colorScheme.secondary,
                                        textAlign = TextAlign.Center
                                    )
                                },
                                modifier = Modifier
                                    .padding(15.dp)
                                    .width(100.dp)
                                    .height(65.dp)
                                    .focusRequester(focusRequester)
                                    .align(Alignment.CenterVertically),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        scope.launch {
                                            btViewModel.gatt!!.writeCharacteristic(service!!.getCharacteristic(
                                                UUID.fromString("39aec0bb-21c9-4519-8e32-e25c7523fde9")), minSoilMoisture.text.toByteArray(), BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)


                                            plantViewModel.currClickedPlant?.setMinMoist(minSoilMoisture.text)

                                            delay(250) // Without this, it only updates one

                                            btViewModel.gatt!!.writeCharacteristic(service.getCharacteristic(
                                                UUID.fromString("437fcdb7-74c7-4968-a669-384aa06f20c1")), maxSoilMoisture.text.toByteArray(), BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)


                                            plantViewModel.currClickedPlant?.setMaxMoist(maxSoilMoisture.text)
                                        }

                                        focusManager.clearFocus()
                                    }
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Done
                                ),
                                singleLine = true,
                                shape = MaterialTheme.shapes.small,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedTextColor = Color(255, 255, 255, 255)
                                ),
                                textStyle = TextStyle(
                                    fontSize = 25.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            contentPadding = padding,
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        value = mSelectedText,
                        onValueChange = { mSelectedText = it }, // Auto complete options to more easily find the plant they want
                        enabled = true,
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(75.dp)
                            //.align(Alignment.CenterHorizontally)
                            .onGloballyPositioned { coordinates ->
                                // This value is used to assign to
                                // the DropDown the same width
                                mTextFieldSize = coordinates.size.toSize()
                            },
                        label = {
                            Text(
                                text = "Plant Presets",
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.Center
                            )
                        },
                        singleLine = true,
                        shape = MaterialTheme.shapes.small,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedTextColor = Color(255, 255, 255, 255)
                        ),
                        trailingIcon = {
                            Icon(
                                icon,
                                "Drop-Down Arrow",
                                Modifier.clickable { mExpanded = !mExpanded }
                            )
                        }
                    )

                    DropdownMenu(
                        expanded = mExpanded,
                        onDismissRequest = { mExpanded = false },
                        modifier = Modifier
                            .width(with(LocalDensity.current) {
                                mTextFieldSize.width.toDp()
                            })
                    ) {
                        mPlants.forEach { label ->
                            DropdownMenuItem(
                                text = {
                                    Text(text = label)
                                },
                                onClick = {
                                    mSelectedText = label
                                    mExpanded = false
                                })
                        }
                    }
                }

            }
        }
    }
}