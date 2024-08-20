package com.example.my_note_app.home

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appbroken_rice.ui.theme.MyNotesApp
import com.example.my_note_app.models.Notes
import com.example.my_note_app.repository.Resources
import com.example.my_note_app.utils.Utils
import com.google.firebase.Timestamp
import java.util.Locale

@Composable
fun HomeScreen(
    homeModel: HomeModel?,
    onNoteClick:(id:String) -> Unit,
    navToDetailPage:() -> Unit,
    navToLoginPage:() -> Unit,
) {
    val homeState = homeModel?.homeState ?: HomeState()

    var openDialog by remember {
        mutableStateOf(false)
    }

    var selectedNote: Notes? by remember {
        mutableStateOf(null)
    }

//    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    
    LaunchedEffect(key1 = Unit) {
        homeModel?.loadNotes()
    }

    Scaffold(scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = { navToDetailPage.invoke() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        topBar = {
            TopAppBar(
                navigationIcon = {},
                actions = {
                          IconButton(onClick = {
                              homeModel?.signOut()
                              navToLoginPage.invoke()
                          }) {
                              Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
                          }
                },
                title = {
                    Text(text = "Home")
                }
            )
        }
    ) {paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when(homeState.notesList){
                is Resources.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }

                is Resources.Success -> {
                    LazyVerticalGrid(columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(homeState.notesList.data ?: emptyList()) { note ->
                            NoteItem(notes = note, onLongClick = {
                                openDialog = true
                                selectedNote = note
                            }) {
                                onNoteClick.invoke(note.docId)

                            }
                        }

//                        AnimatedVisibility(visible = openDialog) {
//                            AlertDialog(onDismissRequest = { openDialog = false },
//                                title = { Text(text = "Delete note?")},
//                                confirmButton = {
//                                    Button(
//                                        onClick = {
//                                            selectedNote?.docId.let {it1->
//                                                homeModel?.deleteNote(it1.toString())
//                                            }
//                                            openDialog = false
//                                        },
//                                        colors = ButtonDefaults.buttonColors(
//                                            backgroundColor = Color.Red
//                                        )
//                                    ) {
//                                        Text(text = "Delete")
//                                    }
//                                },
//                                dismissButton = {
//                                    Button(onClick = { openDialog = false }) {
//                                        Text(text = "Cancel")
//                                    }
//                                }
//                            )
                    }

                        if (openDialog) {
                            AlertDialog(
                                onDismissRequest = { openDialog = false },
                                title = { Text(text = "Delete note?") },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            selectedNote?.docId?.let { id ->
                                                homeModel?.deleteNote(id)
                                            }
                                            openDialog = false
                                        },
                                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                                    ) {
                                        Text(text = "Delete")
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = { openDialog = false }) {
                                        Text(text = "Cancel")
                                    }
                                }
                            )
                        }
                }

                else -> {
                    Text(text = homeState.notesList.throwable?.localizedMessage ?: "Unknow error",
                        color = Color.Red
                    )
                    Log.d("TAG", "HomeScreen: " + homeState.notesList.throwable?.localizedMessage.toString())
                }
            }
        }
    }

    LaunchedEffect(key1 = homeModel?.hasUser, ) {
        if(homeModel?.hasUser == false){
            navToLoginPage.invoke()
        }
    }
}

//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun NoteItem(
//    notes: Notes,
//    onLongClick:() -> Unit,
//    onClick: () -> Unit
//) {
//    Card(modifier = Modifier
//        .combinedClickable(
//            onLongClick = { onLongClick.invoke() },
//            onClick = { onClick.invoke() })
//        .padding(8.dp)
//        .fillMaxWidth(),
//        backgroundColor = Utils.colors[notes.colorId]
//    ) {
//        Column {
//            Text(text = notes.title,
//                style = MaterialTheme.typography.h6,
//                fontWeight = FontWeight.Bold,
//                maxLines = 1,
//                overflow = TextOverflow.Clip,
//                modifier = Modifier.padding(4.dp)
//            )
//
//            Spacer(modifier = Modifier.size(4.dp))
//
//            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
//                Text(text = notes.desription,
//                    style = MaterialTheme.typography.body1,
//                    overflow = TextOverflow.Ellipsis,
//                    modifier = Modifier.padding(4.dp),
//                    maxLines = 4,
//                )
//            }
//
//            Spacer(modifier = Modifier.size(4.dp))
//
//            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
//                Text(text = formatDate(notes.timestamp),
//                    style = MaterialTheme.typography.body1,
//                    overflow = TextOverflow.Ellipsis,
//                    modifier = Modifier
//                        .padding(4.dp)
//                        .align(Alignment.End),
//                    maxLines = 4,
//                )
//            }
//
//        }
//    }
//}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    notes: Notes,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .combinedClickable(
                onLongClick = onLongClick,
                onClick = onClick
            ),
        backgroundColor = Utils.colors[notes.colorId]
    ) {
        Column {
            Text(
                text = notes.title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.padding(4.dp)
            )

            Spacer(modifier = Modifier.size(4.dp))

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                Text(
                    text = notes.desription,
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(4.dp),
                    maxLines = 4,
                )
            }

            Spacer(modifier = Modifier.size(4.dp))

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                Text(
                    text = formatDate(notes.timestamp),
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.End),
                    maxLines = 4,
                )
            }
        }
    }
}


private fun formatDate(timestamp: Timestamp):String {
    val date = SimpleDateFormat("dd-MM-yy hh:mm", Locale.getDefault())
    return date.format(timestamp.toDate())
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewHome() {
    MyNotesApp {
        HomeScreen(homeModel = null, onNoteClick = {}, navToDetailPage = { /*TODO*/ }) {

        }
    }
}