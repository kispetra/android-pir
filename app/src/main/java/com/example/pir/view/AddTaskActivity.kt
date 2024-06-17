package com.example.pir.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.pir.R
import com.example.pir.model.Task
import com.example.pir.viewModel.TaskViewModel

class AddTaskActivity : AppCompatActivity() {
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addtask)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        val taskNameEditText = findViewById<EditText>(R.id.taskNameEditText)
        val taskDetailsEditText = findViewById<EditText>(R.id.taskDetailsEditText)
        val taskStatusSpinner = findViewById<Spinner>(R.id.taskStatusSpinner)
        val addTaskButton = findViewById<Button>(R.id.addTaskButton)
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            // Povratak na TaskActivity
            val intent = Intent(this, TaskActivity::class.java)
            startActivity(intent)
            finish()
        }
        addTaskButton.setOnClickListener {
            val taskName = taskNameEditText.text.toString()
            val taskDetails = taskDetailsEditText.text.toString()
            val isCompleted = taskStatusSpinner.selectedItem.toString() == "Completed"

            if (taskName.isNotBlank() && taskDetails.isNotBlank()) {
                val task = Task(name = taskName, details = taskDetails, isCompleted = isCompleted)
                taskViewModel.addTask(task)
                Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
