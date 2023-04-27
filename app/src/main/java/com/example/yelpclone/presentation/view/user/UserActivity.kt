package com.example.yelpclone.presentation.view.user

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yelpclone.R
import com.example.yelpclone.core.events.SearchEvent
import com.example.yelpclone.databinding.ActivityUserBinding
import com.example.yelpclone.presentation.view.adapter.UserAdapter
import com.example.yelpclone.presentation.view.main.MainActivity
import com.example.yelpclone.presentation.viewmodel.user.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserActivity : AppCompatActivity() {

    private var _binding: ActivityUserBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter

    companion object {
        private const val USER = "USER_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        determineUserState()
        menuItemSelection()
    }

    private fun initRecyclerView() {
        binding.rvUserList.apply {
            hasFixedSize()
            userAdapter =
                UserAdapter(this@UserActivity, object : UserAdapter.OnClickListener {
                    override fun onItemClick(position: Int) {
                        Toast.makeText(
                            this@UserActivity,
                            "Item $position clicked!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            adapter = userAdapter
            layoutManager = LinearLayoutManager(this@UserActivity)
        }.also {
            it.smoothScrollToPosition(0)
        }
    }

    private fun determineUserState() {
        binding.apply {
            lifecycleScope.launch {
                /*
                Flow isn't lifecycle aware so we must flow with lifecycle to handle resource
                consumption.
                */
                userViewModel.userState.flowWithLifecycle(lifecycle).collect { response ->

                    when (response) {
                        is SearchEvent.Failure -> {
                            Snackbar.make(
                                binding.root, "Error when fetching Data!", Snackbar.LENGTH_LONG
                            ).show()
                            pbUser.visibility = View.GONE
                            Log.d(USER, "Failed to update UI with data: ${response.errorMessage}")
                        }

                        is SearchEvent.Loading -> {
                            pbUser.visibility = View.VISIBLE
                            Log.d(USER, "Loading user activity...")
                        }

                        is SearchEvent.Success -> {
                            if (response.results!!.isEmpty()) {
                                Snackbar.make(
                                    binding.root, "Results are Empty!",
                                    Snackbar.LENGTH_LONG
                                ).show()
                                pbUser.visibility = View.GONE
                                noResults.visibility = View.VISIBLE
                                Log.d(
                                    USER, "Failed to update UI with data: ${response.errorMessage}"
                                )

                            } else {
                                response.results.let {
                                    userAdapter.differ.submitList(it.toList())
                                }
                                Snackbar.make(
                                    binding.root,
                                    "Successfully fetched Data!",
                                    Snackbar.LENGTH_LONG
                                ).show()
                                pbUser.visibility = View.GONE
                                Log.d(
                                    USER, "Successfully updated UI with data: ${response.results}"
                                )
                            }
                        }

                        is SearchEvent.Idle -> {
                            Log.d(USER, "Idle State currently...")
                        }
                    }
                }
            }
        }
    }

    private fun menuItemSelection() {
        binding.apply {
            topUserAppBar.setNavigationOnClickListener {
                materialDialog(
                    this@UserActivity,
                    "Menu!".uppercase(),
                    "This button would normally display a menu of other options!" +
                            " Click ok to go to Restaurant list, otherwise click cancel to exit."
                )
            }.also {
                topUserAppBar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.user -> {
                            materialDialog(
                                this@UserActivity,
                                "Navigation!".uppercase(),
                                "To go back to restaurants, click OK. " +
                                        "Otherwise, click cancel to exit."
                            )
                            true
                        }

                        else -> {
                            false
                        }
                    }
                }
            }
        }
    }


    private fun materialDialog(
        context: Context,
        title: String,
        message: String
    ) = object : MaterialAlertDialogBuilder(this) {
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("OK") { _, _ ->
                val intent = Intent(this@UserActivity, MainActivity::class.java)
                startActivity(intent)
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}