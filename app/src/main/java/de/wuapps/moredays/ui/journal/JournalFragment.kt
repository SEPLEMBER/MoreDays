package de.wuapps.moredays.ui.journal


import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.opensooq.supernova.gligar.GligarPicker
import de.wuapps.moredays.R
import de.wuapps.moredays.databinding.FragmentJournalBinding
import de.wuapps.moredays.utilities.DatePickerFragment
import de.wuapps.moredays.utilities.InjectorUtils
import de.wuapps.moredays.utilities.handleFab
import java.util.*

@GlideModule
class JournalFragment : Fragment() {

    private var _binding: FragmentJournalBinding? = null
    private val binding get() = _binding!!
    private val args: JournalFragmentArgs by navArgs()
    private val viewModel: JournalViewModel by viewModels {
        InjectorUtils.provideJournalViewModelFactory(requireActivity(), args.journal)
    }
    private val REQUEST_IMAGE = 42
    private lateinit var exampleAffirmations: Array<String>
    private var optionsMenuDelete: MenuItem? = null
    private var selectedMood = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.setDateFormat(getString(R.string.date_ymd_day))
        _binding = FragmentJournalBinding.inflate(inflater, container, false)
        binding.imageButtonPickImage.setOnClickListener {
            GligarPicker().requestCode(REQUEST_IMAGE).withFragment(this).show()
        }
        handleFab(viewModel.hasFab)
        binding.imageButtonSuggestAffirmation.setOnClickListener { suggestAffirmation() }
        binding.imageButtonRemoveImage.setOnClickListener {
            binding.imageViewPictureOfTheDay.setImageDrawable(null)
            viewModel.journal.value!!.imageFilename = ""
        }
        exampleAffirmations = resources.getStringArray(R.array.sayings)

        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        viewModel.journal.observe(viewLifecycleOwner, {
            updateUI()
        })
        //mood bar
        binding.buttonMood1.setOnClickListener {
            setMood(1)
        }
        binding.buttonMood2.setOnClickListener {
            setMood(2)
        }
        binding.buttonMood3.setOnClickListener {
            setMood(3)
        }
        binding.buttonMood4.setOnClickListener {
            setMood(4)
        }
        binding.buttonMood5.setOnClickListener {
            setMood(5)
        }
        binding.buttonMoodNone.setOnClickListener {
            setMood(-1)
        }
        binding.journalViewModel = viewModel
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun unselectAllMoodButtons() {
        binding.buttonMood1.background = null
        binding.buttonMood2.background = null
        binding.buttonMood3.background = null
        binding.buttonMood4.background = null
        binding.buttonMood5.background = null
        binding.buttonMoodNone.background = null
    }

    private fun updateUI() {
        binding.textViewDate.text = viewModel.getCurrentDate()
        setImage()
        binding.editTextAffirmation.setText(viewModel.journal.value!!.affirmation)
        binding.editTextNote.setText(viewModel.journal.value!!.note)
        if (optionsMenuDelete != null) optionsMenuDelete!!.isVisible = viewModel.hasDeleteOption
        setMood(viewModel.journal.value!!.mood)
    }

    private fun setMood(mood: Int) {
        selectedMood = mood
        unselectAllMoodButtons()
        when (selectedMood) {
            1 -> binding.buttonMood1.setBackgroundResource(R.drawable.circle_in_secondary)
            2 -> binding.buttonMood2.setBackgroundResource(R.drawable.circle_in_secondary)
            3 -> binding.buttonMood3.setBackgroundResource(R.drawable.circle_in_secondary)
            4 -> binding.buttonMood4.setBackgroundResource(R.drawable.circle_in_secondary)
            5 -> binding.buttonMood5.setBackgroundResource(R.drawable.circle_in_secondary)
            -1 -> binding.buttonMoodNone.setBackgroundResource(R.drawable.circle_in_secondary)
        }
        viewModel.changeMood(selectedMood)
    }

    private fun suggestAffirmation() {
        val builder = AlertDialog.Builder(this.activity)
        builder.setTitle(R.string.title_affirmation)
            .setItems(exampleAffirmations,
                DialogInterface.OnClickListener { _, which ->
                    binding.editTextAffirmation.setText(exampleAffirmations.get(which))
                })
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            REQUEST_IMAGE -> {
                val imagesList =
                    data?.extras?.getStringArray(GligarPicker.IMAGES_RESULT)// return list of selected images paths.
                if (!imagesList.isNullOrEmpty()) {
                    viewModel.journal.value!!.imageFilename = imagesList.get(0)
                    setImage()
                }
            }
        }
    }

    private fun setImage() {
        if (viewModel.journal.value!!.imageFilename.isBlank()) {
            binding.imageViewPictureOfTheDay.setImageDrawable(null)
        } else {
            Glide.with(this)
                .load(viewModel.journal.value!!.imageFilename)
                .into(binding.imageViewPictureOfTheDay)
        }
    }

    private fun save() {
        viewModel.save()
        findNavController().popBackStack()
    }

    private fun delete() {
        viewModel.delete()
        view?.post {
            findNavController().popBackStack()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_help -> {
                showHelp()
                true
            }
            R.id.menu_delete -> {
                delete()
                true
            }
            R.id.menu_save -> {
                save()
                return true
            }
            R.id.menu_date -> {
                showDatePicker()
                return true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.help_calendar_delete_save_menu, menu)
        optionsMenuDelete = menu.findItem(R.id.menu_delete)
        optionsMenuDelete!!.isVisible = viewModel.hasDeleteOption
    }

    private fun showDatePicker() {
        var iniDate = viewModel.journal.value!!.getDateAsCalendar()
        val newFragment = DatePickerFragment(iniDate, this::handleDateSelected)
        newFragment.show(childFragmentManager, "datePicker")
    }

    private fun handleDateSelected(calendar: Calendar) {
        viewModel.changeDate(calendar)
    }

    private fun showHelp() {
        TapTargetSequence(requireActivity())
            .targets(
                TapTarget.forView(
                    binding.imageViewPictureOfTheDay,
                    "",
                    getString(R.string.help_journal_picture)
                )
                    .cancelable(false).transparentTarget(true).targetRadius(30),
                TapTarget.forView(
                    binding.editTextAffirmation,
                    "",
                    getString(R.string.help_journal_title)
                )
                    .cancelable(false).transparentTarget(true).targetRadius(30),
                TapTarget.forView(
                    binding.imageButtonSuggestAffirmation,
                    "",
                    getString(R.string.help_journal_button_affirmation)
                )
                    .cancelable(false).transparentTarget(true).targetRadius(30),
                TapTarget.forView(binding.buttonMood1, "", getString(R.string.mood_1))
                    .cancelable(false).transparentTarget(true).targetRadius(30),
                TapTarget.forView(binding.buttonMood2, "", getString(R.string.mood_2))
                    .cancelable(false).transparentTarget(true).targetRadius(30),
                TapTarget.forView(binding.buttonMood3, "", getString(R.string.mood_3))
                    .cancelable(false).transparentTarget(true).targetRadius(30),
                TapTarget.forView(binding.buttonMood4, "", getString(R.string.mood_4))
                    .cancelable(false).transparentTarget(true).targetRadius(30),
                TapTarget.forView(binding.buttonMood5, "", getString(R.string.mood_5))
                    .cancelable(false).transparentTarget(true).targetRadius(30)
            ).start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}