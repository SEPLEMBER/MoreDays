
package de.wuapps.moredays.ui.journal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.opensooq.supernova.gligar.GligarPicker


import de.wuapps.moredays.R
import de.wuapps.moredays.databinding.JournalFragmentBinding

@GlideModule
class JournalFragment : Fragment() {

    companion object {
        private const val REQUEST_IMAGE = 42
    }
    private lateinit var binding: JournalFragmentBinding
    private lateinit var viewModel: JournalViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = JournalFragmentBinding.inflate(inflater, container, false)
        binding.imageButtonPickImage.setOnClickListener{GligarPicker().requestCode(REQUEST_IMAGE).withFragment(this).show()}
        return binding.root
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            REQUEST_IMAGE -> {
                val imagesList = data?.extras?.getStringArray(GligarPicker.IMAGES_RESULT)// return list of selected images paths.
                if (!imagesList.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imagesList.get(0))
                        .into(binding.imageViewPictureOfTheDay);
                }
            }
        }
    }
}