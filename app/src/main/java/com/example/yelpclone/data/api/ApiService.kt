package com.example.yelpclone.data.api

import com.example.yelpclone.data.model.users.UserList
import com.example.yelpclone.data.model.yelp.YelpSearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {

    /*
    We are going to search for Yelp restaurants. In the API documentation, it specifies that we
    have to search based on one query parameters -> location. However, we also are going to
    search based on the term - "food or "restaurant", etc.
     */
    @GET("businesses/search")
    suspend fun searchRestaurants(
        @Header("Authorization") authHeader: String,
        @Query("term") searchTerm: String,
        @Query("location") location: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ) : Response<YelpSearchResult>
}