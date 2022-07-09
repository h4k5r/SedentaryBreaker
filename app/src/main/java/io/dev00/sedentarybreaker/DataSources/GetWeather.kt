package io.dev00.sedentarybreaker.DataSources

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

fun getWeather(API_Key:String,LAT: Double, LON: Double, context: Context, onWeatherFetched:(current: JSONObject) -> Unit): JSONObject? {
    val url = "https://api.weatherapi.com/v1/current.json?key=$API_Key&q=$LAT,$LON"
    var jsonObj: JSONObject? = null
    var current: JSONObject? = null
    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        Request.Method.GET, url,
        { response ->
            jsonObj = response.let { JSONObject(it) }
            current = jsonObj!!.getJSONObject("current")
            onWeatherFetched(jsonObj!!.getJSONObject("current"))
        },
        {
            Log.d("TAG", "onCreate: gone wrong")
        })
    queue.add(stringRequest)
    return current
}