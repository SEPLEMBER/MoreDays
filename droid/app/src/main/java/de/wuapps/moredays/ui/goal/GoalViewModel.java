package de.wuapps.moredays.ui.goal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.wuapps.moredays.database.AppDatabase;
import de.wuapps.moredays.database.DatabaseClient;
import de.wuapps.moredays.database.dao.GoalDao;
import de.wuapps.moredays.database.entity.Goal;

public class GoalViewModel extends AndroidViewModel {
    private ExecutorService executorService;
    private MutableLiveData<List<Goal>> goals;
    private GoalDao goalDao;
    private final MutableLiveData<Goal> selectedGoal = new MutableLiveData<Goal>();

    public GoalViewModel(@NonNull Application application){
        super(Objects.requireNonNull(application));
        goalDao = DatabaseClient.getInstance(application).getAppDatabase().goalDao();
        executorService = Executors.newSingleThreadExecutor();
    }
    public void selectGoal(Goal goal) {
        selectedGoal.setValue(goal);
    }

    public LiveData<Goal> getSelectedGoal() {
        return selectedGoal;
    }

    public LiveData<List<Goal>> getGoals() {
        if (goals == null) {
            goals = new MutableLiveData<List<Goal>>();
            loadGoals();
        }
        return goals;
    }

    private void loadGoals() {
        // Do an asynchronous operation to fetch users.
    }

}