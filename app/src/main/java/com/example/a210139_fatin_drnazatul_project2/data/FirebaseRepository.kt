package com.example.a210139_fatin_drnazatul_project2.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseRepository {
    private val db = Firebase.firestore

    // foodcards
    fun getPublicFoodItems(): Flow<List<Map<String, Any>>> = callbackFlow {
        val listener = db.collection("food_items")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val items = snapshot?.documents?.mapNotNull { it.data?.plus("id" to it.id) } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    fun addFoodItem(food: Map<String, Any>) {
        db.collection("food_items").add(food)
    }

    fun deleteFoodItem(documentId: String) {
        db.collection("food_items").document(documentId).delete()
    }

    // community posts
    fun getCommunityPosts(): Flow<List<Map<String, Any>>> = callbackFlow {
        val listener = db.collection("community_posts")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val posts = snapshot?.documents?.mapNotNull { it.data?.plus("id" to it.id) } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    fun addCommunityPost(post: Map<String, Any>) {
        db.collection("community_posts").add(post)
    }

    fun toggleLike(documentId: String, currentLikes: Int, isLiked: Boolean) {
        db.collection("community_posts").document(documentId).update(
            mapOf(
                "likes" to if (isLiked) currentLikes - 1 else currentLikes + 1
            )
        )
    }

    // initial data
    fun seedInitialDataIfEmpty() {
        db.collection("food_items").get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    val initialItems = listOf(
                        mapOf("title" to "Extra Croissants", "distance" to "1.2km", "user" to "NearBakery", "location" to "Bangi", "category" to "Bakery", "isUserContribution" to false),
                        mapOf("title" to "Tin Tuna", "distance" to "0.8km", "user" to "Mira", "location" to "KIY", "category" to "Canned", "isUserContribution" to false),
                        mapOf("title" to "Roti Gardenia Pandan", "distance" to "1.0km", "user" to "Amar", "location" to "KPZ", "category" to "Bakery", "isUserContribution" to false),
                        mapOf("title" to "Nasi Ayam", "distance" to "0.3km", "user" to "Qila", "location" to "KUO", "category" to "Packed Meals", "isUserContribution" to false)
                    )
                    initialItems.forEach { db.collection("food_items").add(it) }
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("Firebase", "Seed failed: ${e.message}")
            }

        db.collection("community_posts").get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    val initialPosts = listOf(
                        mapOf("userName" to "Mark", "action" to "saved 2 boxes of Donuts", "time" to "5m ago", "likes" to 12, "timestamp" to 4L),
                        mapOf("userName" to "Fatin", "action" to "shared a tray of Muffins", "time" to "15m ago", "likes" to 24, "timestamp" to 3L),
                        mapOf("userName" to "Rose", "action" to "collected Packed Meals", "time" to "1h ago", "likes" to 8, "timestamp" to 2L),
                        mapOf("userName" to "Sarah", "action" to "donated 5 packs of Fried Rice", "time" to "3h ago", "likes" to 45, "timestamp" to 1L)
                    )
                    initialPosts.forEach { db.collection("community_posts").add(it) }
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("Firebase", "Community seed failed: ${e.message}")
            }
    }
}