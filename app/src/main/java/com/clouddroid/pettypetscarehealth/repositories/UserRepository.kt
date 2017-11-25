package com.clouddroid.pettypetscarehealth.repositories

import com.google.firebase.auth.FirebaseAuth

/**
 * Created by Arkadiusz on 20.11.2017
 */

class UserRepository {

    private var listener: OnLoggedListener? = null
    private val mAuth = FirebaseAuth.getInstance()

    interface OnLoggedListener {
        fun onLoggedResult(wasSuccessful: Boolean)
    }

    fun isLoggedIn(): Boolean {
        val currentUser = mAuth.currentUser
        return currentUser != null
    }

    fun signInWithLoginAndPassword(login: String, password: String) {
        if (login.isNotEmpty() && password.isNotEmpty()) {
            mAuth.signInWithEmailAndPassword(login, password).addOnCompleteListener {
                listener?.onLoggedResult(it.isSuccessful)
            }
        }
    }


    fun addOnLoggedListener(listener: OnLoggedListener) {
        this.listener = listener
    }

    fun signOut() {
        mAuth.signOut()
    }
}