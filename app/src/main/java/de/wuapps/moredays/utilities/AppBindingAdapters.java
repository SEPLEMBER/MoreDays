package de.wuapps.moredays.utilities;

import android.view.View;

import androidx.databinding.BindingAdapter;
import androidx.emoji2.widget.EmojiTextView;

import de.wuapps.moredays.R;

public final class AppBindingAdapters {
    private AppBindingAdapters() {
    }

    @BindingAdapter("isGone")
    public static void bindIsGone(View view, boolean isGone) {
        view.setVisibility(isGone ? View.GONE : View.VISIBLE);
    }

    @BindingAdapter("mood")
    public static void setMood(EmojiTextView emojiTextView, int mood) {
        switch (mood) {
            case 1:
                emojiTextView.setText(emojiTextView.getContext().getText(R.string.mood_face_1));
                break;
            case 2:
                emojiTextView.setText(emojiTextView.getContext().getText(R.string.mood_face_2));
                break;
            case 3:
                emojiTextView.setText(emojiTextView.getContext().getText(R.string.mood_face_3));
                break;
            case 4:
                emojiTextView.setText(emojiTextView.getContext().getText(R.string.mood_face_4));
                break;
            case 5:
                emojiTextView.setText(emojiTextView.getContext().getText(R.string.mood_face_5));
                break;
            default:
                emojiTextView.setText("");
                break;
        }
    }
}
