import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.*

class CalendarActivity : ComponentActivity() {

    companion object {
        private const val REQUEST_CALENDAR_PERMISSION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarScreen()
        }
    }

    @Composable
    fun CalendarScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Calendar Activity", modifier = Modifier.padding(bottom = 16.dp))

            // Button to add an event to the calendar
            Button(onClick = {
                if (ContextCompat.checkSelfPermission(
                        this@CalendarActivity,
                        android.Manifest.permission.WRITE_CALENDAR
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    addEventToCalendar()
                } else {
                    ActivityCompat.requestPermissions(
                        this@CalendarActivity,
                        arrayOf(android.Manifest.permission.WRITE_CALENDAR),
                        REQUEST_CALENDAR_PERMISSION
                    )
                }
            }) {
                Text(text = "Add Event to Calendar")
            }
        }
    }

    // Function to add an event to the calendar
    private fun addEventToCalendar() {
        // Get the current date and time
        val calendar = Calendar.getInstance()
        val startMillis = calendar.timeInMillis
        val endMillis = startMillis + 60 * 60 * 1000 // 1 hour event

        // Prepare the event details
        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startMillis)
            put(CalendarContract.Events.DTEND, endMillis)
            put(CalendarContract.Events.TITLE, "Sample Event")
            put(CalendarContract.Events.DESCRIPTION, "This is a description of the event.")
            put(CalendarContract.Events.CALENDAR_ID, 1)  // Default calendar
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        // Insert the event into the calendar
        try {
            val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            if (uri != null) {
                Toast.makeText(this, "Event added to Calendar", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to add event", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CALENDAR_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addEventToCalendar() // Retry after permission granted
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
