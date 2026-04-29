package com.example.darepack_complete.models

data class ProductModel(
    var id: String? = "",
    var productName: String = "",
    var price: String = "",
    var quantity: String = "",
    var description: String = "",
    var dateManufacture: String = "",
    var barcodeNumber: String = "",
    var imageUrl: String? = ""
)
