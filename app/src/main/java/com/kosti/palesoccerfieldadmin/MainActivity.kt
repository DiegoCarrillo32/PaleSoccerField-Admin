package com.kosti.palesoccerfieldadmin



import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.kosti.palesoccerfieldadmin.UserListPackage.UserList


class MainActivity : AppCompatActivity() {
    private lateinit var navigateButtonTest: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigateButtonTest = findViewById(R.id.navigationTestButton)
        navigateButtonTest.setOnClickListener {
            val intent = Intent(this, UserList::class.java)
            startActivity(intent)
        }
    }
}