package com.selcukileri.wakeup.view

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.room.Room
import com.selcukileri.wakeup.databinding.FragmentSettingsBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.prefs.Preferences

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private var selectedOption: Double? = null
    private val compositDisposable = CompositeDisposable()
    private lateinit var sharedPreferencesSettings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        sharedPreferencesSettings = requireActivity().getSharedPreferences("SettingsPrefs", MODE_PRIVATE)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val selectedDistance = sharedPreferencesSettings.getString("selectedDistance", "")
        val selectedAlertType = sharedPreferencesSettings.getString("selectedAlertType", "")
        if (!selectedDistance.isNullOrEmpty()) {
            binding.selectedDistance.text = "Seçtiğiniz uzaklık: $selectedDistance metre"
        }

        if (!selectedAlertType.isNullOrEmpty()) {
            binding.selectedAlertType.text = "Seçtiğiniz alarm tipi: $selectedAlertType"
        }
        binding.destinationDistance.setOnClickListener {
            showCustomAlertDialog2()
        }
        binding.alertType.setOnClickListener {
            showCustomAlertDialog()
        }
        binding.buttonBacktoMenu.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToFragmentMain()
            Navigation.findNavController(it).navigate(action)
        }
    }

    private fun showCustomAlertDialog2() {
        val options = arrayOf("500", "750", "1000")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Kaç metre kala uyarılmak istersiniz")
        builder.setItems(options) { _, which ->
            val selectedDistance = options[which]
            binding.selectedDistance.text = "Seçtiğiniz uzaklık: $selectedDistance metre"

            val editor = sharedPreferencesSettings.edit()
            editor.putString("selectedDistance",selectedDistance)
            editor.apply()
        }
        builder.setCancelable(false)
        builder.show()

    }

    private fun showCustomAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Alarmla mı uyarılmak istersiniz titreşimle mi?")
        val options = arrayOf("Alarm", "Titreşim", "Alarm ve Titreşim")
        var selectedOptionIndex = -1
        builder.setSingleChoiceItems(options, -1) { dialog, which ->
            selectedOptionIndex = which
        }
        builder.setPositiveButton("Tamam") { _, _ ->
            if (selectedOptionIndex != -1) {
                val selectedAlertType = options[selectedOptionIndex]
                binding.selectedAlertType.text = "Seçtiğiniz alarm tipi: $selectedAlertType"

                val editor = sharedPreferencesSettings.edit()
                editor.putString("selectedAlertType",selectedAlertType)
                editor.apply()
            }

            //useless bir adet seçenek
            builder.setNegativeButton("İptal") { _, _ ->
                val action = SettingsFragmentDirections.actionSettingsFragmentToFragmentMain()
                Navigation.findNavController(requireView()).navigate(action)
            }
        }


        builder.setCancelable(false)
        builder.show()
    }

}


