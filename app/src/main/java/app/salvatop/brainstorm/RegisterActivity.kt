package app.salvatop.brainstorm

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.transition.Explode
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.salvatop.brainstorm.model.Idea
import app.salvatop.brainstorm.model.Profile
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class RegisterActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var rEmail: TextInputLayout
    private lateinit var rPassword: TextInputLayout
    private lateinit var rUsername: TextInputLayout
    private lateinit  var register: MaterialButton
    private lateinit  var done: MaterialButton
    private lateinit  var goToLogin: MaterialButton
    private lateinit  var messageLbl: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        addBogoDatas()

        firebaseAuth = FirebaseAuth.getInstance()

        rEmail = findViewById(R.id.editTextTextRegisterEmailAddress)
        rPassword = findViewById(R.id.editTextTextRegisterPassword)
        rUsername = findViewById(R.id.editTextTextUsername)
        messageLbl = findViewById(R.id.messageLabelRegister)
        register = findViewById(R.id.buttonRegister)
        done = findViewById(R.id.buttonRegisterDone)
        goToLogin = findViewById(R.id.buttonGoToLogin)

        register.setOnClickListener {
            val email = rEmail.editText?.text.toString()
            val pass = rPassword.editText?.text.toString()
            if (validateEmailPassword()) {
                createAccount(firebaseAuth, email, pass)
            }
        }
        done.setOnClickListener {
            if (validateUsername()) {
                setupProfile(firebaseAuth, rUsername.editText?.text.toString())
                addProfile()
            }
        }
        goToLogin.setOnClickListener {
            // set an exit transition
            window.exitTransition = Explode()
            val mainActivity = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(mainActivity)
        }
    }

    private fun validateEmailPassword(): Boolean {
        var valid = true
        val email = rEmail.editText?.text.toString()
        if (TextUtils.isEmpty(email)) {
            rEmail.error = "Required."
            valid = false
        } else {
            rEmail.error = null
        }
        val password = rPassword.editText?.text.toString()
        if (TextUtils.isEmpty(password)) {
            rPassword.error = "Required."
            valid = false
        } else {
            rPassword.error = null
        }
        return valid
    }

    private fun validateUsername(): Boolean {
        var valid = true
        val username = rUsername.editText?.text.toString()
        if (TextUtils.isEmpty(username)) {
            rUsername.error = "Required."
            valid = false
        } else {
            rUsername.error = null
        }
        return valid
    }

    private fun setupProfile(mAuth: FirebaseAuth, displayName: String?) {
        val user = mAuth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(Uri.parse(""))
                .build()
        if (BuildConfig.DEBUG && user == null) {
            print("Assertion failed")
        }
        user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        Timber.i("User profile updated.")
                    }
                }
    }

    @SuppressLint("LogNotTimber")
    private fun createAccount(auth: FirebaseAuth, email: String, password: String) {
        Log.d("ACCOUNT CREATION", "createAccount:$email")
        //register user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this@RegisterActivity) { task: Task<AuthResult?> ->
                   Timber.d("New user registration: %s", task.isSuccessful)
                    hideAndDisplayUIElements()
                    if (!task.isSuccessful) {
                        print("Authentication failed. " + task.exception)
                    }
                }
    }

    private fun addBogoDatas(){
        val database = FirebaseDatabase.getInstance()
        val username = "Mario"
        val username2 = "Magda"
        val followed = HashMap<String, String>()
        followed["Magda"] = "Magda"
        val followed2 = HashMap<String, String>()
        followed2["Mario"] = "Mario"
        val following = HashMap<String, String>()
        following["Magda"] = "Magda"
        val following2 = HashMap<String, String>()
        following2["Mario"] = "Mario"
        val teams = ArrayList<String>()
        teams.add("")
        val forks = HashMap<String, String>()
        forks["none"] = "none"
        val bookmarks = HashMap<String, Idea>()
        bookmarks["none"] = Idea(username, "context", "content", "none", "false", forks)
        val bookmarks2 = HashMap<String, Idea>()
        bookmarks["none"] = Idea(username2, "context", "content", "none", "false", forks)

        val ideas = HashMap<String, Idea>()
        ideas["Increase productivity"] = Idea(username, "Office management", "Find ne strategies to increase productivity while working remotely", "Increase productivity", "true", forks)
        ideas["Christmas dinner"] = Idea(username, "Office management", "Find a nice place where to reunion the whole team for a dinner on Christmas", "Christmas dinner", "true", forks)
        val profile = Profile("b1a0d174-d5be-11ea-87d0-0242ac130003","New York, NY","slow but safe","Team leader", username, followed, following, teams, ideas, bookmarks)

        val ideas2 = HashMap<String, Idea>()
        ideas2["New hire process"] = Idea(username2, "Human resources", "Redesign the hire process to include screening for remote workers", "New hire process", "true", forks)
        ideas2["Exit interview process"] = Idea(username2, "Human resources", "Gather input and contents how to format the a survey to add on exit interview", "Exit interview process", "true", forks)
        val profile2 = Profile("c1845f16-d5be-11ea-87d0-0242ac130003","Toronto, ON","think fast, act fast","IT Manager", username2, followed2, following2, teams, ideas2, bookmarks2)

        val myRef = database.getReference("users")

        myRef.child(username).setValue(profile)
        myRef.child(username2).setValue(profile2)
    }

    private fun addProfile() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users")
        val username = rUsername.editText?.text.toString()
        //val username = firebaseAuth.currentUser.toString()
        val userUid = firebaseAuth.currentUser!!.uid
        val followed = HashMap<String, String>()
        followed["none"] = "none"
        val following = HashMap<String, String>()
        following["none"] = "none"
        val teams = ArrayList<String>()
        teams.add("")
        val bookmarks = HashMap<String, Idea>()
        val ideas = HashMap<String, Idea>()
        val forks = HashMap<String, String>()
        forks["none"] = "none"

        GlobalScope.launch {
            delay(1000)

            bookmarks["none"] = Idea(username, "context", "content", "none", "false", forks)

            ideas["none"] = Idea(username, "context", "content", "none", "false", forks)

            val profile = Profile(userUid,"add your city","add your motto","add your occupation", username, followed, following, teams, ideas, bookmarks)

            myRef.child(username).setValue(profile)
            sendEmailVerification()
        }
    }

    private fun hideAndDisplayUIElements() {
        goToLogin.alpha = 0f
        goToLogin.isEnabled = false
        rEmail.alpha = 0f
        rEmail.isEnabled = false
        rEmail.isFocusable = false
        rPassword.alpha = 0f
        rPassword.isEnabled = false
        rPassword.isFocusable = false
        done.alpha = 1f
        rUsername.alpha = 1f
        messageLbl.alpha = 1f
        register.alpha = 0f
        register.isEnabled = false
    }

    private fun sendEmailVerification() {
        // Disable buttons
        done.alpha = 0f
        rUsername.alpha = 0f
        messageLbl.alpha = 0f
        val user = firebaseAuth.currentUser!!
        user.sendEmailVerification()
                .addOnCompleteListener(this) { task: Task<Void?> ->
                    goToLogin.alpha = 1f
                    goToLogin.isEnabled = true
                    if (task.isSuccessful) {
                        Toast.makeText(this@RegisterActivity,
                                "Verification email sent to " + user.email,
                                Toast.LENGTH_SHORT).show()
                    } else {
                        Timber.e(task.exception, "sendEmailVerification")
                        Toast.makeText(this@RegisterActivity,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    fun setupProfile(mAuth: FirebaseAuth, displayName: String?, photoUrl: String?) {
        val user = mAuth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(Uri.parse(photoUrl))
                .build()
        user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        Timber.i("User profile updated.")
                    }
                }
    }

    fun removeUser(auth: FirebaseAuth) {
        val user = auth.currentUser!!
        user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show()
                        this.startActivity(Intent(this, RegisterActivity::class.java))
                    } else {
                        Toast.makeText(this, "Failed to delete your account!", Toast.LENGTH_SHORT).show()
                    }
                }
    }
}

