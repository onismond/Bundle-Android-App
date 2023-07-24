package com.bundlegh.bundle.ui.home.profile.update_profile


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bundlegh.bundle.databinding.FragmentUpdateProfileBinding
import com.bundlegh.bundle.ui.home.HomeActivity
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class UpdateProfileFragment : BaseFragment(), KodeinAware{

    override var bottomNavigationViewVisibility = View.INVISIBLE
    override val kodein by kodein()
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: FragmentUpdateProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateProfileBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        setup()
        binding.buttonUpdate.setOnClickListener {
            update()
        }
        return binding.root
    }

    private fun update() {
        Coroutines.io {
            var name: String
            var userName: String
            var contact: String
            activity?.runOnUiThread {
                name = binding.editTextName.text.toString()
                userName = binding.editTextUsername.text.toString()
                contact = binding.editTextContact.text.toString()
                (activity as HomeActivity).showProgress()
                Coroutines.io {
                    try {
                        Log.e("E", "$name , $userName")
                        val response = viewModel.updateProfile(name, userName, contact)
                        if (response.isSuccessful!!) {
                            val user = response.user!!
                            user.token = response.token!!
                            viewModel.saveLoggedInUser(user)
                            activity?.runOnUiThread {
                                binding.root.snackbar("Successful")
                                (activity as HomeActivity).hideProgress()
//                                activity?.onBackPressed()
                            }
                        }
                        activity?.runOnUiThread { (activity as HomeActivity).hideProgress() }
                    } catch (e: ApiException) {
                        activity?.runOnUiThread {
                            (activity as HomeActivity).hideProgress()
                            binding.root.snackbar(e.message!!)
                        }
                    } catch (e: NoInternetException) {
                        activity?.runOnUiThread {
                            (activity as HomeActivity).hideProgress()
                            binding.root.snackbar(e.message!!)
                        }
                    } catch (e: Exception) {
                        activity?.runOnUiThread {
                            (activity as HomeActivity).hideProgress()
                            binding.root.snackbar("Sorry, an error occurred")
                        }
                    }

                }
            }

        }
    }

    private fun setup() {
        Coroutines.io {
            val user = viewModel.getLoggedInUser()
            activity?.runOnUiThread {
                binding.editTextName.setText(user.name ?: "")
                binding.editTextUsername.setText(user.username ?: "")
                binding.editTextContact.setText(user.contact ?: "")
            }
        }
    }
//
//    private fun setupSchools() {
//        schoolAdapter = SchoolAdapter()
//        binding.spinnerSchool.onItemSelectedListener = schoolAdapter
//        viewModel.getSchools().observe(viewLifecycleOwner) { schools ->
//            val schoolNames = mutableListOf<String>()
//            for (school in schools)
//                schoolNames.add(school.name!!)
//            val schoolAdapter = ArrayAdapter(activity?.applicationContext!!, android.R.layout.simple_spinner_item, schoolNames)
//            schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            activity?.runOnUiThread {
//                binding.spinnerSchool.adapter = schoolAdapter
//            }
////            spinner_school.setSelection()
//        }
//    }
}



