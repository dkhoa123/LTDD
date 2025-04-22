package com.example.uiappchat.ViewModel

import android.util.Log
import com.example.uiappchat.Model.InfoUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

object addFriend {

    fun searchUserByEmail(email: String, onResult: (InfoUser?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("email", email)  // Tìm kiếm theo email
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val user = documents.documents[0].toObject(InfoUser::class.java)
                    onResult(user)
                } else {
                    onResult(null)  // Không tìm thấy
                }
            }
            .addOnFailureListener {
                onResult(null)  // Lỗi khi tìm kiếm
            }
    }


    fun sendFriendRequest(senderId: String, senderName: String, senderEmail: String, receiverId: String) {
        val database = FirebaseDatabase.getInstance().getReference("friend_requests")

        val request = mapOf(
            "senderEmail" to senderEmail,
            "senderName" to senderName
        )

        // Store under friend_requests/{receiverId}/{senderId}
        database.child(receiverId).child(senderId).setValue(request)
            .addOnSuccessListener {
                Log.d("FriendRequest", "Friend request sent from $senderId to $receiverId.")
            }
            .addOnFailureListener { e ->
                Log.e("FriendRequest", "Error sending friend request: ${e.message}", e)
            }
    }

    fun getFriendsListFromRealtimeDB(userId: String, onResult: (List<InfoUser>) -> Unit) {
        val database = Firebase.database.reference
        val friendsRef = database.child("friends").child(userId)
        val usersRef = database.child("users")

        friendsRef.get().addOnSuccessListener { snapshot ->
            val friendIds = snapshot.children.map { it.key.toString() }
            val friendsList = mutableListOf<InfoUser>()

            friendIds.forEach { friendId ->
                usersRef.child(friendId).get().addOnSuccessListener { userSnapshot ->
                    val user = userSnapshot.getValue(InfoUser::class.java)
                    if (user != null) {
                        friendsList.add(user)
                    }
                    if (friendsList.size == friendIds.size) {
                        onResult(friendsList)
                    }
                }
            }
        }
    }


    fun fetchFriendInfo(friendId: String, onResult: (String, Boolean) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(friendId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val firstName = snapshot.child("firstName").value?.toString() ?: ""
                val lastName = snapshot.child("lastName").value?.toString() ?: ""
                val isOnline = snapshot.child("isOnline").value?.toString()?.toBoolean() ?: false

                onResult("$firstName $lastName", isOnline)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Lỗi khi lấy thông tin bạn bè: ${error.message}")
            }
        })
    }


}