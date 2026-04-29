package com.example.darepack_complete.navigation

object Routes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val PROFILE = "profile"
    const val GROUPS = "groups"
    const val BUCKET_LIST = "bucket_list"
    const val CREATE_GROUP = "create_group"
    const val SEND_DARE = "send_dare/{itemId}/{groupId}"
    const val DARE_DETAIL = "dare_detail/{dareId}"
    const val MEMORIES = "memories"
    const val DASHBOARD = "dashboard"
    const val VIEW_PRODUCT = "view_product"

    fun sendDare(itemId: String, groupId: String) = "send_dare/$itemId/$groupId"
    fun dareDetail(dareId: String) = "dare_detail/$dareId"
}
