package com.developers.healtywise.common.helpers

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*


object SingleShotLocationProvider {
    // calls back to calling thread, note this is for low grain: if you want higher precision, swap the
    // contents of the else and if. Also be sure to check gps permission/settings are allowed.
    // call usually takes <10ms
    fun requestSingleUpdate(context: Context, callback: LocationCallback) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (isNetworkEnabled) {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_COARSE
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                callback.cannotGetLocation()
                return
            }
            locationManager.requestSingleUpdate(criteria,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        callback.onNewLocationAvailable(GPSCoordinates(location.latitude,
                            location.longitude))
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {
                        callback.cannotGetLocation()
                    }
                }, null)
        } else {
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (isGPSEnabled) {
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_FINE
                locationManager.requestSingleUpdate(criteria, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        callback.onNewLocationAvailable(GPSCoordinates(location.latitude,
                            location.longitude))
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {
                        callback.cannotGetLocation()
                    }
                }, null)
            }else{
                callback.cannotGetLocation()
            }
        }
    }

    interface LocationCallback {
        fun onNewLocationAvailable(location: GPSCoordinates?)
        fun cannotGetLocation()
    }




    fun showSettingsAlert(mContext:Context) {
        val builder: AlertDialog.Builder =AlertDialog.Builder(mContext)
        builder.setTitle("GPS is not Enabled!")
        builder.setMessage("Do you want to turn on GPS?")
        builder.setPositiveButton("Yes") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext.startActivity(intent)
        }
        builder.setNegativeButton("No"
        ) { dialog, which -> dialog.cancel() }
        val dialog=builder.create()
        dialog.window?.attributes?.windowAnimations= com.developers.healtywise.R.style.MyLocationDialoge
        dialog.show()
    }

     fun getAddress(mContext:Context,latitude: Float, longitude: Float): String? {
        val result = StringBuilder()
        try {
            val geocoder = Geocoder(mContext, Locale.US)
            val addresses: List<Address> = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
            if (addresses.size > 0) {
                val address: Address = addresses[0]
                result.append(address.locality).append(" ")
                result.append(address.countryName)
            }
        } catch (e: IOException) {
            Log.e("tag", e.message.toString())
        }
        return result.toString()
    }
    // consider returning Location instead of this dummy wrapper class
    class GPSCoordinates {
        var longitude = -1f
        var latitude = -1f

        constructor(theLatitude: Float, theLongitude: Float) {
            longitude = theLongitude
            latitude = theLatitude
        }

        constructor(theLatitude: Double, theLongitude: Double) {
            longitude = theLongitude.toFloat()
            latitude = theLatitude.toFloat()
        }
    }



}