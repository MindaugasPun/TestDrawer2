package com.example.testdrawer2.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testdrawer2.databinding.FragmentCalendarBinding
import com.example.testdrawer2.reminder_data.Reminder
import com.example.testdrawer2.reminder_data.ReminderAdapter

class CalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: CalendarViewModel by viewModels { CalendarViewModelFactory(requireContext()) }
        val binding = FragmentCalendarBinding.inflate(inflater, container, false)

        val adapter = ReminderAdapter(ReminderAdapter.ReminderClickListener {
            val reminder = it
            val action = CalendarFragmentDirections.actionNavCalendarToNewReminder(reminder.reminderId)
            binding.root.findNavController().navigate(action)
        }, requireActivity(), null)
        binding.rcvHome.adapter = adapter
        binding.rcvHome.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.lifecycleOwner = viewLifecycleOwner

        //PASS REMINDERS LIST TO THE ADAPTER TO RENDER IMAGE
        viewModel.reminders.observe(viewLifecycleOwner, {
            adapter.reminders = it as MutableList<Reminder>?
        })
        //Update list
        viewModel.reminders.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        //SORT
        binding.btnSort.setOnClickListener {
            viewModel.filter(
                binding.edtSortYear1.text.toString().toIntOrNull(),
                binding.edtSortMonth1.text.toString().toIntOrNull(),
                binding.edtSortDay1.text.toString().toIntOrNull(),
                binding.edtSortYear2.text.toString().toIntOrNull(),
                binding.edtSortMonth2.text.toString().toIntOrNull(),
                binding.edtSortDay2.text.toString().toIntOrNull(),
            )
        }

        return binding.root
    }
}