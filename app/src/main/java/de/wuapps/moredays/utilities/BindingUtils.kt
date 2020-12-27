package de.wuapps.moredays.utilities

import androidx.databinding.InverseMethod
import de.wuapps.moredays.database.entity.Goal

//@InverseMethod("positionToGoal")
//fun goalToPosition(goal: Goal?, goals: List<Goal>): Int {
//    if (goal == null)
//        return 0
//    return goals.indexOfFirst {
//        it.uid == goal.uid
//    }
//}
//
//fun positionToGoal(position: Int, goals: List<Goal>): Goal {
//    return goals.get(position)
//}
//
//@InverseMethod("positionToGoalId")
//fun goalIdToPosition(goalId: Long, goals: List<Goal>): Int {
//    if (goalId < 0)
//        return 0
//    return goals.indexOfFirst {
//        it.uid == goalId
//    }
//}
//
//fun positionToGoalId(position: Int, goals: List<Goal>): Goal {
//    return goals.get(position)
//}