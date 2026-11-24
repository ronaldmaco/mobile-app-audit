package app.notes.data

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class Note(val id: String, val title: String, val body: String)

class NotesApi(private val baseUrl: String, private val token: String) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    fun createNote(title: String, body: String): Note {
        val json = JSONObject().put("title", title).put("body", body).toString()
        val request = Request.Builder()
            .url("$baseUrl/notes")
            .header("Authorization", "Bearer $token")
            .post(json.toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("HTTP ${response.code}")
            val obj = JSONObject(response.body!!.string())
            return Note(obj.getString("id"), obj.getString("title"), obj.getString("body"))
        }
    }

    fun listNotes(): List<Note> {
        val request = Request.Builder()
            .url("$baseUrl/notes")
            .header("Authorization", "Bearer $token")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("HTTP ${response.code}")
            val arr = JSONObject(response.body!!.string()).getJSONArray("notes")
            return (0 until arr.length()).map {
                val o = arr.getJSONObject(it)
                Note(o.getString("id"), o.getString("title"), o.getString("body"))
            }
        }
    }
}
