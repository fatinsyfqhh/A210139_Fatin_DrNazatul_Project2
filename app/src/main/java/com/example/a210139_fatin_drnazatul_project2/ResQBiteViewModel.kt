package com.example.a210139_fatin_drnazatul_project2

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.a210139_fatin_drnazatul_project2.data.FoodEntity
import com.example.a210139_fatin_drnazatul_project2.data.FoodRepository
import com.example.a210139_fatin_drnazatul_project2.data.FirebaseRepository
import com.example.a210139_fatin_drnazatul_project2.data.LocationHelper
import com.example.a210139_fatin_drnazatul_project2.data.OpenFoodApi
import com.example.a210139_fatin_drnazatul_project2.data.OpenFoodProduct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ResQBiteViewModel(
    private val repository: FoodRepository,
    private val firebaseRepository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    // ROOM (private contributions)
    val myContributions: StateFlow<List<FoodEntity>> = repository.myContributions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteFoodItem(food: FoodEntity) {
        viewModelScope.launch {
            try {
                repository.deleteFood(food)
            } catch (e: Exception) {
                android.util.Log.e("Delete", "Item not in Room, skipping: ${e.message}")
            }
        }
    }

    // FIREBASE (public food feed)
    private val _publicFoodList = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val publicFoodList: StateFlow<List<Map<String, Any>>> = _publicFoodList.asStateFlow()

    // FIREBASE (community posts)
    private val _communityPosts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val communityPosts: StateFlow<List<CommunityPost>> = _communityPosts.asStateFlow()

    init {
        firebaseRepository.seedInitialDataIfEmpty()

        viewModelScope.launch {
            firebaseRepository.getPublicFoodItems().collect { items ->
                _publicFoodList.value = items
            }
        }

        viewModelScope.launch {
            firebaseRepository.getCommunityPosts().collect { posts ->
                _communityPosts.value = posts.map { post ->
                    CommunityPost(
                        id = post["id"] as? String ?: "",
                        userName = post["userName"] as? String ?: "",
                        action = post["action"] as? String ?: "",
                        time = post["time"] as? String ?: "",
                        likes = (post["likes"] as? Long)?.toInt() ?: 0
                    )
                }
            }
        }
    }

    fun addMyFoodItem(newItem: FoodEntity) {
        viewModelScope.launch {
            repository.insert(newItem)
            val category = getCategoryFromImageRes(newItem.imageRes)
            firebaseRepository.addFoodItem(
                mapOf(
                    "title" to newItem.title,
                    "distance" to newItem.distance,
                    "user" to newItem.user,
                    "location" to newItem.location,
                    "category" to category,
                    "isUserContribution" to true
                )
            )
        }
    }

    fun deleteFirebaseFoodItem(documentId: String) {
        firebaseRepository.deleteFoodItem(documentId)
    }

    fun getIconForCategory(category: String): Int {
        return when (category) {
            "Bakery" -> R.drawable.croissant
            "Canned" -> R.drawable.canned_tuna
            "Packed Meals" -> R.drawable.packedmeals
            "Fruits" -> R.drawable.fruits
            "Drinks" -> R.drawable.drinks
            "Snacks" -> R.drawable.croissant
            else -> R.drawable.resqlogo
        }
    }

    fun getCategoryFromImageRes(imageRes: Int): String {
        return when (imageRes) {
            R.drawable.croissant -> "Bakery"
            R.drawable.canned_tuna -> "Canned"
            R.drawable.packedmeals -> "Packed Meals"
            R.drawable.fruits -> "Fruits"
            R.drawable.drinks -> "Drinks"
            else -> "Other"
        }
    }

    fun toggleLike(postId: String) {
        val post = _communityPosts.value.find { it.id == postId } ?: return
        firebaseRepository.toggleLike(postId, post.likes, post.isLiked)
        _communityPosts.value = _communityPosts.value.map {
            if (it.id == postId) {
                if (it.isLiked) it.copy(likes = it.likes - 1, isLiked = false)
                else it.copy(likes = it.likes + 1, isLiked = true)
            } else it
        }
    }

    fun addStory(userName: String, action: String) {
        firebaseRepository.addCommunityPost(
            mapOf(
                "userName" to userName,
                "action" to action,
                "time" to "Just now",
                "likes" to 0,
                "timestamp" to System.currentTimeMillis()
            )
        )
    }

    // OpenFoodFacts API
    private val _foodSearchResults = MutableStateFlow<List<OpenFoodProduct>>(emptyList())
    val foodSearchResults: StateFlow<List<OpenFoodProduct>> = _foodSearchResults.asStateFlow()

    private val _isSearchingApi = MutableStateFlow(false)
    val isSearchingApi: StateFlow<Boolean> = _isSearchingApi.asStateFlow()

    private val _apiError = MutableStateFlow<String?>(null)
    val apiError: StateFlow<String?> = _apiError.asStateFlow()

    fun searchFoodFromApi(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isSearchingApi.value = true
            _apiError.value = null
            var lastException: Exception? = null
            repeat(3) { attempt ->
                try {
                    val response = OpenFoodApi.service.searchFood(query)
                    val filtered = response.products.filter {
                        !it.product_name.isNullOrBlank()
                    }
                    _foodSearchResults.value = filtered
                    _isSearchingApi.value = false
                    return@launch
                } catch (e: Exception) {
                    lastException = e
                    android.util.Log.e("OpenFoodApi", "Attempt ${attempt + 1} failed: ${e.message}")
                    if (attempt < 2) kotlinx.coroutines.delay(1000L)
                }
            }
            _apiError.value = "Could not fetch results after 3 attempts. Try again!"
            _foodSearchResults.value = emptyList()
            _isSearchingApi.value = false
        }
    }

    fun clearFoodSearch() {
        _foodSearchResults.value = emptyList()
        _apiError.value = null
    }

    fun getCategoryFromApiTags(tags: List<String>?): String {
        if (tags == null) return "Other"
        return when {
            tags.any { it.contains("bakery") || it.contains("bread") || it.contains("pastry") } -> "Bakery"
            tags.any { it.contains("canned") || it.contains("tuna") || it.contains("tin") } -> "Canned"
            tags.any { it.contains("fruit") } -> "Fruits"
            tags.any { it.contains("beverage") || it.contains("drink") || it.contains("juice") } -> "Drinks"
            tags.any { it.contains("snack") || it.contains("chip") || it.contains("biscuit") } -> "Snacks"
            tags.any { it.contains("meal") || it.contains("rice") || it.contains("noodle") } -> "Packed Meals"
            else -> "Other"
        }
    }

    // GPS location
    private val _currentLocation = MutableStateFlow("Bangi")
    val currentLocation: StateFlow<String> = _currentLocation.asStateFlow()

    private val _isLoadingLocation = MutableStateFlow(false)
    val isLoadingLocation: StateFlow<Boolean> = _isLoadingLocation.asStateFlow()

    fun fetchCurrentLocation(context: Context) {
        viewModelScope.launch {
            _isLoadingLocation.value = true
            try {
                val locationHelper = LocationHelper(context.applicationContext)
                val lastKnown = locationHelper.getLastKnownLocation()
                if (lastKnown != null) {
                    val (lat, lng) = lastKnown
                    val address = locationHelper.getAddressFromCoordinates(lat, lng)
                    _currentLocation.value = address
                    _isLoadingLocation.value = false
                }
                val precise = locationHelper.getCurrentLocation()
                if (precise != null) {
                    val (lat, lng) = precise
                    val address = locationHelper.getAddressFromCoordinates(lat, lng)
                    _currentLocation.value = address
                }
            } catch (e: Exception) {
                android.util.Log.e("GPS", "Location fetch failed: ${e.message}")
            } finally {
                _isLoadingLocation.value = false
            }
        }
    }

    // impact tracker
    private val _savedMealsCount = MutableStateFlow(10)
    val savedMealsCount: StateFlow<Int> = _savedMealsCount.asStateFlow()

    fun incrementSavedMeals() {
        _savedMealsCount.value += 1
    }

}

class ResQBiteViewModelFactory(private val repository: FoodRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResQBiteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ResQBiteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}