package android.example.myapplication002.screens.signInOrUp

import android.content.Context
import android.example.myapplication002.MainActivityViewModel
import android.example.myapplication002.R
import android.example.myapplication002.databinding.FragmentSignUpBinding
import android.example.myapplication002.screens.database.ReservoirDatabase
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import timber.log.Timber
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth.AuthStateListener

class SignUpFragment: Fragment(){
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel:SignInViewModel
    private var auth:FirebaseAuth=FirebaseAuth.getInstance()
    private lateinit var viewModelMA:MainActivityViewModel


     override fun onStart() {
       super.onStart()
         requireActivity().setTitle("Εγγραφή")
         // Check if user is signed in (non-null) and update UI accordingly.
         // Not needed anymore because if a user is signed in they will automatically be navigated to the title screen
         val currentUser = auth.currentUser
         if (currentUser != null) {
             Toast.makeText(
                 requireContext(),
                 "Current firebase user name is: ${currentUser.displayName}" +
                         "while user is: $currentUser",
                 Toast.LENGTH_SHORT
             ).show()
             Timber.i("Current firebase user is ${currentUser.displayName}")
         }
         else{
             //Toast.makeText(requireContext(),"No user currently signed in",Toast.LENGTH_SHORT).show()
         }


         val authStateListener = AuthStateListener { firebaseAuth ->
             val user = firebaseAuth.currentUser
             val displayName = if (user != null) {
                 // User is signed in
                 user.displayName
                 // Do something with the user data
             } else {
                 // User is signed out
                 // Redirect to sign-in screen or perform any other action
                 "empty"
             }
             Toast.makeText(requireContext(), "Auth state changed",Toast.LENGTH_SHORT).show()
             Timber.i("Auth state has been changed")
         }
         auth.addAuthStateListener {authStateListener}
     }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelMA = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
        val binding:FragmentSignUpBinding=DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up,container,false)

        binding.lifecycleOwner = this

        val application= requireNotNull(this.activity).application
        val dataSource= ReservoirDatabase.getInstance(application).reservoirDatabaseDao
        val dataSourceUser= ReservoirDatabase.getInstance(application).userDatabaseDao
        val viewModelFactory= SignUpViewModelFactory(dataSource,dataSourceUser,application)
        val viewModel= ViewModelProvider(this,viewModelFactory).get(SignUpViewModel::class.java)

        binding.signUpViewModel=viewModel

        binding.userNameText.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.userNameText.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                   // val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    //inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                  //  true
                    binding.emailText.imeOptions = EditorInfo.IME_ACTION_DONE
                    binding.emailText.requestFocus()
                }
                else -> false
            }
        }

        binding.emailText.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                    true
                }
                else -> false
            }
        }

        binding.passwordText.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                    true
                }
                else -> false
            }
        }

        binding.passwordConfirmText.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                    true
                }
                else -> false
            }
        }

        //var canPress=false//not persistent, should be live data? or stored in viewModel?
        //logic that determines whether to create user or not

        binding.createUserButton.setOnClickListener {
            if (binding.userNameText.text.isNotEmpty() && binding.emailText.text.isNotEmpty()
                && binding.passwordText.text.isNotEmpty() && binding.passwordConfirmText.text.isNotEmpty())
            {
               Timber.i("password smth is: ${binding.passwordText.text}")

                 if(binding.userNameText.length()>3){
                   if(binding.emailText.text.isValidEmail()){
                        if(binding.passwordText.text.length>5) {
                           if (!viewModel.listofUsernames.contains(binding.userNameText.text.toString())) {
                               if (binding.passwordText.text.toString() == binding.passwordConfirmText.text.toString()) {
                                   Timber.i("User will now be created")
                                   val email=binding.emailText.text.toString()
                                   val password=binding.passwordText.text.toString()
                                   val username= binding.userNameText.text.toString()

                                   signOut()
                                   auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(requireActivity())
                                   { task ->
                                        if (task.isSuccessful){
                                       //  Sign in success, update UI with the signed-in user's information
                                       //  Log.d(TAG, "createUserWithEmail:success")
                                       //  if (auth.currentUser != null) {
                                         val user: FirebaseUser? = auth.currentUser
                                           viewModel.firebaseID = user!!.uid
//                                           Toast.makeText(
//                                               requireContext(),
//                                               "Ο χρήστης δημιουργήθηκε",
//                                               Toast.LENGTH_SHORT,
//                                           ).show()
                                       //}
                                            viewModel.password =password
                                            viewModel.email = email
                                            viewModel.username = username
                                            viewModel.storeUserInfo()
                                            viewModel.updateUI.observe(viewLifecycleOwner,Observer<Boolean>{
                                                viewModelMA.getUser()
                                                Toast.makeText(requireContext(),"Επιτυχής δημιουργία λογαριασμού",Toast.LENGTH_SHORT).show()
                                            })

                                           //Navigate upon success
                                           //findNavController(it).navigate(R.id.action_signUpFragment_to_titleFragment) //no longer needed because the currentDestination check in
                                           //MainActivity automatically changes destination upon login
                                   }
                                        else {
                                           // If sign in fails, display a message to the user.
                                           //Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                           Toast.makeText(
                                               requireContext(),
                                               "Η αυθεντικοποίηση απέτυχε",
                                               Toast.LENGTH_SHORT,
                                           ).show()
                                          // updateUI(null)
                                       }
                                   }
                               } else {
                                   Toast.makeText(
                                       requireContext(),
                                       "Ο κωδικός επαλήθευσης διαφέρει",
                                       Toast.LENGTH_SHORT
                                   ).show()
                               }
                           }
                            else{
                                Toast.makeText(context,"Υπάρχει ήδη χρήστης με αυτό το όνομα. Επιλέξτε άλλο.",Toast.LENGTH_SHORT).show()
                           }
                        }
                        else{
                            Toast.makeText(context,"Ο κωδικός πρέπει να έχει τουλάχιστον 6 χαρακτήρες",Toast.LENGTH_SHORT).show()
                        }

                    Timber.i("Email address is valid")
                   }
                    else{
                       Toast.makeText(context,"Μη έγκυρο e-mail",Toast.LENGTH_SHORT).show()
                       Timber.i("Can't create user; at least one parameter is unmet")
                     }
                 }
                 else{
                     Toast.makeText(context,"Το όνομα χρήστη πρέπει να έχει τουλάχιστον 4 χαρακτήρες",Toast.LENGTH_SHORT).show()
                 }
              }
            else{
                Toast.makeText(context,"Τουλάχιστον ένα πεδίο είναι κενό",Toast.LENGTH_SHORT).show()
                Timber.i("At least one EditText is empty")
            }
        }
       // binding.nextPageButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_signUpFragment_to_titleFragment))
        return binding.root
    }

    private fun signOut()
    {
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnCompleteListener {
                // ...
            Timber.i("Αποσυνδεθήκατε")
            }
    }
    private fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}