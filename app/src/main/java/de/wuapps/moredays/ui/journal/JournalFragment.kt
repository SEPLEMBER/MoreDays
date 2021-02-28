package de.wuapps.moredays.ui.journal


import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import com.opensooq.supernova.gligar.GligarPicker
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.R
import de.wuapps.moredays.databinding.JournalFragmentBinding
import de.wuapps.moredays.ui.tools.handleFab
import de.wuapps.moredays.ui.tools.setFabOnClickListener
import de.wuapps.moredays.ui.tools.showFab
import de.wuapps.moredays.utilities.InjectorUtils
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

@GlideModule
class JournalFragment : Fragment(), IconDialog.Callback {

    private lateinit var binding: JournalFragmentBinding
    private val args: JournalFragmentArgs by navArgs()
    private val viewModel: JournalViewModel by viewModels {
        InjectorUtils.provideJournalViewModelFactory(requireActivity(), args.journal)
    }
    private val ICON_DIALOG_TAG = "ICON_DIALOG_TAG"
    private val REQUEST_IMAGE = 42
    private lateinit var adapter: IconAdapter
    private val maxIcons = 100
    private lateinit var exampleAffirmations: List<String>
    private var optionsMenuDelete: MenuItem? = null
    private var optionsMenuDate: MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.setDateFormat(getString(R.string.date_ymd))
        binding = JournalFragmentBinding.inflate(inflater, container, false)
        binding.imageButtonPickImage.setOnClickListener {
            GligarPicker().requestCode(REQUEST_IMAGE).withFragment(this).show()
        }
        handleFab(viewModel.hasFab)
        binding.imageButtonSuggestAffirmation.setOnClickListener { suggestAffirmation() }
        binding.imageButtonRemoveImage.setOnClickListener{
            binding.imageViewPictureOfTheDay.setImageDrawable(null)
            viewModel.journal.value!!.imageFilename = ""
        }
        exampleAffirmations = getString(R.string.affirmation_examples).split("\\.".toRegex())
        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerviewIcons.layoutManager = layoutManager
        adapter = IconAdapter()
        viewModel.journal.observe(viewLifecycleOwner, {
            updateUI()
        })
        binding.journalViewModel = viewModel
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showFab()
    }

    private fun updateUI() {
        binding.textViewDate.text = viewModel.getCurrentDate()
        setImage()
        binding.recyclerviewIcons.adapter = adapter
        val icons =
            viewModel.journal.value!!.icons.map { MoreDaysApplication.iconPack!!.getIcon(it)!! }
        setIcons(icons)
        binding.editTextAffirmation.setText(viewModel.journal.value!!.affirmation)
        binding.editTextNote.setText(viewModel.journal.value!!.note)
        val iconDialog =
            childFragmentManager.findFragmentByTag(ICON_DIALOG_TAG) as IconDialog?
                ?: IconDialog.newInstance(IconDialogSettings {
                    maxSelection = maxIcons
                    showMaxSelectionMessage = true
                })
        setFabOnClickListener {
            iconDialog.selectedIconIds = viewModel.journal.value!!.icons
            iconDialog.show(
                childFragmentManager,
                ICON_DIALOG_TAG
            )
        }
        if (optionsMenuDelete != null) optionsMenuDelete!!.isVisible = viewModel.hasDeleteOption
        if (optionsMenuDate != null) optionsMenuDate!!.isVisible = viewModel.hasCalendarOption
    }

    private fun suggestAffirmation() {
        val builder = AlertDialog.Builder(this.activity)
        builder.setTitle(R.string.title_affirmation)
            .setItems(exampleAffirmations.toTypedArray(),
                DialogInterface.OnClickListener { dialog, which ->
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
        if (!viewModel.journal.value!!.imageFilename.isNullOrBlank())
            Glide.with(this)
                .load(viewModel.journal.value!!.imageFilename)
                .into(binding.imageViewPictureOfTheDay);
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

    private fun setIcons(icons: List<Icon>) {
        adapter.submitList(icons)
    }

    /**
     * IconDialog.Callback
     */
    @InternalCoroutinesApi
    override val iconDialogIconPack: IconPack?
        get() = MoreDaysApplication.iconPack

    @InternalCoroutinesApi
    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        viewModel.journal.value!!.icons.clear()
        viewModel.journal.value!!.icons.addAll(icons.map { it.id })
        setIcons(icons)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
        inflater.inflate(R.menu.delete_save_calendar_menu, menu)
        optionsMenuDelete = menu.findItem(R.id.menu_delete)
        optionsMenuDelete!!.isVisible = viewModel.hasDeleteOption
        optionsMenuDate = menu.findItem(R.id.menu_date)
        optionsMenuDate!!.isVisible = viewModel.hasCalendarOption
    }

    private fun showDatePicker() {
        var iniDate = viewModel.journal.value!!.getDateAsCalendar()
        binding.datePicker.init(
            iniDate.get(Calendar.YEAR), iniDate.get(Calendar.MONTH),
            iniDate.get(Calendar.DAY_OF_MONTH)
        ) { view, year, month, day ->
            iniDate.set(Calendar.YEAR, year)
            iniDate.set(Calendar.MONTH, month)
            iniDate.set(Calendar.DAY_OF_MONTH, day)
            viewModel.changeDate(iniDate)
            binding.datePicker.visibility = View.GONE
        }
        binding.datePicker.visibility = View.VISIBLE
    }
}