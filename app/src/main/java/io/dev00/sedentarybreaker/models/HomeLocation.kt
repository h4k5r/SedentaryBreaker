package io.dev00.sedentarybreaker.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_location")
class HomeLocation(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    @ColumnInfo(name = "latitude")
    var lat: Double,
    @ColumnInfo(name = "longitude")
    var lon: Double
) {
    constructor() : this(id = null, lat = 0.0, lon = 0.0)
    constructor(lat: Double, long: Double) : this(id = null, lat = lat, lon = long)

    override fun toString(): String {
        return "Latitude: ${this.lat} Longitude: ${this.lon}"
    }
}