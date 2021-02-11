package de.wuapps.moredays.ui.journal


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import com.opensooq.supernova.gligar.GligarPicker
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.R
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.databinding.JournalFragmentBinding
import de.wuapps.moredays.ui.ResponsiveGridLayoutManager
import de.wuapps.moredays.ui.tools.handleFab
import de.wuapps.moredays.ui.tools.setFabOnClickListener
import de.wuapps.moredays.ui.tools.showFab
import kotlinx.coroutines.InternalCoroutinesApi

@GlideModule
class JournalFragment : Fragment(), IconDialog.Callback {

    private lateinit var binding: JournalFragmentBinding
    private lateinit var viewModel: JournalViewModel
    private var selectedColor: Int = 0
    private val ICON_DIALOG_TAG = "ICON_DIALOG_TAG"
    private val REQUEST_IMAGE = 42
    private lateinit var adapter: IconAdapter
    private val maxIcons = 8

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        val db = MoreDaysDatabase.getInstance(application)
        val arguments = JournalFragmentArgs.fromBundle(requireArguments())

        viewModel = ViewModelProvider(
            this,
            JournalViewModelFactory(
                db.journalDao,
                arguments.journal
            )
        ).get(JournalViewModel::class.java)
        binding = JournalFragmentBinding.inflate(inflater, container, false)
        binding.imageButtonPickImage.setOnClickListener {
            GligarPicker().requestCode(REQUEST_IMAGE).withFragment(this).show()
        }
        handleFab(viewModel.hasFab)
        binding.buttonSave.setOnClickListener { save() }
        if (!viewModel.isNew) binding.buttonDelete.setOnClickListener { delete() }
        setImage()
        val iconDialog =
            childFragmentManager.findFragmentByTag(ICON_DIALOG_TAG) as IconDialog?
                ?: IconDialog.newInstance(IconDialogSettings {
                    maxSelection = maxIcons
                    showMaxSelectionMessage = true
                })
        setFabOnClickListener {
            iconDialog.selectedIconIds = viewModel.journal.icons
            iconDialog.show(
                childFragmentManager,
                ICON_DIALOG_TAG
            )
        }
        val layoutManager = LinearLayoutManager(application)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerviewIcons.layoutManager = layoutManager
        adapter = IconAdapter()
        binding.recyclerviewIcons.adapter = adapter
        val icons = viewModel.journal.icons.map { MoreDaysApplication.iconPack!!.getIcon(it)!! }
        setIcons(icons)
        binding.journalViewModel = viewModel
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showFab()
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
                    viewModel.journal.imageFilename = imagesList.get(0)
                    setImage()
                }
            }
        }
    }

    private fun setImage() {
        if (!viewModel.journal.imageFilename.isNullOrBlank())
            Glide.with(this)
                .load(viewModel.journal.imageFilename)
                .into(binding.imageViewPictureOfTheDay);
    }

    private fun save() {
        viewModel.save()
        findNavController().popBackStack()
    }

    private fun delete() {
        if (!viewModel.isNew) {
            viewModel.delete()
            view?.post {
                findNavController().popBackStack()
            }
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
        viewModel.journal.icons.clear()
        viewModel.journal.icons.addAll(icons.map { it.id })
        setIcons(icons)
    }
}