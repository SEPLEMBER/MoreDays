package de.wuapps.moredays.ui.home

import android.content.Context

interface EntryType {
    fun getDesiredValue(): Int
    fun getCurrentValue(): Int
    fun getId(): String
    fun getName(): String
    fun getInfo(): String
    fun getSymbol(): Int
    fun offerInput(context: Context)
}