package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        //set filename text
        val filename = intent.getStringExtra("fileName").toString()
        file_name.text = filename

        //set success status
        val status = intent.getStringExtra("status").toString()
        status_text.text = status

        ok_button.setOnClickListener {
            onBackPressed()
        }
    }
}
