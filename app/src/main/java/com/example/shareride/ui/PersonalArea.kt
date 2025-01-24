package com.example.shareride.ui

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.shareride.R

class PersonalArea : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_personal_area, container, false)

        val textView: TextView = view.findViewById(R.id.helloTextView)
        textView.text = "Hello"

        return view
    }
}