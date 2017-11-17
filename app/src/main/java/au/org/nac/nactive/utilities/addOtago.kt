package au.org.nac.nactive.utilities

import android.content.Context
import android.util.Log
import au.org.nac.nactive.R
import au.org.nac.nactive.model.Exercise
import io.objectbox.Box

/**
 * Otago
 */

object addOtago {
    private var TAG = "Otago"

    fun addAll(context: Context, box : Box<Exercise>){
        val otagoNameArray = context.resources.getStringArray(R.array.OtagoNames_Array)
        val otagoStepsArray = context.resources.getStringArray(R.array.OtagoSteps_Array)
        var i = 0

        Log.i(TAG, "Otago Exercises Being added")

        while(i < otagoNameArray.size){
            val exercise = Exercise()
            Log.i(TAG, "Exercise " + i)
            exercise.name = otagoNameArray[i]
            Log.d(TAG, "Exercise Name: ${otagoNameArray[i]} added")
            exercise.minTime = 10
            exercise.difficultylevel = 1
            exercise.experience = 5
            exercise.stepsStrings = otagoStepsArray[i]
            box.put(exercise)
            i++
        }

        Log.i(TAG, "Otago exercises added")
    }
}