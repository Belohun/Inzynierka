package com.example.inzynierka.ui.login

import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

import androidx.navigation.fragment.findNavController
import com.example.inzynierka.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException


class RegisterFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        val root = inflater.inflate(R.layout.fragment_register, container, false)
        val emailTextView = root.findViewById<TextInputEditText>(R.id.email_register_text)
        val passwordTextView = root.findViewById<TextInputEditText>(R.id.password_register_text)
        root.findViewById<Button>(R.id.btn_register).setOnClickListener {
            var noErrors = true
            if(emailTextView.text!!.isEmpty())
            {
                emailTextView.error = "Login field must not be empty "
                noErrors=false
            }else{
                emailTextView.error = null
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(emailTextView.text.toString()).matches()){
                emailTextView.error="Please enter valid email"
                noErrors= false
            }else{
                emailTextView.error= null
            }
            if(passwordTextView.text!!.isEmpty())
            {
                passwordTextView.error = "Password field must not be empty "
                noErrors=false
            }else{
                passwordTextView.error = null
            }

            if(noErrors)
            {
                try {
                    auth.createUserWithEmailAndPassword(emailTextView.text.toString(),passwordTextView.text.toString())
                        .addOnCompleteListener(Activity()) { task ->
                            if (task.isSuccessful) {
                                findNavController().navigate(R.id.action_navigation_register_to_navigation_login)


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                                                            }

                            // ...
                        }

                }catch (e: IOException){
                    Toast.makeText(context,"Wrong login or password",Toast.LENGTH_LONG).show()
                }
            }
        }
        return root
    }
}