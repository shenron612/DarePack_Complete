package com.example.darepack_complete.data


import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.darepack_complete.models.ProductModel
import com.example.darepack_complete.navigation.Routes
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.InputStream

class ProductViewModel : ViewModel() {
private val cloudinaryUrl = "https://api.cloudinary.com/v1_1/dkq3txb6h/image/upload"
private val uploadPreset = "first_app"

fun uploadProduct(
    imageUri: Uri?,
    productName: String,
    price: String,
    quantity: String,
    description: String,
    dateManufacture: String,
    barcodeNumber: String,
    context: Context,
    navController: NavController
) {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            val imageUrl = imageUri?.let { uploadToCloudinary(context, it) }
            val ref = FirebaseDatabase.getInstance().getReference("Products").push()
            val productData = ProductModel(
                id = ref.key,
                productName = productName,
                price = price,
                quantity = quantity,
                description = description,
                dateManufacture = dateManufacture,
                barcodeNumber = barcodeNumber,
                imageUrl = imageUrl
            )
            ref.setValue(productData).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Product saved Successfully", Toast.LENGTH_LONG).show()
                                navController.navigate(Routes.VIEW_PRODUCT)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Product not saved: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

private fun uploadToCloudinary(context: Context, uri: Uri): String {
    val contentResolver = context.contentResolver
    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    val fileBytes = inputStream?.readBytes() ?: throw Exception("Image read failed")
    val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart(
            "file", "image.jpg",
            RequestBody.create("image/*".toMediaTypeOrNull(), fileBytes)
        )
        .addFormDataPart("upload_preset", uploadPreset).build()
    val request = Request.Builder().url(cloudinaryUrl).post(requestBody).build()
    val response = OkHttpClient().newCall(request).execute()
    if (!response.isSuccessful) throw Exception("Upload failed")
    val responseBody = response.body?.string()
    val secureUrl = Regex("\"secure_url\":\"(.*?)\"")
        .find(responseBody ?: "")?.groupValues?.get(1)
    return secureUrl ?: throw Exception("Failed to get image URL")
}

private val _products = mutableStateListOf<ProductModel>()
val products: List<ProductModel> = _products

fun fetchProduct(context: Context) {
    val ref = FirebaseDatabase.getInstance().getReference("Products")
    ref.get().addOnSuccessListener { snapshot ->
        _products.clear()
        for (child in snapshot.children) {
            val product = child.getValue(ProductModel::class.java)
            product?.let {
                it.id = child.key
                _products.add(it)
            }
        }
    }.addOnFailureListener {
        Toast.makeText(context, "Failed to load products", Toast.LENGTH_LONG).show()
    }
}

fun updateProduct(
    productId: String,
    imageUri: Uri?,
    productName: String,
    price: String,
    quantity: String,
    description: String,
    dateManufacture: String,
    barcodeNumber: String,
    context: Context,
    navController: NavController
) {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            var imageUrl: String? = null
            if (imageUri != null) {
                imageUrl = uploadToCloudinary(context, imageUri)
            }

            val ref = FirebaseDatabase.getInstance().getReference("Products").child(productId)

            val updates = mutableMapOf<String, Any?>(
                "productName" to productName,
                "price" to price,
                "quantity" to quantity,
                "description" to description,
                "dateManufacture" to dateManufacture,
                "barcodeNumber" to barcodeNumber
            )
            if (imageUrl != null) {
                updates["imageUrl"] = imageUrl
            }

            ref.updateChildren(updates).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Update Successful", Toast.LENGTH_LONG).show()
                                navController.navigate(Routes.DASHBOARD)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Update Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

fun deleteProduct(productId: String, context: Context) {
    val ref = FirebaseDatabase.getInstance()
        .getReference("Products").child(productId)
    ref.removeValue().addOnSuccessListener {
        _products.removeAll { it.id == productId }
        Toast.makeText(context, "Product Deleted", Toast.LENGTH_LONG).show()
    }.addOnFailureListener {
        Toast.makeText(context, "Product Deletion Failed", Toast.LENGTH_LONG).show()
    }
}
}

