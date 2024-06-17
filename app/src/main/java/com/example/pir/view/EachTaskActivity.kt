package com.example.pir.view

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.pir.R
import com.example.pir.model.Task
import com.example.pir.viewModel.TaskViewModel

class EachTaskActivity : AppCompatActivity() {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskNameEditText: EditText
    private lateinit var taskDetailsEditText: EditText
    private lateinit var taskStatusSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eachtask)

        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        taskNameEditText = findViewById(R.id.task_name_edit_text)
        taskDetailsEditText = findViewById(R.id.task_details_edit_text)
        taskStatusSpinner = findViewById(R.id.taskStatusSpinner)
        saveButton = findViewById(R.id.save_button)
        deleteButton = findViewById(R.id.delete_button)

        task = intent.getParcelableExtra("TASK_OBJECT")

        Log.d("EachTaskActivity", "Received task: $task")

        task?.let {
            taskNameEditText.setText(it.name)
            taskDetailsEditText.setText(it.details)
            taskStatusSpinner.setSelection(if (it.isCompleted) 1 else 0)
        }

        saveButton.setOnClickListener {
            task?.let { nonNullTask ->
                nonNullTask.name = taskNameEditText.text.toString()
                nonNullTask.details = taskDetailsEditText.text.toString()
                nonNullTask.isCompleted = taskStatusSpinner.selectedItemPosition == 1
                taskViewModel.updateTask(nonNullTask)
                setResult(Activity.RESULT_OK) // Return result
                finish()
            }
        }

        deleteButton.setOnClickListener {
            task?.let { nonNullTask ->
                taskViewModel.deleteTask(nonNullTask)
                setResult(Activity.RESULT_OK) // Return result
                finish()
            }
        }

    }
}
