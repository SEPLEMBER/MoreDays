package de.wuapps.moredays.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.ChallengeRepository
import de.wuapps.moredays.data.repository.GoalRepository
import java.util.*

class SharedViewModel(
    activityRepository: ActivityRepository,
    goalRepository: GoalRepository,
    challengeRepository: ChallengeRepository,
    application: Application
) : BaseViewModel(){
    val goals = goalRepository.allGoals.asLiveData()
    val activities = activityRepository.allActivitys.asLiveData()
    val challenges = challengeRepository.allChallenges.asLiveData()
    val date: LiveData<Date> = liveData {
        Date()
    }
    var iconPack: IconPack
    init {
        // Create an icon pack loader with application context.
        val loader = IconPackLoader(application)
        // Create an icon pack and load all drawables.
        iconPack = createDefaultIconPack(loader)
        iconPack.loadDrawables(loader.drawableLoader)

    }
}