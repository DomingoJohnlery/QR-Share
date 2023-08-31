package com.myapp.qrcodebuilder

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myapp.qrcodebuilder.databinding.FragmentMessageDisplayBinding

class MessageDisplay : Fragment() {

    private lateinit var binding: FragmentMessageDisplayBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageDisplayBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val msg = sharedPreferences.getString("msg","")

        val msgDisplay = binding.msgDisplay
        msgDisplay.text = msg
        msgDisplay.movementMethod = ScrollingMovementMethod()
    }
}