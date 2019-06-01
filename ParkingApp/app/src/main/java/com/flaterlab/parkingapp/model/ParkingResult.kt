package com.flaterlab.parkingapp.model

import java.util.*

data class ParkingResult(val id: String,
                         val name: String,
                         val started: Date,
                         val finished: Date) {

    val duration: Long = finished.time - started.time
}