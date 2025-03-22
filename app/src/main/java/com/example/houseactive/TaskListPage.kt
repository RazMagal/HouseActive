package com.example.houseactive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.DateFormat
import java.text.DateFormat.getDateInstance

@Composable
fun TaskListPage() {
    val taskList = getTaskList()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp)
    ) {
        LazyColumn(
            content = {
                itemsIndexed(taskList) { index: Int, item: Task -> TaskItem(item = item)
                }
            }
        )
    }
}

@Composable
fun TaskItem (item: Task) {
    var isCompleted by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isCompleted) Color.Green else Color.Blue)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = item.name,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Row(modifier = Modifier.fillMaxWidth()){
                val dateFormat = getDateInstance(DateFormat.SHORT)
                Text(
                    text = "Due: ${dateFormat.format(item.dueDate)}",
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    color = Color.LightGray,
                    modifier = Modifier
                        .weight(1f)
                )
                Text(
                    text = "Importance: ${item.importance}",
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    color = Color.LightGray,
                    modifier = Modifier
                        .weight(1f),
                    textAlign = TextAlign.End
                )
            }

            Button(
                onClick = {
                    isCompleted = !isCompleted
                }
            ) {
                Text(text = if (isCompleted) "Completed!" else "Complete?",
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    color = Color.Magenta
                )
            }
        }
    }
}