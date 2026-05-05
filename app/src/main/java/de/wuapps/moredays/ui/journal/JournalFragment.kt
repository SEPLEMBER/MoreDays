package de.wuapps.moredays.ui.journal


import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.yalantis.ucrop.UCrop
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Journal
import de.wuapps.moredays.databinding.FragmentJournalBinding
import de.wuapps.moredays.utilities.DatePickerFragment
import de.wuapps.moredays.utilities.InjectorUtils
import de.wuapps.moredays.utilities.getColorThemeRes
import de.wuapps.moredays.utilities.handleFab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
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
    private lateinit var journalPrompts: Array<String>
    private var optionsMenuDelete: MenuItem? = null
    private var selectedMood = -1
    private var cameraTempFile: File? = null
    private var pendingImportedImageFile: File? = null
    private var legacyMigrationInProgressFor: String? = null
    private val openImageDocument = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { startCrop(it) }
    }
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val cameraUri = cameraTempFile?.let { fileToUri(it) }
        if (success && cameraUri != null) {
            startCrop(cameraUri)
        } else {
            cleanupPendingSelectionFiles()
        }
    }
    private val cropImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            finalizeCroppedImage()
        } else {
            pendingImportedImageFile?.delete()
            pendingImportedImageFile = null
        }
        cleanupCameraTempFile()
    }

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
        binding.imageButtonPickImage.setOnClickListener {
            showImageSourceChooser()
        }
        handleFab(viewModel.hasFab)
        binding.imageButtonSuggestAffirmation.setOnClickListener { suggestAffirmation() }
        binding.imageButtonSuggestJournalPrompt.setOnClickListener { suggestJournalPrompt() }
        binding.imageButtonRemoveImage.setOnClickListener {
            deleteImportedImage(viewModel.journal.value!!.imageFilename)
            binding.imageViewPictureOfTheDay.setImageDrawable(null)
            viewModel.journal.value!!.imageFilename = ""
        }
        exampleAffirmations = resources.getStringArray(R.array.sayings)
        journalPrompts = resources.getStringArray(R.array.journal_prompts)

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
    override fun onPause() {
        super.onPause()
        viewModel.save()
    }

    private fun showImageSourceChooser() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.title_select_image_source)
            .setItems(
                arrayOf(
                    getString(R.string.label_select_image_gallery),
                    getString(R.string.label_select_image_camera)
                )
            ) { _, which ->
                when (which) {
                    0 -> openImageDocument.launch(arrayOf("image/*"))
                    1 -> openCamera()
                }
            }
            .show()
    }

    private fun openCamera() {
        val tempFile = createTempSelectionFile("camera_${viewModel.journal.value!!.date}")
        cameraTempFile = tempFile
        takePicture.launch(fileToUri(tempFile))
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
        migrateLegacyImageIfNeeded()
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

    private fun suggestJournalPrompt() {
        val builder = AlertDialog.Builder(this.activity)
        builder.setTitle(R.string.title_journal)
            .setItems(
                journalPrompts
            ) { _, which ->
                binding.editTextNote.setText(journalPrompts[which])
            }
        builder.create().show()
    }

    private fun setImage() {
        if (viewModel.journal.value!!.imageFilename.isBlank()) {
            binding.imageViewPictureOfTheDay.setImageDrawable(null)
        } else {
                Glide.with(this)
                    .load(JournalImageCompat.glideModel(viewModel.journal.value!!.imageFilename))
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
            deleteImportedImage(viewModel.journal.value!!.imageFilename)
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
    }

    private fun startCrop(uri: Uri) {
        try {
            requireContext().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: SecurityException) {
            // This is expected for camera captures and some providers. The temporary grant is
            // enough for the crop step and the final image is imported into app storage.
        }

        pendingImportedImageFile?.delete()
        pendingImportedImageFile = createImportedImageFile()
        val destinationUri = fileToUri(pendingImportedImageFile!!)
        val options = UCrop.Options().apply {
            setCompressionQuality(95)
            setHideBottomControls(false)
            setFreeStyleCropEnabled(true)
            setToolbarColor(requireContext().getColorThemeRes(R.attr.colorPrimary))
            setStatusBarColor(requireContext().getColorThemeRes(R.attr.colorPrimary))
            setToolbarWidgetColor(requireContext().getColorThemeRes(R.attr.colorOnPrimary))
        }
        val cropIntent = UCrop.of(uri, destinationUri)
            .useSourceImageAspectRatio()
            .withOptions(options)
            .getIntent(requireContext())
        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        cropImage.launch(cropIntent)
    }

    private fun finalizeCroppedImage() {
        val importedImage = pendingImportedImageFile ?: return
        deleteImportedImage(viewModel.journal.value!!.imageFilename)
        viewModel.journal.value!!.imageFilename = importedImage.absolutePath
        pendingImportedImageFile = null
        setImage()
    }

    private fun createImportedImageFile(): File {
        val imageDirectory = JournalImageCompat.importedImageDirectory(requireContext())
        if (!imageDirectory.exists()) {
            imageDirectory.mkdirs()
        }
        return File(imageDirectory, "journal_${viewModel.journal.value!!.date}_${UUID.randomUUID()}.jpg")
    }

    private fun createTempSelectionFile(prefix: String): File {
        val tempDirectory = File(requireContext().cacheDir, "journal-image-selection")
        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs()
        }
        return File.createTempFile(prefix, ".jpg", tempDirectory)
    }

    private fun fileToUri(file: File): Uri {
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )
    }

    private fun cleanupPendingSelectionFiles() {
        pendingImportedImageFile?.delete()
        pendingImportedImageFile = null
        cleanupCameraTempFile()
    }

    private fun cleanupCameraTempFile() {
        cameraTempFile?.delete()
        cameraTempFile = null
    }

    private fun deleteImportedImage(path: String) {
        if (!JournalImageCompat.isImportedImage(requireContext(), path)) {
            return
        }

        val imageFile = File(path)
        imageFile.delete()
    }

    private fun migrateLegacyImageIfNeeded() {
        val path = viewModel.journal.value?.imageFilename ?: return
        if (path.isBlank() || JournalImageCompat.isImportedImage(requireContext(), path)) {
            legacyMigrationInProgressFor = null
            return
        }
        if (legacyMigrationInProgressFor == path) {
            return
        }

        legacyMigrationInProgressFor = path
        viewLifecycleOwner.lifecycleScope.launch {
            val migratedPath = withContext(Dispatchers.IO) {
                importLegacyImage(path)
            }

            if (!isAdded || viewModel.journal.value?.imageFilename != path) {
                if (legacyMigrationInProgressFor == path) {
                    legacyMigrationInProgressFor = null
                }
                return@launch
            }

            if (migratedPath != null) {
                viewModel.journal.value!!.imageFilename = migratedPath
                viewModel.save()
                setImage()
            }
            legacyMigrationInProgressFor = null
        }
    }

    private fun importLegacyImage(path: String): String? {
        val sourceUri = JournalImageCompat.toImportUri(path) ?: return null
        val destinationFile = createImportedImageFile()
        val imported = runCatching {
            requireContext().contentResolver.openInputStream(sourceUri)?.use { input ->
                destinationFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return null
            destinationFile.absolutePath
        }.getOrNull()

        if (imported == null) {
            destinationFile.delete()
        }
        return imported
    }

}
