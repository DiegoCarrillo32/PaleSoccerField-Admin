package com.kosti.palesoccerfieldadmin.utils
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
class FirebaseUtils {
    private val db = Firebase.firestore

    fun readCollection(collectionName: String, callback: (Result<MutableList<HashMap<String, Any>>> ) -> Unit) {
        val documets = mutableListOf<HashMap<String, Any>>()
        db.collection(collectionName)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    documets.add(document.data as HashMap<String, Any>)
                }
                callback(Result.success(documets))
            }
            .addOnFailureListener { exception ->
                Log.w("Error getting documents.", exception)
                callback(Result.failure(exception))
            }
    }


    fun createDocument(collectionName: String, document: HashMap<String, Any>){
        db.collection("cities")
            .add(document)
            .addOnSuccessListener { documentReference ->
                println( "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println( "Error adding document: ${e.toString()}")
            }
    }

    fun deleteDocument(collectionName: String, idCollection: String){
        db.collection(collectionName).document(idCollection)
            .delete()
            .addOnSuccessListener { println("DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> println("Error deleting document: ${e.toString()}") }
    }
}