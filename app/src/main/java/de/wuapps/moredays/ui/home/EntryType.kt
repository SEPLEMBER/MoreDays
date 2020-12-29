package de.wuapps.moredays.ui.home

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater

interface EntryType {
    fun getId(): String
    fun getName(): String
    fun getInfo(): String
    //fun getDrawing(): Drawable
    fun getSymbol(): Int
    fun offerInput(context: Context)
}