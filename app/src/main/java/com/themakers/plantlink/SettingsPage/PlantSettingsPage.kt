package com.themakers.plantlink.SettingsPage

import android.content.Context
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
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
import androidx.navigation.NavHostController
import com.themakers.plantlink.R

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantSettingsPage(
    context: Context,
    navController: NavHostController,
    plantViewModel: CurrClickedPlantViewModel
) {
    val lazyListState = rememberLazyListState()
    var minSoilMoisture by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text = plantViewModel.currClickedPlant?.minMoisture ?: "Min", TextRange(0, 3)))
    }
    var maxSoilMoisture by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text = plantViewModel.currClickedPlant?.maxMoisture ?: "Max", TextRange(0, 3)))
    }
    var plantName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text = plantViewModel.currClickedPlant?.name ?: ""))
    }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

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
                        plantViewModel.currClickedPlant = null


                        navController.popBackStack()
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
                        plantViewModel.currClickedPlant = null


                        navController.navigate("Home")
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
                        plantViewModel.currClickedPlant = null

                        navController.navigate("Settings")
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
                //NavigationBarItem(
                //    colors = NavigationBarItemDefaults.colors(
                //        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                //        selectedIconColor = Color(0, 0, 0, 255),
                //        indicatorColor = MaterialTheme.colorScheme.background
                //    ),
                //    selected = false,
                //    onClick = {
                //        navController.navigate("History")
                //    },
                //    label = {
                //        Text(
                //            text = "History",
                //            color = MaterialTheme.colorScheme.secondary,
                //            fontSize = 15.sp
                //        )
                //    },
                //    icon = {
                //        Icon(
                //            painter = painterResource(R.drawable.baseline_bar_chart_24),
                //            contentDescription = "Bar Chart"
                //        )
                //    }
                //)
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
                    .height(450.dp)
            )
        }
        LazyColumn(
            contentPadding = padding,
            state = lazyListState
        ) {

            /* TODO: To have an image set for each plant, item would go here to upload/change */


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
                                        text = plantViewModel.currClickedPlant?.name ?: "Plant Name", // Eventually make this the value stored in the arduino
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
                                        focusManager.clearFocus()
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
                                        text = "Min", // Eventually make this the value stored in the arduino
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
                                        text = "Max",
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
    }
}