package com.example.mobilecomputinghomework

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


// Data class for PuppyProfile
data class PuppyProfile(
    val id: Int,
    val name: String,
    val breed: String,
    val imageResId: Int,
    val bio: String
)


// Data for puppy profiles
val puppies = listOf(
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
)

// MainActivity is the entry point of the app.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "frontPage") {
                    composable("frontPage") { FrontPage(navController) }
                    composable("mainScreen") { AppMainScreen(navController) }
                }
            }
        }
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
        }
    }
}





// Opt-in annotation to use experimental Material 3 API features.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMainScreen(navController: NavController) {
    // State to keep track of the selected puppy for displaying details.
    val selectedPuppy = remember { mutableStateOf<PuppyProfile?>(null) }

    // Scaffold provides basic material design layout structure.
    Scaffold(
        // Defines the top app bar of the app.
        topBar = {
            TopAppBar(
                title = { Text("Puppy Friend Finder") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("frontPage") {
                            // Clearing everything up to the 'frontPage' from the back stack
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
        // Box layout to hold the content of the screen.
        Box(modifier = Modifier.padding(innerPadding)) {
            // Grid of puppy images. On clicking an image, updates the selectedPuppy state.
            PuppyGrid(puppies) { puppy ->
                selectedPuppy.value = puppy
            }

            // Displays the puppy detail dialog if a puppy is selected.
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
    // Card is used to provide a material design card layout for each puppy image.
    Card(
        modifier = Modifier
            .padding(8.dp) // Adds padding around the card.
            .clickable { onPuppyClick(puppy) }, // Makes the card clickable, triggering onPuppyClick with the puppy's profile when clicked.
    ) {
        // Image composable to display the puppy's picture.
        Image(
            painter = painterResource(id = puppy.imageResId), // Loads the image resource.
            contentDescription = "Puppy image", // Provides a content description for accessibility.
            modifier = Modifier.size(170.dp) // Sets the size of the image.
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
                    Image(
                        painter = painterResource(id = puppy.imageResId), // Loads the image from resources.
                        contentDescription = "Puppy image", // Accessibility description of the image.
                        modifier = Modifier
                            .fillMaxWidth() // Image occupies the maximum width available.
                            .height(300.dp) // Fixed height for the image.
                            .clip(RoundedCornerShape(8.dp)) // Rounded corners for the image.
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






