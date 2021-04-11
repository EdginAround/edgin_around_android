package com.edgin.around.app

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.edgin.around.rendering.About
import java.io.File
import kotlin.concurrent.thread

const val TAG: String = "EdginAround"
const val RESOURCE_ZIP_ASSET_NAME = "edgin_around_resources.zip"
const val RESOURCE_LOCK_FILE_NAME = "edgin_around_resources.lock"
const val RESOURCE_DIR = "resources"
const val SPRITE_DIR = "sprites"

class IntroActivity : AppCompatActivity() {
    private val REQUEST_CODE = 1

    private lateinit var buttonStart: Button

    init {
        System.loadLibrary("edgin_around_android")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "Welcome!")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        buttonStart = findViewById(R.id.button_start) as Button
        buttonStart.setOnClickListener { gotoDashboard() }

        checkEdginAroundVersion()
        checkPermissions()
        extractResourcesIfNeeded()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                val haveAllPermissions = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                if (!haveAllPermissions) {
                    with(AlertDialog.Builder(this, R.style.AlertDialog)) {
                        setTitle(R.string.intro_no_permissions_title)
                        setMessage(R.string.intro_no_permissions_text)
                        setPositiveButton(
                            R.string.intro_no_permissions_ok,
                            DialogInterface.OnClickListener { _, _ -> finish() }
                        )
                        create()
                    }.show()
                }
            }
        }
    }

    private fun checkPermissions() {
        val neededPremissions = arrayOf(
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
        val about = About()
        if (!about.checkVersion()) {
            val version = about.getVersion()
            Log.e(TAG, "Lib version: ${version[0]}.${version[1]}.${version[2]}")
            Toast.makeText(this, R.string.lib_wrong_version, Toast.LENGTH_LONG).show()
        }
    }

    private fun gotoDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }

    // Resources are packed in APK as assets in a zip file. In future we will download additional
    // resources from the internet (skins, etc.). Those files in turn will have to be stored in the
    // internal or external storage. To make the access to all the files uniform, we extract the
    // bundled files to an internal directory.
    private fun extractResourcesIfNeeded() {
        val resourcesDir = File(getFilesDir(), RESOURCE_DIR)
        val lockFile = File(getFilesDir(), RESOURCE_LOCK_FILE_NAME)

        // Check is lock file exists - if so, that means the previous extraction failed and
        // the resources need to be removed and extracted again
        if (lockFile.exists()) {
            Log.w(TAG, "Previous resource extraction failed!")
            FsUtil().remove(resourcesDir)
            lockFile.delete()
        }

        // Extract resources if not already extracted
        if (!resourcesDir.isDirectory()) {
            thread {
                Log.i(TAG, "Extracting resources...")
                lockFile.createNewFile()
                extractResources(resourcesDir)
                lockFile.delete()
                Log.i(TAG, "Extracting resources: DONE")
            }
        } else {
            Log.i(TAG, "Resources already extracted")
        }
    }

    private fun extractResources(resourcesDir: File) {
        resourcesDir.mkdirs()

        val assets = getResources().getAssets()
        val resourceStream = assets.open(RESOURCE_ZIP_ASSET_NAME)

        ZipUtil().unzip(resourceStream, resourcesDir)
    }
}
