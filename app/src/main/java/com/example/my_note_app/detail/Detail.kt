package com.example.my_note_app.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appbroken_rice.ui.theme.MyNotesApp
import com.example.my_note_app.utils.Utils

@Composable
fun DetailScreen(
    detailModel: DetailModel?,
    noteId: String ,
    onNavigate:() -> Unit
) {
    val detailState = detailModel?.detailState ?: DetailState()

    val checkFormsNote = detailState.note.isNotBlank() && detailState.title.isNotBlank()

    val selectedColor by animateColorAsState(targetValue = Utils.colors[detailState.colorId])

    val checkNoteId = noteId.isNotBlank()
    val icon = if(checkNoteId){
        Icons.Default.Refresh
    }else{
        Icons.Default.Check
    }
    
    LaunchedEffect(key1 = Unit) {
        if(checkNoteId){
            detailModel?.getNote(noteId)
        }else{
            detailModel?.resetState()
        }
    }

//    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    Scaffold (
        scaffoldState = scaffoldState,
        floatingActionButton = {
            AnimatedVisibility(visible = checkFormsNote) {
                FloatingActionButton(onClick = {
                    if(checkNoteId){
                        detailModel?.updateNote(noteId)
                    }else{
                        detailModel?.addNote()
                    }
                }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            }
        },
    ){padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = selectedColor)
            .padding(padding)
        ) {
//            if(detailState.noteStatus){
//                scope.launch {
//                    scaffoldState.snackbarHostState
//                        .showSnackbar("Added note success")
//
//                    detailModel?.resetNoteStatus()
//                    onNavigate.invoke()
//                }
//            }
//
//            if(detailState.updateNoteStatus){
//                scope.launch {
//                    scaffoldState.snackbarHostState
//                        .showSnackbar("Updated success")
//                    detailModel?.resetNoteStatus()
//                    onNavigate.invoke()
//                }
//            }

            LaunchedEffect(key1 = detailState.noteStatus) {
                if(detailState.noteStatus) {
                    scaffoldState.snackbarHostState.showSnackbar("Added note success")
                    detailModel?.resetNoteStatus()
                    onNavigate.invoke()
                }
            }

            LaunchedEffect(key1 = detailState.updateNoteStatus) {
                if(detailState.updateNoteStatus) {
                    scaffoldState.snackbarHostState.showSnackbar("Updated success")
                    detailModel?.resetNoteStatus()
                    onNavigate.invoke()
                }
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
            ) {
                itemsIndexed(Utils.colors){colorId , color->
                    ColorItem(color = color) {
                        detailModel?.handleChangeColor(colorId)
                    }
                }

            }

            OutlinedTextField(value = detailState.title,
                onValueChange = {
                    detailModel?.handleChangeTitle(it)
                },
                label = {Text(text = "Title")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            OutlinedTextField(value = detailState.note,
                onValueChange = {
                    detailModel?.handleChangeNote(it)
                },
                label = { Text(text = "Notes")},
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            )

        }
    }
}

@Composable
fun ColorItem(
    color: Color,
    onClick:() -> Unit
) {
    Surface(color = color,
        shape = CircleShape,
        modifier = Modifier
            .padding(8.dp)
            .size(36.dp)
            .clickable {
                onClick.invoke()
            },
        border = BorderStroke(2.dp, Color.Black)
    ) {

    }
}


@Preview(showSystemUi = true)
@Composable
private fun PrevDetail() {
    MyNotesApp {
        DetailScreen(detailModel = null, noteId = "" ) {

        }
    }
}