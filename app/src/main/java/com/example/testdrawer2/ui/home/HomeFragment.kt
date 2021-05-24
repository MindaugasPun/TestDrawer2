package com.example.testdrawer2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testdrawer2.R
import com.example.testdrawer2.databinding.FragmentHomeBinding
import com.example.testdrawer2.reminder_data.Reminder
import com.example.testdrawer2.reminder_data.ReminderAdapter

lateinit var reminder: Reminder

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: HomeViewModel by viewModels { HomeViewModelFactory(requireContext()) }
        val binding = FragmentHomeBinding.inflate(inflater, container, false)


        val adapter = ReminderAdapter(ReminderAdapter.ReminderClickListener {
            val reminder = it
            val action = HomeFragmentDirections.actionNavHomeToNewReminder(reminder.reminderId)
            binding.root.findNavController().navigate(action)
        }, requireActivity(), null)
        binding.rcvHome.adapter = adapter
        binding.rcvHome.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //PASS REMINDERS LIST TO THE ADAPTER TO RENDER IMAGE
        viewModel.reminders.observe(viewLifecycleOwner, {
            adapter.reminders = it as MutableList<Reminder>?
        })
        //Update list
        viewModel.reminders.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        // Floating button - open add new reminder
        binding.fab.setOnClickListener { view ->
            Toast.makeText(context, "Create new reminder", Toast.LENGTH_LONG).show()
            view?.findNavController()?.navigate(R.id.action_nav_home_to_newReminder)
        }


        return binding.root
    }
}