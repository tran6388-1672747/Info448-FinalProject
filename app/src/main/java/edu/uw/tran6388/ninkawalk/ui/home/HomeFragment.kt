package edu.uw.tran6388.ninkawalk.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import edu.uw.tran6388.ninkawalk.R
//import android.support.v9.app.AppCompatActivity
import android.hardware.Sensor
//import android.content.Context
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService

import kotlinx.android.synthetic.main.fragment_home.*


import edu.uw.tran6388.ninkawalk.MainActivity
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        //sensorManager = getSystemService(this.SENSOR_SERVICE) as SensorManager
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })
        var steps = (activity as MainActivity).steps
        if(steps != null)
            root.findViewById<TextView>(R.id.text_display).setText("$steps STEPS")
        return root
    }
}