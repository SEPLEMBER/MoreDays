package de.wuapps.moredays.ui.goal

import android.app.Application
import androidx.lifecycle.*
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.database.dao.GoalDao
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.databinding.GoalListFragmentBinding
import de.wuapps.moredays.ui.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*

class GoalListViewModel( private val goalRepository: GoalRepository, application: Application) : BaseViewModel(application) {
    val goals: LiveData<List<Goal>> = goalRepository.allGoals.asLiveData()

    init {

    }



}