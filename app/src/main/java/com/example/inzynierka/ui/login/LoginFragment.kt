package com.example.inzynierka.ui.login

import android.app.Activity
import android.app.ActivityOptions
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.inzynierka.MainActivity
import com.example.inzynierka.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginFragment: Fragment(){
    private lateinit var auth: FirebaseAuth
    private var navController: NavController? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_login, container, false)
        navController = findNavController()
        auth = FirebaseAuth.getInstance()
        val btn_login: Button = root.findViewById(R.id.btn_login)
        val btn_register: Button = root.findViewById(R.id.btn_goto_register)
        val loginTextView: TextInputEditText = root.findViewById(R.id.login_text)
        val passwordTextView: TextInputEditText = root.findViewById(R.id.password_text)
        btn_login.setOnClickListener{
            var noErrors = true
            if(loginTextView.text!!.isEmpty())
            {
                loginTextView.error = "Login field must not be empty "
                noErrors=false
            }else{
                loginTextView.error = null
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
                auth.signInWithEmailAndPassword(loginTextView.text.toString(),passwordTextView.text.toString())
                    .addOnCompleteListener(Activity()) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            updateUI(user)



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ContentValues.TAG, "loginUserWithEmail:failure", task.exception)
                            Toast.makeText(context, "Login failed.",
                                Toast.LENGTH_SHORT).show()
                        }

                    }

            }

        }
        btn_register.setOnClickListener{

            navController?.navigate(R.id.action_navigation_login_to_navigation_register)

        }


        return root
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user!=null){
            val intent = Intent(context, MainActivity::class.java)
            val options =
                ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, R.anim.fade_out)
            startActivity(intent,options.toBundle())
            activity?.finish()
        }else{
            Log.d(ContentValues.TAG, "loginUserWithEmail:failure")
            Toast.makeText(context, "Login failed.",
                Toast.LENGTH_SHORT).show()

        }


    }
}