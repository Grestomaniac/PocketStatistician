package com.example.pocketstatistician

import android.view.GestureDetector
import android.view.MotionEvent
import com.example.pocketstatistician.activities.TableActivity
import com.example.pocketstatistician.convenience.log
import kotlin.math.abs

class NavigationGestureDetector(private val activity: TableActivity): GestureDetector.SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float
    ): Boolean {
        if (activity.selectedView.view == null) return false
        val selectedViewNotePos = activity.selectedView.notePosition
        val selectedViewVariablePos = activity.selectedView.variablePosition
        if (abs(velocityX) > abs(velocityY)) {
            if (velocityX < 0) moveRight(selectedViewNotePos, selectedViewVariablePos + 1)
            else moveLeft(selectedViewNotePos, selectedViewVariablePos - 1)
        }
        else {
            if (velocityY < 0) moveDown(selectedViewNotePos + 1, selectedViewVariablePos)
            else moveUp(selectedViewNotePos - 1, selectedViewVariablePos)
        }
        return false
    }

    private fun moveLeft(newNotePosition: Int, newVariablePosition: Int) {
        if (newVariablePosition < 0) return
        activity.recViewPosition = newVariablePosition
        activity.selectViewAtPosition(newNotePosition, newVariablePosition)
    }

    private fun moveRight(newNotePosition: Int, newVariablePosition: Int) {
        if (newVariablePosition >= activity.statistic.variables.size) return
        activity.recViewPosition = newVariablePosition
        activity.selectViewAtPosition(newNotePosition, newVariablePosition)
    }

    private fun moveDown(newNotePosition: Int, newVariablePosition: Int) {
        if (newNotePosition >= activity.statistic.data.size) return
        activity.selectViewAtPosition(newNotePosition, newVariablePosition)
    }

    private fun moveUp(newNotePosition: Int, newVariablePosition: Int) {
        if (newNotePosition < 0) return
        activity.selectViewAtPosition(newNotePosition, newVariablePosition)
    }

}