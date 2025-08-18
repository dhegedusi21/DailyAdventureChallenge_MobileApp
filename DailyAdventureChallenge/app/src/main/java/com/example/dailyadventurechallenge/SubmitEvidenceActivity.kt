package com.example.dailyadventurechallenge

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberAsyncImagePainter
import com.example.dailyadventurechallenge.ui.Submission.SubmissionViewModel
import com.example.dailyadventurechallenge.ui.Submission.SubmissionViewModelFactory
import com.example.dailyadventurechallenge.ui.theme.DailyAdventureChallengeTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SubmitEvidenceActivity : ComponentActivity() {

    private lateinit var submissionViewModel: SubmissionViewModel
    private var challengeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        challengeId = intent.getIntExtra("CHALLENGE_ID", -1)
        if (challengeId == -1) {
            Toast.makeText(this, "Error: Challenge ID not found.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val factory = SubmissionViewModelFactory(application)
        submissionViewModel = ViewModelProvider(this, factory)[SubmissionViewModel::class.java]

        setContent {
            DailyAdventureChallengeTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SubmitEvidenceScreen(
                        challengeId = challengeId,
                        viewModel = submissionViewModel,
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitEvidenceScreen(
    challengeId: Int,
    viewModel: SubmissionViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val selectedImageUri by viewModel.selectedImageUri.observeAsState()
    val isLoadingImageUpload by viewModel.isLoadingImageUpload.observeAsState(false)
    val isLoadingCreateSubmission by viewModel.isLoadingCreateSubmission.observeAsState(false)

    val imageUploadResultObserved by viewModel.imageUploadResult.observeAsState()
    val submissionResultObserved by viewModel.submissionResult.observeAsState()

    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.setSelectedImageUri(uri)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            viewModel.setSelectedImageUri(tempPhotoUri)
        }
    }

    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            tempPhotoUri = context.createImageFileUri()
            cameraLauncher.launch(tempPhotoUri)
        } else {
            Toast.makeText(context, "Camera permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(submissionResultObserved) {
        submissionResultObserved?.let { currentSubmissionResult ->
            when (currentSubmissionResult) {
                is com.example.dailyadventurechallenge.data.repository.Result.Success -> {
                    Toast.makeText(context, "Submission successful!", Toast.LENGTH_LONG).show()
                    viewModel.resetSubmissionState()
                    onBack()
                }
                is com.example.dailyadventurechallenge.data.repository.Result.Error -> {
                    Toast.makeText(context, "Submission failed: ${currentSubmissionResult.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Submit Evidence") },
                navigationIcon = {
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Challenge ID: $challengeId",
                style = MaterialTheme.typography.titleMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Selected image preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text("No image selected. Pick one below!")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoadingImageUpload && !isLoadingCreateSubmission
                ) {
                    Text("From Gallery")
                }
                Button(
                    onClick = {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                                tempPhotoUri = context.createImageFileUri()
                                cameraLauncher.launch(tempPhotoUri)
                            }
                            else -> {
                                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoadingImageUpload && !isLoadingCreateSubmission
                ) {
                    Text("Take Photo")
                }
            }

            if (selectedImageUri != null) {
                Button(
                    onClick = { viewModel.uploadAndCreateSubmission(challengeId) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoadingImageUpload && !isLoadingCreateSubmission
                ) {
                    if (isLoadingImageUpload || isLoadingCreateSubmission) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(Modifier.width(8.dp))
                        Text("Submitting...")
                    } else {
                        Text("Submit Evidence")
                    }
                }
            }

            imageUploadResultObserved?.let { currentImageUploadResult ->
                if (currentImageUploadResult is com.example.dailyadventurechallenge.data.repository.Result.Error) {
                    Text(
                        "Image Upload Failed: ${currentImageUploadResult.exception.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

fun Context.createImageFileUri(): Uri {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir: File? = getExternalFilesDir("DAC_Temp_Images")
    if (storageDir != null && !storageDir.exists()) {
        storageDir.mkdirs()
    }
    val imageFile = File.createTempFile(
        imageFileName,
        ".jpg",
        storageDir
    )
    return FileProvider.getUriForFile(
        this,
        "${BuildConfig.APPLICATION_ID}.provider",
        imageFile
    )
}

