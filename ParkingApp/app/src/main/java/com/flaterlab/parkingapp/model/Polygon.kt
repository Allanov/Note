package com.flaterlab.parkingapp.model

import android.graphics.Color
import com.flaterlab.parkingapp.util.LocationUtils
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.Polygon
import com.google.maps.android.PolyUtil

data class Polygon(val id: String,
                   val corner1: LatLng,
                   val corner2: LatLng,
                   val corner3: LatLng,
                   val corner4: LatLng,
                   var isFree: Boolean = false) {

    val imageUrl: String? = null
    val description: String? = null
    val center: LatLng = getPolygonCenterPoint(arrayListOf(corner1, corner2, corner3, corner4))
    private lateinit var polygon: Polygon
    private lateinit var map: GoogleMap

    fun makeFree() {
        isFree = true
        polygon.strokeColor = FREE_STROKE_COLOR
        polygon.strokeWidth = FREE_STROKE_WIDTH.toFloat()
    }

    fun makeReserved() {
        isFree = false
        polygon.remove()
        val polygonOptions: PolygonOptions = PolygonOptions().add(corner1, corner2, corner3, corner4)
                .strokeColor(RESERVED_STROKE_COLOR)
                .strokeWidth(RESERVED_STROKE_WIDTH.toFloat())
        polygon = map.addPolygon(polygonOptions)
    }

    fun asArray(): List<LatLng> = listOf(corner1, corner2, corner3, corner4)

    fun containsPoint(point: LatLng) = PolyUtil.containsLocation(point, this.asArray(), false)

    fun drawOnMap(map: GoogleMap) {
        this.map = map
        val polygonOptions: PolygonOptions = PolygonOptions().add(corner1, corner2, corner3, corner4)
                .strokeColor(FREE_STROKE_COLOR)
                .strokeWidth(FREE_STROKE_WIDTH.toFloat())
        polygon = map.addPolygon(polygonOptions)
    }

    fun getParentFrom(parkingZones: List<ParkingZone>): ParkingZone? = parkingZones.find {
        it.containsPolygon(this)
    }

    fun getDistanceFrom(latLng: LatLng) = LocationUtils.CalculationByDistance(center, latLng)

    private fun getPolygonCenterPoint(polygonPointsList: ArrayList<LatLng>): LatLng {
        val centerLatLng: LatLng?
        val builder = LatLngBounds.Builder()
        for (polygon in polygonPointsList) {
            builder.include(polygon)
        }
        val bounds = builder.build()
        centerLatLng = bounds.center

        return centerLatLng
    }

    companion object {
        private const val FREE_STROKE_WIDTH = 3
        private const val RESERVED_STROKE_WIDTH = 5
        private const val FREE_STROKE_COLOR = Color.GREEN
        private const val RESERVED_STROKE_COLOR = Color.RED
    }
}