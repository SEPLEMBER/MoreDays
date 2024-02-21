package de.wuapps.moredays.ui.journal


import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.imagepickerlibrary.ImagePicker
import com.app.imagepickerlibrary.model.PickerType
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import de.wuapps.moredays.MainActivity
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Journal
import de.wuapps.moredays.databinding.FragmentJournalBinding
import de.wuapps.moredays.utilities.DatePickerFragment
import de.wuapps.moredays.utilities.InjectorUtils
import de.wuapps.moredays.utilities.handleFab
import de.wuapps.moredays.utilities.isAtLeast11
import java.util.Calendar

@GlideModule
class JournalFragment : Fragment(), MenuProvider {

    private var _binding: FragmentJournalBinding? = null
    private val binding get() = _binding!!
    private val args: JournalFragmentArgs by navArgs()
    private val viewModel: JournalViewModel by viewModels {
        InjectorUtils.provideJournalViewModelFactory(requireActivity(), args.journal)
    }
    private lateinit var exampleAffirmations: Array<String>
    private var optionsMenuDelete: MenuItem? = null
    private var selectedMood = -1
    private lateinit var imagePickerMain: ImagePicker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val preferences =
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        val journalDBFormat = preferences.getString(
            getString(R.string.preferences_frequency_journal_entries_key),
            Journal.DATE_FORMAT
        ) ?: Journal.DATE_FORMAT
        viewModel.setDateFormatDb(journalDBFormat)
        if (journalDBFormat != Journal.DATE_FORMAT)
            viewModel.setDateFormatView(getString(R.string.date_time))
        else
            viewModel.setDateFormatView(getString(R.string.date_ymd_day))

        _binding = FragmentJournalBinding.inflate(inflater, container, false)
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                val mainActivity = requireActivity() as MainActivity
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                mainActivity.contentResolver.takePersistableUriPermission(uri, flag)
                mainActivity.setImagePickerCallback { uri -> onImagePick(uri) }
            }
        }
        //configureImagePicker()
        binding.imageButtonPickImage.setOnClickListener {

            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

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
        viewModel.journal.observe(viewLifecycleOwner) {
            updateUI()
        }
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
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }

//    private fun checkPermission(){
//        val permissions =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                arrayOf(
//                    Manifest.permission.READ_MEDIA_IMAGES
//                )
//            } else {
//                arrayOf(
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.CAMERA
//                )
//            }
//        permissions.forEach { perm -> checkPermission(perm) }
//    }
//    private fun checkPermission(permission: String) {
//        if (ContextCompat.checkSelfPermission(
//                requireContext(),
//                permission
//            ) == PackageManager.PERMISSION_GRANTED
//        )
//        else
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(permission),
//                    201
//                )
//
//        }
    private fun configureImagePicker() {
        val mainActivity = requireActivity() as MainActivity
        imagePickerMain = mainActivity.imagePicker

        imagePickerMain
            .title(getString(R.string.title_journal))
            .multipleSelection(enable = false)
            .showFolder(true)
            .cameraIcon(true)
            .doneIcon(true)
            .allowCropping(true)
            .compressImage(false)

        if (isAtLeast11()) {
            imagePickerMain.systemPicker(true)
        }
        mainActivity.setImagePickerCallback { uri -> onImagePick(uri) }
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
            .setItems(
                exampleAffirmations
            ) { _, which ->
                binding.editTextAffirmation.setText(exampleAffirmations[which])
            }
        builder.create().show()
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
        val alertDialog: AlertDialog.Builder =
        AlertDialog.Builder(requireNotNull(this.activity))
        alertDialog.setTitle(getString(R.string.title_delete))
        alertDialog.setMessage(getString(R.string.question_delete_journal))
        alertDialog.setPositiveButton(
            getString(R.string.yes)
        ) { _, _ ->
            viewModel.delete()
            view?.post {
                findNavController().popBackStack()
            }
        }
        alertDialog.setNegativeButton(
            getString(R.string.no)
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }


    override fun onMenuItemSelected(item: MenuItem): Boolean {
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

    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.help_calendar_delete_save_menu, menu)
        optionsMenuDelete = menu.findItem(R.id.menu_delete)
        optionsMenuDelete!!.isVisible = viewModel.hasDeleteOption
    }

    private fun showDatePicker() {
        val iniDate = viewModel.journal.value!!.getDateAsCalendar()
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
        (requireActivity() as MainActivity).unsetImagePickerCallback()
    }

    private fun onImagePick(uri: Uri) {
        if (uri.path != null) {
            viewModel.journal.value!!.imageFilename = uri.path!!
            setImage()
        }
    }

}