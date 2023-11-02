package android.example.myapplication002.screens.signInOrUp

import android.app.Activity.RESULT_OK
import android.example.myapplication002.MainActivity
import android.example.myapplication002.MainActivityViewModel
import android.example.myapplication002.R
import android.example.myapplication002.databinding.FragmentSignInBinding
import android.example.myapplication002.screens.database.ReservoirDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import timber.log.Timber
import android.content.Context
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager

class SignInFragment: Fragment()//, AdapterView.OnItemSelectedListener
{
    private lateinit var binding: FragmentSignInBinding
    private lateinit var viewModel:SignInViewModel
    //private var userName4VM=MutableLiveData<String>()
    //private var userId4VM=MutableLiveData<String>()
    private lateinit var userNames:Array<String>
    private var letIf=false
    private val auth=FirebaseAuth.getInstance()
    private lateinit var viewModelMA:MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelMA = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
        val binding:FragmentSignInBinding=DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in,container,false)
        binding.lifecycleOwner = this

        //viewModel creation setup
        val application= requireNotNull(this.activity).application
        val dataSource= ReservoirDatabase.getInstance(application).reservoirDatabaseDao
        val dataSourceUser= ReservoirDatabase.getInstance(application).userDatabaseDao
        val dataSourceMeasurement=ReservoirDatabase.getInstance(application).measurementDatabaseDao
        val viewModelFactory= SignInViewModelFactory(dataSource,dataSourceUser,dataSourceMeasurement,application)
        viewModel= ViewModelProvider(this,viewModelFactory).get(SignInViewModel::class.java)

        binding.signInViewModel=viewModel
        requireActivity().setTitle("Είσοδος")

        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
           // AuthUI.IdpConfig.FacebookBuilder().build(),
           // AuthUI.IdpConfig.TwitterBuilder().build())
        )

       // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setAlwaysShowSignInMethodScreen(true)
            .build()

        binding.passwordText.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val inputMethodManager =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                    true
                }

                else -> false
            }
        }

        binding.signInButton.setOnClickListener(){
            signInLauncher.launch(signInIntent)
        }

        binding.signInLocalButton.setOnClickListener {
            viewModel.userString=binding.userNameText.text.toString()
            viewModel.passwordString=binding.passwordText.text.toString()
            viewModel.loginLocal()
            viewModelMA.getUser()
        }

//        binding.signOutButton.setOnClickListener {
//            //signs out from firebase
//            signOutFirebase()
//            //signs out locally
//            viewModel.signOut()
//            viewModelMA.getUser()
//        }

   //     binding.skipButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_signInFragment_to_titleFragment))

//        binding.deleteUsersButton.setOnClickListener{
//             viewModel.deleteAll()
//             //findNavController().navigate(SignInFragmentDirections.actionSignInFragmentSelf())
//        }

//     //   Sets up spinner after all users have been retrieved from the database
//        viewModel.spinnerAction.observe(viewLifecycleOwner,Observer<Boolean>{
//            if(viewModel.letIf) {
//                 userNames=viewModel.userNames.toTypedArray()
//      //          Adding spinners for filters
//                val spinner: Spinner = binding.userSpinner
//                val adapter = ArrayAdapter<String>(
//                    requireContext(),
//                    android.R.layout.simple_spinner_item,
//                    userNames
//               )
//                   .also { arrayAdapter ->
//                         //Specify the layout to use when the list of choices appears
//                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                         //Apply the adapter to the spinner
//                        spinner.adapter = arrayAdapter
//                    }
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//
//                val selected = spinner.setSelection(0)
//                Timber.i("selected is $selected")
//                spinner.onItemSelectedListener = this
//                letIf=true
//
//            }
//        })
        return binding.root
    }

//    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        Timber.i("onItemSelected is called!")
//            if(letIf) {//originally false because position 0 is selected on setup and without the viewModel being initiated, it would cause a crash
//
//                    Timber.i("Position selected is $position")
//                    Timber.i("Corresponds to user: ${userNames[position]}")
//                    viewModel.signInViaSpinner(userNames[position])
//
//                    //Observer to sign in user with firebase when data is ready
//
//                viewModel.letSignIn.observe(viewLifecycleOwner,Observer<Boolean>{
//                        if (viewModel.letSignInIf) {
//                            val email = viewModel.email
//                            val password = viewModel.password
//                            if (email.isNotBlank() && password.isNotBlank()) {
//
//                            auth.signInWithEmailAndPassword(email, password)
//                                .addOnCompleteListener(requireActivity())
//                                { task ->
//                                    if (task.isSuccessful) {
//                                        //  Sign in success, update UI with the signed-in user's information
//                                        //  Log.d(TAG, "createUserWithEmail:success")
//                                        //  if (auth.currentUser != null) {
//                                        Timber.i("User $email signed in with firebase")
//                                        //Toast.makeText(requireContext(),"User with email: $email \n signed in",300).show()
//                                    } else {
//                                        Timber.i("Fire base sign in failed")
//                                        // If sign in fails, display a message to the user.
//                                        //Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                                        // updateUI(null)
//                                    }
//                                }
//                        }
//                            else if(viewModel.userNameIs=="User 0"){
//                                signOutFirebase()
//                            }
//
//                            else if(viewModel.userNameIs.isNotBlank() && password.isBlank()){
//                               // auth.signInWithCustomToken() maybe this is the method but I need to get the token upon user sign up in
//                               // Toast.makeText(requireContext(),"User will only sign in locally",300).show()
//                                Timber.i("User signed in locally ONLY")
//                              //  signOutFirebase()
//                            }
//                    }
//                    viewModelMA.getUser()
//                })
//            }
//
//    }
//
//    override fun onNothingSelected(parent: AdapterView<*>?) {
//        Timber.i("Spinner: Nothing has been selected")
//    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
        Timber.i("Registered for activity")
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        Timber.i("On sign in result accessed")
        //val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
        // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            val userphone=user!!.phoneNumber?:""
            val userId= user!!.uid
            val userName=user.displayName ?:userphone
            val userEmail=user.email?:""
            Timber.i("Username is $userName and user id is $userId and phone is $userphone")
            Toast.makeText(requireContext(),"Επιτυχής σύνδεση",Toast.LENGTH_SHORT).show()
            viewModel.signInUser(userId,userName!!,userEmail,userphone)
            //Thread.sleep(100)//seems to fix issue where viewModelMA functioned returned user before this fragment's viewModel ran
            viewModel.letSignIn.observe(viewLifecycleOwner,Observer<Boolean>{
            viewModelMA.getUser()
             })
            //no need to navigate manually, main activity will trigger the change automatically
            //findNavController().navigate(R.id.action_signInFragment_to_titleFragment)
        }
        else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Timber.i("Sign in failed")
            Toast.makeText(requireContext(),"Η σύνδεση απέτυχε",Toast.LENGTH_SHORT).show()
        }
    }

    private fun signOutFirebase()
    {
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnCompleteListener {
            // ...
            // Toast.makeText(requireContext(),"SignIn: User has signed out from firebase",Toast.LENGTH_SHORT).show(
            Timber.i("User has signed out")
                viewModelMA.getUser()
            }
    }
}