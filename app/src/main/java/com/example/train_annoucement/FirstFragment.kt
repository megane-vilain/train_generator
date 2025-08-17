package com.example.train_annoucement

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.train_annoucement.databinding.FragmentFirstBinding
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val worldSprinner: Spinner = view.findViewById(R.id.world_spinner)
        val expansionSpinner: Spinner = view.findViewById(R.id.expansion_spinner)
        val citySpinner: Spinner = view.findViewById(R.id.city_spinner)
        val aetheryteSpinner: Spinner = view.findViewById(R.id.aetheryte_spinner)

        val startTimeInput: EditText = view.findViewById(R.id.start_time_input)
        val nbMarks: EditText  = view.findViewById(R.id.nb_marks_input)
        val generateButton: Button = view.findViewById(R.id.button_generate)

        val worldAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.world_list,
            android.R.layout.simple_spinner_item
        )
        worldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        worldSprinner.adapter = worldAdapter
        
        val expansionAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.expansions_list,
            android.R.layout.simple_spinner_item
        )
        expansionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        expansionSpinner.adapter = expansionAdapter

        val cityAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.shadowbringer_list,
            android.R.layout.simple_spinner_item
        )
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = cityAdapter

        val aetheryteAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.fringe_list,
            android.R.layout.simple_spinner_item
        )
        aetheryteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        aetheryteSpinner.adapter = aetheryteAdapter

        expansionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                selectedView: View?,
                position: Int,
                id: Long
            ) {
                val selected = parent.getItemAtPosition(position).toString()
                nbMarks.setText("12")
                when (position) {
                    0 -> updateSpinner(citySpinner, R.array.stormblood_list)
                    1 -> updateSpinner(citySpinner, R.array.shadowbringer_list)
                    2 -> {
                        updateSpinner(citySpinner, R.array.endwalker_list)
                        nbMarks.setText("16")
                    }
                    3 -> updateSpinner(citySpinner, R.array.dawntrail_list)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>,
                selectedView: View?,
                position: Int,
                id: Long
            ) {

                val expansionSelection = expansionSpinner.selectedItemPosition
                when(expansionSelection) {
                    0 -> updateSpinner(aetheryteSpinner, Stb.entries[position].resourceId)
                    1 -> updateSpinner(aetheryteSpinner, Shb.entries[position].resourceId)
                    2 -> updateSpinner(aetheryteSpinner, Enw.entries[position].resourceId)
                    3 -> updateSpinner(aetheryteSpinner, Dawntrail.entries[position].resourceId)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        generateButton.setOnClickListener {
            val startTime = startTimeInput.text.toString().toLong()
            val now = Instant.now()
            val future = now.plus(startTime, ChronoUnit.MINUTES)
            val epochInSecond = future.epochSecond

            val timestamp = "<t:$epochInSecond:R>"
            val world = worldSprinner.selectedItem as String
            val expansion = expansionSpinner.selectedItem as String
            val nbMarks = nbMarks.text.toString().toInt()
            val city = citySpinner.selectedItem as String
            val aetheryte = aetheryteSpinner.selectedItem as String

            var ping = ""
            var nbMaxMarks = 12
            var speed = "Bullet"
            when(expansionSpinner.selectedItemPosition) {
                0 -> {
                    ping = "<@&1091904124301353110>"
                    speed = "Semi-Chill Let me pull the targets"
                }
                1 -> ping = "<@&934264458069561354>"
                2 -> {
                    ping = "<@&934264593470070784>"
                    nbMaxMarks = 16
                }
                3 -> ping = "<@&1255412015757918240>"
            }

            // Create the string with string interpolation
            val trainDetails = """
            $ping $world $expansion train $nbMarks/$nbMaxMarks.
            Speed: $speed
            Start time: $timestamp
            Type `/sea first rosenbloom en` in **game chat** to find current train zone
            Start: $city - $aetheryte
            """.trimIndent()

            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            // Create ClipData with the text to copy
            val clip = ClipData.newPlainText("trainDetails", trainDetails)

            // Set the text to the clipboard
            clipboard.setPrimaryClip(clip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateSpinner(spinner: Spinner, arrayResId: Int) {
        // Get the array from resources
        val options = resources.getStringArray(arrayResId)

        // Set the new adapter for spinner2 with the updated options
        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter2
    }

    // Define the enum inside the Fragment class
    enum class Stb(val resourceId: Int) {
        FRINGES(R.array.fringe_list),
        PEAKS(R.array.peaks_list),
        RUBY(R.array.ruby_list),
        YANXIA(R.array.yanxia_list),
        AZIM(R.array.azimme_list),
        LOCKS(R.array.loch_list);
    }

    enum class Shb(val resourceId: Int) {
        LAKELAND(R.array.lakeland_list),
        KHOLUSIA(R.array.kholusia_list),
        AHM(R.array.ahm_list),
        ILMEGH(R.array.ilmegh_list),
        RATIKA(R.array.ratika_list),
        TEMPEST(R.array.tempest_list);
    }

    enum class Enw(val resourceId: Int) {
        LABYRINTHOS(R.array.laby_list),
        THAVNAIR(R.array.thav_list),
        GARLEMALD(R.array.garlemald_list),
        MARE(R.array.mare_list),
        ELPIS(R.array.elpis_list),
        ULTIMA(R.array.ultima_list);
    }

    enum class Dawntrail(val resourceId: Int) {
        URQ(R.array.urq_list),
        KOZA(R.array.koza_list),
        YAK(R.array.yak_list),
        SHAAOLINI(R.array.shaaloani_list),
        HERITAGE(R.array.heritage_list),
        LM(R.array.living_meomry_list);
    }

}