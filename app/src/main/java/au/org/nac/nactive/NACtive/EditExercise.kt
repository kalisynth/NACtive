package au.org.nac.nactive.NACtive

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import au.org.nac.nactive.R
import au.org.nac.nactive.model.CurrentUser
import au.org.nac.nactive.model.Exercise
import kotlinx.android.synthetic.main.fragment_exercise_setup.*
import org.jetbrains.anko.image

/**
 * Edit already created exercises
 */

class EditExercise : AppCompatActivity(){

    //Vars
    private var currentUser = CurrentUser.name
    private lateinit var exercise : Exercise
    var levelofdifficulty : Int = 0
    lateinit var staron : Drawable
    lateinit var staroff : Drawable

    companion object {
        internal val EXTRA_PERSON_ID = "personId"
    }


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        //Wanted to try using fragments, Im no good with fragments, decided to just do it the way I have done it before
        setContentView(R.layout.fragment_exercise_setup)

        // Difficulty Checking and Images
        if(Build.VERSION.SDK_INT > 22) {
            staron = getDrawable(R.drawable.diffstaron)
            staroff = getDrawable(R.drawable.diffstaroff)
        } else {
            staron = this.resources.getDrawable(R.drawable.diffstaron)
            staroff = this.resources.getDrawable(R.drawable.diffstaroff)
        }

        initDifficulty(exercise.difficultylevel)

        exercise_diff_level_1.setOnClickListener {
            setDifficultyLevel(1)
        }

        exercise_diff_level_2.setOnClickListener {
            setDifficultyLevel(2)
        }

        exercise_diff_level_3.setOnClickListener {
            setDifficultyLevel(3)
        }
    }

    private fun initDifficulty(level: Int){
        when(level){
            0->{
                levelofdifficulty = 1
            }
            1,2,3->{
                levelofdifficulty = level
            }
        }
        setDifficultyLevel(levelofdifficulty)
    }

    private fun setDifficultyLevel(level: Int){
        when(level){
            1->{
                exercise_diff_level_2.image = staroff
                exercise_diff_level_3.image = staroff
            }
            2->{
                exercise_diff_level_2.image = staron
                exercise_diff_level_3.image = staroff
            }
            3->{
                exercise_diff_level_2.image = staron
                exercise_diff_level_3.image = staron
            }
        }
    }
}