package de.wuapps.moredays.utilities

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.emoji2.widget.EmojiTextView
import de.wuapps.moredays.R


@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("android:mood")
fun setMood(emojiTextView: EmojiTextView, mood: Int) {
   when(mood){
       1-> emojiTextView.text = emojiTextView.context.getText(R.string.mood_face_1)
       2-> emojiTextView.text = emojiTextView.context.getText(R.string.mood_face_2)
       3-> emojiTextView.text = emojiTextView.context.getText(R.string.mood_face_3)
       4-> emojiTextView.text = emojiTextView.context.getText(R.string.mood_face_4)
       5-> emojiTextView.text = emojiTextView.context.getText(R.string.mood_face_5)
       else -> emojiTextView.text = ""
   }
}
