package io.dev00.sedentarybreaker.DataSources

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

fun getWeather(API_Key:String,LAT: Double, LON: Double, context: Context, onWeatherFetched:(current: JSONArray) -> Unit) {
    val url = "https://api.openweathermap.org/data/2.5/weather?lat=$LAT&lon=$LON&appid=$API_Key"
    var jsonObj: JSONObject? = null
    var current: JSONArray? = null
    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        Request.Method.GET, url,
        { response ->
            jsonObj = JSONObject(response)
            current = jsonObj!!.getJSONArray("weather")
            onWeatherFetched(jsonObj!!.getJSONArray("weather"))
        },
        {
            Log.d("TAG", ": gone wrong")
        })
    queue.add(stringRequest)
}