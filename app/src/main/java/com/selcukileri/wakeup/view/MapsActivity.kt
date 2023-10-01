package com.selcukileri.wakeup.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.selcukileri.wakeup.R
import com.selcukileri.wakeup.databinding.ActivityMapsBinding
import com.selcukileri.wakeup.model.Place
import com.selcukileri.wakeup.roomdb.PlaceDao
import com.selcukileri.wakeup.roomdb.PlaceDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferencesSettings: SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences
    private var selectedOption: Double? = null
    private var trackBoolean: Boolean? = null
    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null
    private lateinit var db: PlaceDatabase
    private lateinit var placeDao: PlaceDao
    private val compositDisposable = CompositeDisposable()
    private var placeFromBookmarks: Place? = null
    private var placeFromBookmarks2: Place? = null
    private var alarmRingtone: Ringtone? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferencesSettings = this.getSharedPreferences("SettingsPrefs", MODE_PRIVATE)
        registerLauncher()
        sharedPreferences = this.getSharedPreferences("com.selcukileri.wakeup", MODE_PRIVATE)
        trackBoolean = false
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(p0: Location) {

            }

        }
        selectedLatitude = 0.0
        selectedLongitude = 0.0
        db = Room.databaseBuilder(
            applicationContext,
            PlaceDatabase::class.java,
            "Places"
        )
            //.allowMainThreadQueries()
            .build()
        placeDao = db.placeDao()
        binding.saveButton.isEnabled = false

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)
        val intent = intent
        val info = intent.getStringExtra("info")
        val info1 = intent.getStringExtra("info1")
        //val info2 = intent.getStringExtra("info2")
        //val info2 = intent.getStringExtra("info2")
        //Log.d("INFO_DEBUG", "info: $info, info2: $info2")
        if (info == "new") {
            binding.remainingDistance.visibility = View.GONE
            binding.stopButton.visibility = View.GONE
            binding.saveButton.visibility = View.VISIBLE
            binding.deleteButton.visibility = View.GONE
            binding.startButton.visibility = View.GONE
            locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
            locationListener = LocationListener { p0 ->
                trackBoolean = sharedPreferences.getBoolean("trackBoolean", false)
                if (trackBoolean == false) {
                    val userLocation = LatLng(p0.latitude, p0.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                    sharedPreferences.edit().putBoolean("trackBoolean", true).apply()
                }
            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    Snackbar.make(
                        binding.root,
                        "Permission Needed For Location",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("Give Permission") {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }.show()
                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else {
                //permission granted
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    15000,
                    100f,
                    locationListener
                )
                val lastLocation =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastLocation != null) {
                    val lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))
                }
                mMap.isMyLocationEnabled = true
            }

        } else if (info == "old") {
            mMap.clear()
            placeFromBookmarks = intent.getSerializableExtra("selectedPlace") as Place
            placeFromBookmarks?.let {
                val latlng = LatLng(it.latitude, it.longitude)
                mMap.addMarker(MarkerOptions().position(latlng).title(it.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f))
                binding.placeText.setText(it.name)
                binding.startButton.visibility = View.VISIBLE
                binding.saveButton.visibility = View.GONE
                binding.deleteButton.visibility = View.VISIBLE
                binding.remainingDistance.visibility = View.GONE
                binding.stopButton.visibility = View.GONE
            }
        } else if (info == "start") {
            binding.startButton.visibility = View.GONE
            binding.placeText.visibility = View.GONE
            binding.saveButton.visibility = View.GONE
            binding.deleteButton.visibility = View.GONE
            binding.remainingDistance.visibility = View.VISIBLE
            binding.stopButton.visibility = View.VISIBLE

            val selectedDistanceStr =
                sharedPreferencesSettings.getString("selectedDistance", "")
            val selectedAlertType =
                sharedPreferencesSettings.getString("selectedAlertType", "")
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 100f, locationListener
                )
                val currentLocation =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (currentLocation != null) {
                    val lastCurrentLocation =
                        LatLng(currentLocation.latitude, currentLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastCurrentLocation, 20f))
                    mMap.isMyLocationEnabled = true
                    placeFromBookmarks = intent.getSerializableExtra("selectedPlace") as? Place
                    Log.d("placebookmars", "placebookmarks ${placeFromBookmarks?.name}")
                    if (placeFromBookmarks != null){
                        placeFromBookmarks.let {
                            val latlng = LatLng(it!!.latitude, it.longitude)
                            mMap.addMarker(MarkerOptions().position(latlng).title(it.name))
                            val targetLocation = Location("")
                            targetLocation.latitude = it.latitude
                            targetLocation.longitude = it.longitude
                            val distance = currentLocation.distanceTo(targetLocation)
                            val remainingDistanceText =
                                getString(R.string.remaining_distance, distance.toString())
                            Log.d("wakemeup", "Distance: $distance")
                            binding.remainingDistance.text = remainingDistanceText
                            val selectedDistance = selectedDistanceStr!!.toDouble()
                            if (distance <= selectedDistance) {
                                when (selectedAlertType) {
                                    "Alarm" -> {
                                        triggerAlarm()
                                        Log.d("wakemeup", "alarm triggered")
                                    }

                                    "Titreşim" -> {
                                        triggerVibration()
                                        Log.d("wakemeup", "vibration triggered")
                                    }

                                    "Alarm ve Titreşim" -> {
                                        triggerAlarm()
                                        triggerVibration()
                                        Log.d("wakemeup", "alarm and vibration triggered")
                                    }
                                }
                            }
                        }
                    } else {
                        Log.d("wakemeup", "place From bookmarks null or empty")
                    }

                }


            }


        }


    }

    private fun registerLauncher() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        //permission granted
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            15000,
                            100f,
                            locationListener
                        )
                        val lastLocation =
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (lastLocation != null) {
                            val lastUserLocation =
                                LatLng(lastLocation.latitude, lastLocation.longitude)
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    lastUserLocation,
                                    15f
                                )
                            )
                        }
                        mMap.isMyLocationEnabled = true
                    }

                } else {
                    //permission denied
                    Toast.makeText(this@MapsActivity, "Permission Needed", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0))
        selectedLatitude = p0.latitude
        selectedLongitude = p0.longitude
        binding.saveButton.isEnabled = true

    }

    private fun userLocation(): Location? {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //permission granted
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                15000,
                100f,
                locationListener
            )
            val lastLocation =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation != null) {
                val currentLocation = Location("")
                currentLocation.latitude = lastLocation.latitude
                currentLocation.longitude = lastLocation.longitude
                return currentLocation
            }
            mMap.isMyLocationEnabled = true
        }
        return null
    }

    fun start(view: View) {
        val selectedDistanceStr =
            sharedPreferencesSettings.getString("selectedDistance", "")
        val selectedAlertType =
            sharedPreferencesSettings.getString("selectedAlertType", "")
        if (selectedAlertType.isNullOrEmpty() || selectedDistanceStr.isNullOrEmpty()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Uyarı")
            builder.setMessage("Lütfen ayarlara gidip seçenekleri düzgün ayarlayın.")
            builder.setPositiveButton("Tamam") { dialog, _ ->
                dialog.dismiss()
            }
            builder.setNegativeButton("Ayarlara Git") { dialog, _ ->
                val settingsFragment = SettingsFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, settingsFragment)
                transaction.addToBackStack(null)
                transaction.commit()
                binding.startButton.visibility = View.GONE
                binding.saveButton.visibility = View.GONE
                binding.deleteButton.visibility = View.GONE
                binding.remainingDistance.visibility = View.GONE
                binding.stopButton.visibility = View.GONE
                binding.placeText.visibility = View.GONE
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        } else {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("info", "start")
            startActivity(intent)
        }


    }


    fun save(view: View) {

        val place =
            Place(binding.placeText.text.toString(), selectedLatitude!!, selectedLongitude!!)
        compositDisposable.add(
            placeDao.insert(place)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }

    private fun handleResponse() {
        val bookmarksFragment = BookmarksFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, bookmarksFragment)
        transaction.commit()
        binding.startButton.visibility = View.GONE
        binding.saveButton.visibility = View.GONE
        binding.deleteButton.visibility = View.GONE
        binding.remainingDistance.visibility = View.GONE
        binding.stopButton.visibility = View.GONE
        binding.placeText.visibility = View.GONE

    }

    fun delete(view: View) {
        placeFromBookmarks?.let {
            compositDisposable.add(
                placeDao.delete(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        compositDisposable.clear()
    }


    private fun showCustomAlertDialog2() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Kaç metre kala uyarılmak istersiniz")
        val options = arrayOf("500m", "750m", "1000m")
        builder.setItems(options) { dialog, which ->
            selectedOption = options[which].toDouble()

        }
        builder.setCancelable(false)
        builder.show()
    }


    private fun triggerVibration() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android Oreo ve sonrası için
            vibrator.vibrate(
                longArrayOf(
                    0,
                    500,
                    110,
                    500,
                    110,
                    450,
                    110,
                    200,
                    110,
                    170,
                    40,
                    450,
                    110,
                    200,
                    110,
                    170,
                    40,
                    500
                ), -1
            )
            //vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // Android Nougat ve öncesi için
            vibrator.vibrate(
                longArrayOf(
                    0,
                    500,
                    110,
                    500,
                    110,
                    450,
                    110,
                    200,
                    110,
                    170,
                    40,
                    450,
                    110,
                    200,
                    110,
                    170,
                    40,
                    500
                ), -1
            )
        }
    }

    private fun triggerAlarm() {
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            alarmRingtone = RingtoneManager.getRingtone(applicationContext, notification)
            alarmRingtone?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private fun AlertDialog.Builder.setMessage(s: String, s1: String) {

}
