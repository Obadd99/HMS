package com.developers.healtywise.data.netWork.account

import android.net.Uri
import android.util.Log
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.common.helpers.utils.Constants.HOLDER_ICON
import com.developers.healtywise.common.helpers.utils.Constants.MESSAGES
import com.developers.healtywise.common.helpers.utils.Constants.POSTS
import com.developers.healtywise.common.helpers.utils.Constants.TAG
import com.developers.healtywise.common.helpers.utils.Constants.USERS
import com.developers.healtywise.domin.models.main.ChatMessage
import com.developers.healtywise.domin.models.main.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class AccountService @Inject constructor(
    private val auth: FirebaseAuth,
) {
    private val users = FirebaseFirestore.getInstance().collection(USERS)
    private val posts = FirebaseFirestore.getInstance().collection(POSTS)
    private val messages = FirebaseFirestore.getInstance().collection(MESSAGES)
    private val storage = Firebase.storage

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        mobile: String,
        password: String,
        birthdate: String,
        doctor: Boolean,
        male: Boolean,
        imageUri: String?,
    ): User {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid!!
        var imageURL = HOLDER_ICON
        imageUri?.let {
            val imageUploadResult =
                storage.getReference(email).putFile(Uri.parse(it)).await()
            imageURL =
                imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()
        }
        val user = User(uid, firstName, lastName, email, mobile, imageURL, birthdate, doctor, male)
        users.document(uid).set(user).await()
        return user
    }


    suspend fun login(email: String, password: String): User {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return if (result.user != null) {
            val currentUid = auth.uid!!
            Log.i("TAG", "login:1 ${currentUid}")
            val currentUser = users.document(currentUid).get().await().toObject(User::class.java)
                ?: throw  IllegalStateException()
            Log.i("TAG", "login:2 ${currentUser}")
            currentUser
        } else {
            Log.i("TAG", "login:3 ")
            User()
        }
    }

    suspend fun createPost(text: String): Any {
        val uid = auth.currentUser!!.uid
        val postId = UUID.randomUUID().toString()
        val post = Post(
            id = postId,
            authorUid = uid,
            text = text,
            date = System.currentTimeMillis()
        )
        posts.document(postId).set(post).await()

        return Any()
    }

    suspend fun deletingPost(post: Post): Post {
        posts.document(post.id).delete().await()
//            storage.getReferenceFromUrl(post.imageUrl).delete().await()
        return post
    }

    suspend fun searchDoctorUser(query: String,userDoctor:Boolean=false): List<User> {
        val userResult = if (query.isNotEmpty()) {
            users.whereLessThanOrEqualTo("firstName", query)
                .whereEqualTo("doctor", userDoctor)
                .get().await().toObjects(User::class.java)
        } else {
            users.whereEqualTo("doctor", true).get().await().toObjects(User::class.java)
        }

        return userResult
    }

    suspend fun getUser(uid: String): User {
        val user = users.document(uid).get().await().toObject(User::class.java)
            ?: throw  IllegalStateException()
//        val currentUid = FirebaseAuth.getInstance().uid!!
//        val currentUser = users.document(currentUid).get().await().toObject(User::class.java)
//            ?: throw  IllegalStateException()
        return user
    }

    suspend fun getPosts(): List<Post> {
        val allposts =
            posts.orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().await().toObjects(Post::class.java)
                .onEach { post ->
                    val user = getUser(post.authorUid)
                    post.authorProfilePictureUrl = user.imageProfile
                    post.authorUsername = "${user.firstName} ${user.lastName}"
                }
        return allposts
    }

    suspend fun sendMessage(message: String, receiverId: String): Any {
        val uid = auth.currentUser!!.uid
        val messageId = UUID.randomUUID().toString()
        val messageChat = ChatMessage(
            id = messageId,
            date = System.currentTimeMillis(),
            sendId = uid,
            receiverId = receiverId,
            message = message
        )
        messages.document(messageId).set(messageChat).await()
        Log.i(TAG, "sendMessage: ")
        return Any()
    }

    fun getMessage(messages: List<ChatMessage>): List<ChatMessage> = messages

    fun addMessageHotSnap(sendId: String, receiverId: String) {
        getMessage(emptyList())
        messages.orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .whereEqualTo("sendId", sendId)
            .whereEqualTo("receiverId", receiverId)
            .addSnapshotListener(messageListener)
        messages.orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .whereEqualTo("sendId", receiverId)
            .whereEqualTo("receiverId", sendId)
            .addSnapshotListener(messageListener)
    }

    private val messageListener: EventListener<QuerySnapshot> = EventListener { value, error ->
        value?.let {
            Log.i(TAG, "EventListener:${it.toString()} ")
            val messages = it.toObjects(ChatMessage::class.java)
            getMessage(messages)
        }
    }
}