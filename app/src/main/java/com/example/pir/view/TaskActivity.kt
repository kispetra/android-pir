package com.example.pir.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pir.R
import com.example.pir.model.Task
import com.example.pir.viewModel.TaskViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class TaskActivity : AppCompatActivity() {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskListLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        val addButton = findViewById<Button>(R.id.add_button)
        taskListLayout = findViewById(R.id.task_list_layout)

        addButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_todo

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent1 = Intent(this, MainActivity::class.java)
                    startActivity(intent1)
                    true
                }
                R.id.navigation_todo -> {
                    val intent2 = Intent(this, TaskActivity::class.java)
                    startActivity(intent2)
                    true
                }
                R.id.navigation_tips -> {
                    val intent3 = Intent(this, GuestListActivity::class.java)
                    startActivity(intent3)
                    true
                }
                R.id.navigation_profile -> {
                    val intent4 = Intent(this, ProfileActivity::class.java)
                    startActivity(intent4)
                    true
                }
                else -> false
            }
        }

        taskViewModel.tasks.observe(this, Observer { tasks ->
            val sortedTasks = tasks.sortedWith(compareBy({ it.isCompleted }, { it.name }))

            taskListLayout.removeAllViews()
            sortedTasks.forEach { task ->
                addTaskToLayout(task)
            }
        })

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim().toLowerCase()

                taskViewModel.tasks.observe(this@TaskActivity, Observer { tasks ->
                    val filteredTasks = tasks.filter { task ->
                        task.name.toLowerCase().contains(searchText) ||
                                task.details.toLowerCase().contains(searchText)
                    }.sortedWith(compareBy({ it.isCompleted }, { it.name }))

                    taskListLayout.removeAllViews()
                    filteredTasks.forEach { task ->
                        addTaskToLayout(task)
                    }
                })
            }

            override fun afterTextChanged(s: Editable?) {
                // Not used
            }
        })
    }

    private fun addTaskToLayout(task: Task) {
        val taskView = layoutInflater.inflate(R.layout.item_task, taskListLayout, false)
        val taskNameTextView = taskView.findViewById<TextView>(R.id.task_name)
        val taskDetailsTextView = taskView.findViewById<TextView>(R.id.task_details)
        val taskStatusIcon = taskView.findViewById<ImageView>(R.id.task_status_icon)

        taskNameTextView.text = "${taskListLayout.childCount + 1}. ${task.name}"
        taskDetailsTextView.text = task.details

        if (task.isCompleted) {
            taskStatusIcon.setImageResource(R.drawable.ic_done)
        } else {
            taskStatusIcon.setImageResource(R.drawable.ic_notdone)
        }

        taskView.setOnClickListener {
            val intent = Intent(this, EachTaskActivity::class.java).apply {
                putExtra("TASK_OBJECT", task)
            }
            startActivity(intent)
        }

        taskListLayout.addView(taskView)
    }
}
