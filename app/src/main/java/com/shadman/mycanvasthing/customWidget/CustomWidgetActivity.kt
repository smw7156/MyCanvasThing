package com.shadman.mycanvasthing.customWidget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shadman.mycanvasthing.databinding.CustomWidgetActivityBinding

class CustomWidgetActivity: AppCompatActivity() {

    private lateinit var binding: CustomWidgetActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CustomWidgetActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}