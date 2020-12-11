package de.wuapps.moredays.ui.goal;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.wuapps.moredays.R;


/**
 * A fragment representing a list of Items.
 */
public class GoalsFragment extends Fragment {
    private GoalViewModel viewModel;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GoalsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static GoalsFragment newInstance(int columnCount) {
        GoalsFragment fragment = new GoalsFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        viewModel = new ViewModelProvider(this).get(GoalViewModel.class);
        viewModel.getGoals().observe(getViewLifecycleOwner(), goals -> {
            // update UI
        });
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, 2)); //@todo use utilities intelligent
            recyclerView.setAdapter(new GoalsAdapter((List<Goal>) viewModel.getGoals()));
        }
        return view;
    }
}