package com.xyzecommerce

import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

import kotlinx.android.synthetic.main.activity_transfer.*

class TransferActivity : AppCompatActivity() {
    private val tag = "mtag"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)


        SendAmountButton.setOnClickListener {
            if (AmountField.text.isNotEmpty()) {
                val postDict = JSONObject()
                try {
                    postDict.put("AMOUNT", AmountField.text.toString().trim { it <= ' ' })
                    postDict.put("TO_SLID", AccountIDField.text.toString().trim { it <= ' ' })
                    postDict.put("FROM_SLID", getSharedPreferences(Utils.SHARED_PREF, Context.MODE_PRIVATE).getString(Utils.PROFILE_ID, "o00"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                if (postDict.length() > 0) {
                    //TODO for update server
                    SendJsonDataToServer3().execute(postDict.toString(), "https://xyzecommerce.herokuapp.com/transaction.php")
                }

            } else if (AmountField.text.isNotEmpty()) {
                AccountIDField.error = "Enter valid Number"

            } else {
                AmountField.error = "Enter Username"
            }
        }
    }

    internal inner class SendJsonDataToServer3 : AsyncTask<String, String, String>() {
        private var url_link: String? = null

        override fun doInBackground(vararg params: String): String? {
            val jsonResponse: String? = null
            val jsonData = params[0]
            url_link = params[1]
            var urlConnection: HttpURLConnection? = null
            var reader: BufferedReader? = null
            try {
                val url = URL(url_link)

                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "POST"
                urlConnection.doOutput = true
                urlConnection.doInput = true
                urlConnection.setRequestProperty("Content-Type", "application/json")
                urlConnection.setRequestProperty("Accept", "application/json")

                val out = BufferedOutputStream(urlConnection.outputStream)

                val writer = BufferedWriter(OutputStreamWriter(out, "UTF-8"))
                writer.write(jsonData)
                writer.flush()
                writer.close()
                out.close()

                urlConnection.connect()
                val inputStream = urlConnection.inputStream
                val buffer = StringBuffer()
                reader = BufferedReader(InputStreamReader(inputStream))

                val inputLine = reader.readLine()
                while (inputLine != null)
                    buffer.append(inputLine)
                if (buffer.isEmpty()) {
                    Log.i(tag, jsonResponse!! + "empty")
                    return null
                }
                return buffer.toString()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        Log.e(tag, "Error closing stream", e)
                    }

                }
            }
            return null
        }


        override fun onPostExecute(s: String) {
            if (s.contains("sucess"))
                Toast.makeText(baseContext, "TRansaction successed", Toast.LENGTH_LONG).show()
            else
                Toast.makeText(baseContext, "derror", Toast.LENGTH_LONG).show()
        }
    }
}
