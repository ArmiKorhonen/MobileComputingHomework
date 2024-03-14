package com.example.mobilecomputinghomework

import android.Manifest
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.livedata.observeAsState
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.TextField
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat


// Data class for PuppyProfile
/*data class PuppyProfile(
    val id: Int,
    val name: String,
    val breed: String,
    val imageResId: Int,
    val bio: String
)*/


// Data for puppy profiles
/*val puppies = listOf(
    PuppyProfile(1, "Max", "Golden Retriever", R.drawable.max, "Professional sock thief. Loves to play fetch and dreams of chasing squirrels all day."),
    PuppyProfile(2, "Bella", "Beagle", R.drawable.bella, "Sniffing out snacks and cuddles! Can howl the melody of 'Happy Birthday' and loves belly rubs."),
    PuppyProfile(3, "Charlie", "French Bulldog", R.drawable.charlie, "Small but mighty! Enjoys lounging in the sun and is a connoisseur of fine dog treats."),
    PuppyProfile(4, "Luna", "Siberian Husky", R.drawable.luna, "Adventure seeker! Enjoys long runs and talking back. Known for dramatic opera singing at the moon."),
    PuppyProfile(5, "Cooper", "Labrador Retriever", R.drawable.cooper, "Water-loving, tail-wagging optimist. Aspiring fetch champion and loyal friend."),
    PuppyProfile(6, "Daisy", "German Shepherd", R.drawable.daisy, "Smart and sassy. Loves to play hide and seek and is an expert at finding hidden treats."),
    PuppyProfile(7, "Oliver", "Dachshund", R.drawable.oliver, "Long-bodied, short-legged, and full of sass. Expert napper and professional sunbather."),
    PuppyProfile(8, "Lucy", "Boxer", R.drawable.lucy, "Bouncy and full of energy. Can jump over anything and loves a good game of tug-of-war."),
    PuppyProfile(9, "Bailey", "Poodle", R.drawable.bailey, "Elegant and intelligent. Enjoys puzzle toys and is a master of stylish hairdos."),
    PuppyProfile(10, "Sadie", "Border Collie", R.drawable.sadie, "Energetic and intelligent. Loves learning new tricks and herding anything that moves.")
)*/


// MainActivity is the entry point of the app.
class MainActivity : ComponentActivity(), SensorEventListener {

    companion object {
        const val CHANNEL_ID = "channel_id"
        const val NOTIFICATION_ID = 101
        private val REQUEST_CODE_POST_NOTIFICATIONS = 101 // This is a request code you define for handling permission result
    }


    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private val accelerometerReading = mutableStateOf("No data")
    // Timestamp of the last notification
    private var lastNotificationTime: Long = 0

    // Minimum delay between notifications (e.g., 10000 milliseconds = 10 seconds)
    private val notificationCooldown: Long = 10000

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
            }
        }
    }

    // Handle the permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, you can proceed with showing a notification
            // Depending on your app's flow, you might need to trigger the notification showing logic here again
        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendTiltNotification() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastNotificationTime > notificationCooldown) {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("showDialog", true) // Custom flag to indicate dialog should be shown
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.dog) // replace with your own icon
                .setContentTitle("Who let the dogs out?")
                .setContentText("You might've let the dogs out by tilting your phone so much!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true) // Removes the notification when tapped

            with(NotificationManagerCompat.from(this)) {
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                notify(NOTIFICATION_ID, builder.build()) // NOTIFICATION_ID is a unique int for each notification that you must define
            }
            // Update lastNotificationTime
            lastNotificationTime = currentTime
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the ViewModel
        val app = application as PuppyDatingApp
        val dao = app.database.puppyProfileDao()
        val factory = PuppyViewModelFactory(dao)
        val viewModel = ViewModelProvider(this, factory)[PuppyViewModel::class.java]

        val showDialog = intent.getBooleanExtra("showDialog", false)


        setContent {
            AppTheme {
                val navController = rememberNavController()
                val puppies = viewModel.puppies.observeAsState(listOf()).value

                NavHost(navController = navController, startDestination = "frontPage") {
                    composable("frontPage") { FrontPage(navController) }
                    composable("mainScreen") { AppMainScreen(navController, puppies) }
                    composable("createPuppyProfile") { CreatePuppyProfileScreen(navController, viewModel) }
                }
            }
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        createNotificationChannel()
        checkAndRequestNotificationPermission()

    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { accel ->
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = it.values[0] // X-axis value
                accelerometerReading.value = "X: $x Y: ${it.values[1]} Z: ${it.values[2]}"
                //Log.d("Sensor", "Accelerometer data: ${accelerometerReading.value}")

                // Check if the X-axis value meets the criteria for tilting
                if (x > 9 || x < -9) {
                    Log.d("Sensor", "Tilt detected, attempting to send notification.")
                    sendTiltNotification()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Can be used to respond to changes in sensor accuracy.
    }

}


//"Front page" of the app
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrontPage(navController: NavController) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it), // 'it' refers to the padding provided by Scaffold
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Welcome to the Puppy Friend Finder!",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                "Find new friends for your puppy and get socializing! Tap on a puppy picture to view more details about them. Scroll to see more puppies!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { navController.navigate("mainScreen") }) {
                Text("Go to Puppy Friend Finder")
            }
            Spacer(modifier = Modifier.height(16.dp)) // Add space between the buttons
            Button(onClick = { navController.navigate("createPuppyProfile") }) {
                Text("Create your own puppy profile")
            }

        }
    }
}

// Opt-in annotation to use experimental Material 3 API features.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMainScreen(navController: NavController, puppies: List<PuppyProfile>) {
    // State to keep track of the selected puppy for displaying details.
    val selectedPuppy = remember { mutableStateOf<PuppyProfile?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Puppy Friend Finder") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("frontPage") {
                            popUpTo("frontPage") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            PuppyGrid(puppies) { puppy ->
                selectedPuppy.value = puppy
            }

            selectedPuppy.value?.let {
                PuppyDetailDialog(
                    puppy = it,
                    onDismiss = { selectedPuppy.value = null }
                )
            }
        }
    }
}

// Custom theme wrapper function for the app.
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    // MaterialTheme for the Material Design styling for composables inside it.
    MaterialTheme {
        content()
    }
}

// A composable function that displays a grid of puppy images.
@Composable
fun PuppyGrid(puppies: List<PuppyProfile>, onPuppyClick: (PuppyProfile) -> Unit) {
    Log.d("PuppyGrid", "Puppies count: ${puppies.size}")

    /*Column {
        puppies.forEach { puppy ->
            Text("Puppy name: ${puppy.name}", modifier = Modifier.padding(8.dp))
        }
    }*/

    // LazyVerticalGrid is used to create a grid layout that lazily loads its content. Good for large number of items.
    LazyVerticalGrid(
        // GridCells.Fixed creates a grid with a fixed number of columns.
        columns = GridCells.Fixed(2),
        // Padding around the entire grid.
        contentPadding = PaddingValues(8.dp)
    ) {
        // items function is used to loop over the list of puppies. For each puppy, PuppyImageItem is called.
        items(puppies.size) { index ->
            // PuppyImageItem displays the individual puppy image. It takes a puppy profile and a click handler as parameters.
            PuppyImageItem(puppy = puppies[index], onPuppyClick = onPuppyClick)

        }
    }
}

// Composable function to display an individual puppy image item.
@Composable
fun PuppyImageItem(puppy: PuppyProfile, onPuppyClick: (PuppyProfile) -> Unit) {
    Log.d("PuppyGrid", "Puppy image: ${puppy.imageUri}")
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onPuppyClick(puppy) },
    ) {
        // Use AsyncImage from Coil to load the image from the URI
        AsyncImage(
            model = puppy.imageUri,
            contentDescription = "Puppy image",
            modifier = Modifier
                .size(170.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop,
            onError = { error ->
                Log.d("PuppyGrid", "Error loading image: ${error.result.throwable}")
            },
            //fallback = painterResource(id = R.drawable.fallback_image) // Placeholder in case of error
        )

    }
}


// Composable function to display the detail dialog of a selected puppy.
@Composable
fun PuppyDetailDialog(puppy: PuppyProfile?, onDismiss: () -> Unit) {
    // Check if a puppy profile is available. The dialog is shown only if a puppy is selected.
    if (puppy != null) {
        // AlertDialog is used to show a pop-up dialog with the puppy's details.
        AlertDialog(
            onDismissRequest = onDismiss, // Specifies what to do when the dialog is dismissed.
            title = { Text(text = puppy.name) }, // Title of the dialog set to the puppy's name.
            text = {
                // Column layout to display the puppy's image and text details vertically.
                Column {
                    // Displays the puppy's image.
                    // Use AsyncImage from Coil to load the image from the URI
                    AsyncImage(
                        model = puppy.imageUri,
                        contentDescription = "Puppy image",
                        modifier = Modifier
                            .size(170.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                        onError = { error ->
                            Log.d("PuppyGrid", "Error loading image: ${error.result.throwable}")
                        },
                        //fallback = painterResource(id = R.drawable.fallback_image) // Placeholder in case of error
                    )
                    Spacer(Modifier.height(8.dp)) // Spacer to add some space between the image and the text.
                    // Displaying the puppy's breed with mixed styling.
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Breed: ")
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                                append(puppy.breed)
                            }
                        }
                    )

                    // Displaying the puppy's bio with mixed styling.
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Bio: ")
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                                append(puppy.bio)
                            }
                        }
                    )
                }
            },
            confirmButton = {
                // Button to close the dialog.
                TextButton(onClick = onDismiss) {
                    Text("Close") // Text of the button.
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePuppyProfileScreen(navController: NavController, viewModel: PuppyViewModel) {
    // State variables to store form inputs
    val name = remember { mutableStateOf("") }
    val breed = remember { mutableStateOf("") }
    val bio = remember { mutableStateOf("") }
    val imageUri = remember { mutableStateOf("") }

    val context = LocalContext.current // This gets the context within Compose

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                // Obtain persistable URI permissions
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)

                // Now you can safely store the URI in your database
                imageUri.value = it.toString()
            }
        }
    )


    Scaffold(
        topBar = { /* Your TopAppBar code here */ }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Name") }, // This replaces the decorationBox for hint
                    singleLine = true, // Makes it a single line TextField
                    modifier = Modifier.fillMaxWidth() // To make the TextField take the full width
                )
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = breed.value,
                    onValueChange = { breed.value = it },
                    label = { Text("Breed") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = bio.value,
                    onValueChange = { bio.value = it },
                    label = { Text("Bio") },
                    singleLine = false, // Assuming the bio might be multi-line
                    modifier = Modifier.fillMaxWidth().height(100.dp) // Adjust the height for multiline input
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Select Image")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    // Here you would call a function in your ViewModel to insert the new puppy profile
                    viewModel.insertPuppyProfile(name.value, breed.value, imageUri.value, bio.value)
                    navController.popBackStack() // Go back after insertion
                }) {
                    Text("Create Puppy Profile")
                }
            }
        }
    }
}








