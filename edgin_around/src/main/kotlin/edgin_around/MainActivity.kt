package com.edgin.around

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.DialogInterface 
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val TAG: String = "EdginAround"
val LIB_VERSION: IntArray = intArrayOf(0, 1, 0)

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE = 1

    private lateinit var buttonStart: Button

    init {
        System.loadLibrary("edgin_around_android")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonStart = findViewById(R.id.button_start) as Button
        buttonStart.setOnClickListener { gotoGame() }

        checkEdginAroundVersion()
        checkPermissions()
    }

    override fun onRequestPermissionsResult (
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray)
    {
        when (requestCode) {
            REQUEST_CODE -> {
                val haveAllPermissions = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                if (!haveAllPermissions) {
                    with(AlertDialog.Builder(this, R.style.AlertDialog)) {
                        setTitle(R.string.main_no_permissions_title)
                        setMessage(R.string.main_no_permissions_text)
                        setPositiveButton(
                            R.string.main_no_permissions_ok,
                            DialogInterface.OnClickListener { _, _ -> finish() }
                        )
                        create()
                    }.show()
                }
            }
        }
    }

    private fun checkPermissions() {
        val neededPremissions = arrayOf (
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET
        )

        val permissions = neededPremissions.map { ContextCompat.checkSelfPermission(this, it) }
        val haveAllPermissions = permissions.all { it == PackageManager.PERMISSION_GRANTED }
        if (!haveAllPermissions) {
            ActivityCompat.requestPermissions(this, neededPremissions, REQUEST_CODE)
        }
    }

    private fun checkEdginAroundVersion() {
        val version = About().getVersion();
        val major = version[0].toInt();
        val minor = version[1].toInt();
        val patch = version[2].toInt();
        if (major != LIB_VERSION[0] || minor != LIB_VERSION[1] || patch < LIB_VERSION[2]) {
            Toast.makeText(this, R.string.lib_wrong_version, Toast.LENGTH_LONG).show()
        }
    }

    private fun gotoGame() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }
}

