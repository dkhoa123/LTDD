package com.example.uiappchat.ViewModel

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// Đối tượng lưu trữ state toàn cục cho camera
object CameraState {
    var isFrontCamera = mutableStateOf(false)
    var flashMode = mutableStateOf(ImageCapture.FLASH_MODE_OFF)
    var imageCapture: ImageCapture? = null
    var lastCapturedImageUri = mutableStateOf<Uri?>(null)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PreCameraScreen() {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        CameraView(
            isFrontCamera = CameraState.isFrontCamera.value,
            flashMode = CameraState.flashMode.value
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
    }
}

@Composable
fun CameraView(
    isFrontCamera: Boolean,
    flashMode: Int
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    var error by remember { mutableStateOf<String?>(null) }

    val cameraSelector = remember(isFrontCamera) {
        if (isFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
    }

    LaunchedEffect(cameraSelector, flashMode) {
        try {
            val cameraProvider = context.getCameraProvider()
            cameraProvider.unbindAll()

            CameraState.imageCapture = ImageCapture.Builder()
                .setFlashMode(flashMode)
                .build()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                CameraState.imageCapture
            )
        } catch (e: Exception) {
            error = "Không thể khởi tạo camera: ${e.message}"
            Log.e("CameraView", "Error", e)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        error?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

    cameraProviderFuture.addListener({
        try {
            val cameraProvider = cameraProviderFuture.get()
            continuation.resume(cameraProvider)
        } catch (e: Exception) {
            Log.e("CameraX", "Lỗi khởi tạo camera provider", e)
            throw e
        }
    }, ContextCompat.getMainExecutor(this))
}

// Chuyển đổi camera trước/sau
fun toggleCamera() {
    CameraState.isFrontCamera.value = !CameraState.isFrontCamera.value
}

// Chuyển đổi chế độ đèn flash
fun toggleFlash() {
    CameraState.flashMode.value = when (CameraState.flashMode.value) {
        ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
        ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
        else -> ImageCapture.FLASH_MODE_OFF
    }
}

// Chụp ảnh và lưu vào bộ nhớ
fun captureImage(
    context: Context,
    onImageCaptured: (Uri) -> Unit = {},
    onError: (ImageCaptureException) -> Unit = {}
) {
    val imageCapture = CameraState.imageCapture ?: return

    // Tạo tên file dựa trên thời gian
    val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault())
        .format(System.currentTimeMillis())

    // ContentValues để lưu vào MediaStore
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "$name.jpg")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
        }
    }

    // Cấu hình đầu ra
    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        .build()

    // Chụp ảnh
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = outputFileResults.savedUri
                if (savedUri != null) {
                    Toast.makeText(context, "Ảnh đã được lưu", Toast.LENGTH_SHORT).show()
                    CameraState.lastCapturedImageUri.value = savedUri
                    onImageCaptured(savedUri)
                } else {
                    Toast.makeText(context, "Không thể lưu ảnh", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Lỗi: ${exception.message}", Toast.LENGTH_SHORT).show()
                onError(exception)
            }
        }
    )
}

// Mở ảnh đã chụp bằng ứng dụng mặc định
fun openImage(context: Context, uri: Uri) {
    val intent = android.content.Intent().apply {
        action = android.content.Intent.ACTION_VIEW
        setDataAndType(uri, "image/*")
        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Không thể mở ảnh", Toast.LENGTH_SHORT).show()
    }
}

// Hàm này để sử dụng trong CameraScreen khi cần chụp ảnh
fun handleCapture(
    context: Context,
    onCaptured: (Uri) -> Unit
) {
    captureImage(
        context = context,
        onImageCaptured = { uri ->
            Log.d("Camera", "Ảnh được lưu tại: $uri")
            onCaptured(uri)
        },
        onError = { exception ->
            Log.e("Camera", "Lỗi chụp ảnh: ${exception.message}")
        }
    )
}
