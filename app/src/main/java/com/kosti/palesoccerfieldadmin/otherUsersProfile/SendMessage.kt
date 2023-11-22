package com.kosti.palesoccerfieldadmin.otherUsersProfile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.kosti.palesoccerfieldadmin.R

class SendMessage : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message)

        toolbar = findViewById(R.id.toolbarSendMessage)

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
}