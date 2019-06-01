package com.flaterlab.parkingapp.model

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import java.util.*
import kotlin.collections.ArrayList

data class ParkingZone(val id: String,
                       val name: String,
                       val polygons: List<Polygon>,
                       val costPerMinute: Float,
                       val location: LatLng,
                       val corners: List<LatLng> = ArrayList()) {

    val size: Int get() = polygons.size
    val isFull: Boolean = polygons.all { !it.isFree }

    fun getCurrentPolygon(coors: LatLng): Polygon?
            = polygons.asSequence().find { it.isFree && it.containsPoint(coors) }

    fun getPolygonById(id: String): Polygon? = polygons.find { it.id == id }

    fun containsPolygon(polygon: Polygon): Boolean = polygons.any { it == polygon }

    fun contains(latLng: LatLng) = PolyUtil.containsLocation(latLng, corners, false)

    fun List<Polygon>.sortedByDistance(location: Location?) =
            if (location == null) {
                this.sortedBy { it.id }
            } else {
                this.sortedBy { it.getDistanceFrom(LatLng(location.latitude, location.longitude)) }
            }

    fun getSortedPolygons(location: Location?): List<Polygon> = polygons.sortedByDistance(location)

    fun getFreePolygons(location: Location?): List<Polygon> =
            polygons.filter { it.isFree }.sortedByDistance(location)

    fun drawOnMap(map: GoogleMap) {
        polygons.forEach { it.drawOnMap(map) }
        polygons.asSequence().filter { !it.isFree }.forEach { it.makeReserved() }
    }
}