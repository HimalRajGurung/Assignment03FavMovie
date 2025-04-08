package com.example.assignment03favmovie.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.assignment03favmovie.model.Movie
import com.example.assignment03favmovie.model.MovieCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class Repository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun registerUser(email: String, password: String): LiveData<Result<FirebaseUser?>> {
        val result = MutableLiveData<Result<FirebaseUser?>>()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.value = Result.success(auth.currentUser)
                } else {
                    result.value = Result.failure(task.exception ?: Exception("Unknown error"))
                }
            }

        return result
    }

    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addMovieToFireStore(movie: Movie): Result<Boolean> {
        return try {
            db.collection("movies").add(movie).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMovies(): Result<List<Movie>> {
        return try {
            val snapshot = db.collection("movies").get().await()
            val movieList = snapshot.toObjects(Movie::class.java)
            Result.success(movieList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMovie(movie: Movie): Result<Boolean> {
        return try {
            val movieRef = db.collection("movies")
                .whereEqualTo("movieId", movie.movieId)
                .get()
                .await()
                .documents.firstOrNull()

            if (movieRef != null) {
                movieRef.reference.set(movie).await()
                Result.success(true)
            } else {
                Result.failure(Exception("Movie not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun updateMovie(movie: Movie, callback: MovieCallback) {
        val db = FirebaseFirestore.getInstance()
        db.collection("movies")
            .whereEqualTo("movieId", movie.movieId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val docRef = snapshot.documents[0].reference
                    docRef.update("description", movie.description)
                        .addOnSuccessListener { callback.onSuccess() }
                        .addOnFailureListener { callback.onFailure(it) }
                } else {
                    callback.onFailure(Exception("Movie not found"))
                }
            }
            .addOnFailureListener { callback.onFailure(it) }
    }

    fun deleteMovie(movie: Movie, callback: MovieCallback) {
        val db = FirebaseFirestore.getInstance()
        db.collection("movies")
            .whereEqualTo("movieId", movie.movieId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val docRef = snapshot.documents[0].reference
                    docRef.delete()
                        .addOnSuccessListener { callback.onSuccess() }
                        .addOnFailureListener { callback.onFailure(it) }
                } else {
                    callback.onFailure(Exception("Movie not found"))
                }
            }
            .addOnFailureListener { callback.onFailure(it) }
    }


}

