package de.wuapps.moredays.ui.goal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.wuapps.moredays.R;
import de.wuapps.moredays.database.entity.Goal;


/**
 * {@link RecyclerView.Adapter} that can display a {@link Goal}.
 */
public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private final List<Goal> goals;

    public GoalsAdapter(List<Goal> items) {
        goals = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.goal_viewholder,
                        parent,
                        false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.goal = goals.get(position);
        //holder.txtName.setText(goals.get(position));
        //holder.mContentView.setText(goals.get(position).content);
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView txtPoints;
        public final TextView txtName;
        public final ImageView ivIcon;
        public Goal goal;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.txtName = view.findViewById(R.id.textViewName);
            txtPoints = view.findViewById(R.id.textViewPoints);
            ivIcon = view.findViewById(R.id.ivIcon);
        }

     }
}