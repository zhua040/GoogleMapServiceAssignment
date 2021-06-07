package com.example.googlemaps2;

import org.w3c.dom.Comment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {
    //declare a method and call back later

    @GET("posts") // "post" is a relative URL
    Call<List<Post>> getPosts(); // use the getPost() method to retrieved Json object and store in the List<Post>


    @GET("posts") // "post" is a relative URL
    Call<List<Post>> getPosts(
            @Query("userId") Integer userId,
            @Query("userId") Integer userId2,
            @Query("_sort") String sort,
            @Query("_order") String order
    );





    @GET("posts/{id}/comments")
    Call<List<Comment>> getComments(@Path("id")int postId);
}
