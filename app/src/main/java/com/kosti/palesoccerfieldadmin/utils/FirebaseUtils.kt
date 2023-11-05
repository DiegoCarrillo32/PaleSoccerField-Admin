package com.kosti.palesoccerfieldadmin.utils

import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date

class FirebaseUtils {
    private val db = Firebase.firestore

    fun readCollection(
        collectionName: String,
        callback: (Result<MutableList<HashMap<String, Any>>>) -> Unit
    ) {
        val documents = mutableListOf<HashMap<String, Any>>()
        db.collection(collectionName)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val documentData = document.data as HashMap<String, Any>
                    documentData["id"] = document.id
                    documents.add(documentData)
                }
                callback(Result.success(documents))
            }
            .addOnFailureListener { exception ->
                Log.w("Error getting documents.", exception)
                callback(Result.failure(exception))
            }
    }

    fun getDocumentReferenceById(collectionName: String, idCollection: String): DocumentReference {
        return db.collection(collectionName).document(idCollection)

    }


    fun readCollectionFilter(
        collectionName: String,
        callback: (Result<MutableList<HashMap<String, Any>>>) -> Unit
    ) {
        val documents = mutableListOf<HashMap<String, Any>>()
        db.collection(collectionName)
            .whereEqualTo("estado", false)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val documentData = document.data as HashMap<String, Any>
                    documentData["id"] = document.id
                    documents.add(documentData)
                }
                callback(Result.success(documents))
            }
            .addOnFailureListener { exception ->
                Log.w("Error getting documents.", exception)
                callback(Result.failure(exception))
            }
    }

    fun createDocument(collectionName: String, document: HashMap<String, Any>) {
        db.collection(collectionName)
            .add(document)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error adding document: ${e.toString()}")
            }
    }

    fun deleteDocument(collectionName: String, idCollection: String) {
        db.collection(collectionName).document(idCollection)
            .delete()
            .addOnSuccessListener { println("DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> println("Error deleting document: ${e.toString()}") }
    }

    fun getDocumentById(
        collectionName: String,
        idCollection: String,
        callback: (Result<HashMap<String, Any>>) -> Unit
    ) {
        db.collection(collectionName).document(idCollection)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val documentData = document.data as HashMap<String, Any>
                    documentData["id"] = document.id
                    callback(Result.success(documentData))
                } else {
                    Log.d(TAG, "No such document")
                    callback(Result.failure(Exception("No such document")))
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                callback(Result.failure(exception))
            }
    }

    // This function is used to update a document in a collection by id
    fun updateDocument(
        collectionName: String,
        idCollection: String,
        document: HashMap<String, Any>
    ) {
        db.collection(collectionName).document(idCollection)
            .update(document)
            .addOnSuccessListener { println("DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> println("Error updating document: ${e.toString()}") }
    }

    fun updateProperty(collectionName: String, idCollection: String, property: String, value: Any) {
        db.collection(collectionName).document(idCollection)
            .update(property, value)
            .addOnSuccessListener { println("DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> println("Error updating document: ${e.toString()}") }
    }

    fun getCollectionByProperty(
        collectionName: String, fieldName: String, fieldValue: String,
        callback: (Result<MutableList<HashMap<String, Any>>>) -> Unit
    ) {
        val collection = mutableListOf<HashMap<String, Any>>()
        db.collection(collectionName).whereEqualTo(fieldName, fieldValue)
            .limit(1).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val documentData = document.data as HashMap<String, Any>
                    documentData["id"] = document.id
                    collection.add(documentData)
                }
                callback(Result.success(collection))
            }
            .addOnFailureListener { exception ->
                callback(Result.failure(exception))
            }
    }

    fun deleteUserFromFirestore(collectionName: String, documentId: String, callback: (Boolean) -> Unit) {

        val query = db.collection(collectionName).document(documentId)

        query
            .delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun getUserEmail(documentId: String, callback: (String) -> Unit){
        val query = db.collection("jugadores").document(documentId)

        query
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val response = documentSnapshot.getString("correo")
                    if (response != null) {
                        callback(response)
                    } else {
                        callback("")
                    }
                } else {
                    callback("")
                }
            }.addOnFailureListener {
                callback("")
            }
    }

    fun transformEpochToAge(it: Long): Int {
        val date = Date(it)
        val currentDate = Date()
        return currentDate.year - date.year

    }

    fun transformEpochToDateWithFormat(it: Long): String {
        val date = Date(it)
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(date).toString()
    }

}