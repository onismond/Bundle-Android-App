package com.pietech.bundle.ui.home.profile.update_profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.pietech.bundle.R
import com.pietech.bundle.databinding.FragmentUpdateProfileBinding
import com.pietech.bundle.ui.home.HomeViewModel
import com.pietech.bundle.ui.home.HomeViewModelFactory
import com.pietech.bundle.util.*
import org.kodein.di.Kodein
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import org.kodein.di.KodeinAware

class UpdateProfileFragment : BaseFragment(), KodeinAware{

    override var bottomNavigationViewVisibility = View.INVISIBLE
    override val kodein by kodein()
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: FragmentUpdateProfileBinding
    private lateinit var schoolAdapter: SchoolAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateProfileBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        setupSchools()
        setup()
        binding.buttonUpdate.setOnClickListener {
            update()
        }
        return binding.root
    }

    private fun update() {
        Coroutines.io {
            val school = schoolAdapter.school
            var name: String
            var userName: String
            var contact: String
            activity?.runOnUiThread {
                name = binding.editTextName.text.toString()
                userName = binding.editTextUsername.text.toString()
                contact = binding.editTextContact.text.toString()
                Coroutines.io {
                    try {
                        Log.e("E", "$name , $userName , $school")
                        val response = viewModel.updateProfile(name, userName, contact, school)
                        if (response.isSuccessful!!) {
                            val user = response.user!!
                            user.token = response.token!!
                            viewModel.saveLoggedInUser(user)
                            activity?.runOnUiThread {
                                binding.root.snackbar("Successful")
//                                activity?.onBackPressed()
                            }
                        }
                    } catch (e: ApiException) {
                        activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
                    } catch (e: NoInternetException) {
                        activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
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
                binding.spinnerSchool.setSelection(user.school!!.toInt())
            }
        }
    }

    private fun setupSchools() {
        schoolAdapter = SchoolAdapter()
        binding.spinnerSchool.onItemSelectedListener = schoolAdapter
        viewModel.getSchools().observe(viewLifecycleOwner) { schools ->
            val schoolNames = mutableListOf<String>()
            for (school in schools)
                schoolNames.add(school.name!!)
            val schoolAdapter = ArrayAdapter(activity?.applicationContext!!, android.R.layout.simple_spinner_item, schoolNames)
            schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activity?.runOnUiThread {
                binding.spinnerSchool.adapter = schoolAdapter
            }
        }
    }
}



