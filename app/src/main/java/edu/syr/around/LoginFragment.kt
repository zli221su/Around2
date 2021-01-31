package edu.syr.around


import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : androidx.fragment.app.Fragment() {
    // listener is the LoginActivity
    private var listener: OnFragmentInteractionListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.background = ColorDrawable(0xFFA500)
// button click event handler!
        login_button.setOnClickListener {
            performLogin()
        }
// switch to sign up or reset password!
        back_to_register.setOnClickListener{
            val email = email_login.text.toString()
            val password = password_login.text.toString()
// LoginActivity implements onSignUpRoutine()
            listener!!.onSignUpRoutine(email, password)
        }
    }
    private fun performLogin() {
        val email = email_login.text.toString()
        val password = password_login.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please fill out email/pw.", Toast.LENGTH_SHORT).show()
            return
        }
// Firebase Authentication using email and password!
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Login", "Successfully logged in: ${it.result!!.user!!.uid}")
// launch the Main activity, clear back stack!
// not going back to login activity when back button pressed
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to log in: ${it.message}"
                    , Toast.LENGTH_SHORT).show()
            }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener")
        }
    }
    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    interface OnFragmentInteractionListener {
        fun onSignUpRoutine(email: String, passwd: String)
    }
}