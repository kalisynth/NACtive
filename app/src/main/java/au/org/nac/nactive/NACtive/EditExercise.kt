package au.org.nac.nactive.NACtive

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import au.org.nac.nactive.NACtiveApp
import au.org.nac.nactive.R
import au.org.nac.nactive.Utils.NACiveUtils
import au.org.nac.nactive.model.BodyParts
import au.org.nac.nactive.model.Constants.Companion.EXERCISEIDKEY
import au.org.nac.nactive.model.Constants.Companion.USERIDKEY
import au.org.nac.nactive.model.CurrentUser
import au.org.nac.nactive.model.Exercise
import au.org.nac.nactive.model.Exercise_
import io.objectbox.Box
import io.objectbox.query.Query
import kotlinx.android.synthetic.main.fragment_exercise_setup.*
import org.jetbrains.anko.*
import org.jetbrains.anko.image
import org.jetbrains.anko.sdk25.coroutines.onClick
import javax.inject.Inject

/**
 * Edit already created exercises
 */

class EditExercise : AppCompatActivity(){

    //Vars
    private var currentUser = CurrentUser.name
    var levelofdifficulty : Int = 0
    lateinit var staron : Drawable
    lateinit var staroff : Drawable
    private var TAG = "EXERCISE_EDITOR"
    private lateinit var stepsList : MutableList<String>
    private var defaultLevel : Int = 0
    private var defaultReps : Int = 0
    private var READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 13
    private var filePath = ""

    //UI
    private lateinit var exerciseNameText : EditText
    private lateinit var exerciseDescText : EditText
    private lateinit var exerciseVidLocText : TextView
    private lateinit var exerciseRepsText : EditText
    private lateinit var exerciseSaveBtn : Button
    private lateinit var exerciseUpdateBtn : Button
    private lateinit var exerciseDeleteBtn : Button
    private lateinit var exerciseStepsList : ListView
    private lateinit var exerciseTotal : TextView
    private lateinit var exerciseMinTime : TextView
    private lateinit var exerciseAddStepsBtn : Button
    lateinit var stepsShortDesc : EditText
    lateinit var stepsLongDesc : EditText
    private var PICK_FILE_REQUEST = 9002

    //ObjectBox
    @Inject
    lateinit var exerciseBox : Box<Exercise>
    private lateinit var exerciseQuery : Query<Exercise>
    private var exercise : Exercise? = Exercise()

    lateinit var stepsAdapter : ArrayAdapter<String>

    lateinit var context : Context


    companion object {
        internal val EXTRA_PERSON_ID = USERIDKEY
        internal val EXTRA_EXERCISE_ID = EXERCISEIDKEY
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        //Wanted to try using fragments, Im no good with fragments, decided to just do it the way I have done it before
        setContentView(R.layout.fragment_exercise_setup)

        context = this

        exerciseBox = (application as NACtiveApp).boxStore.boxFor(Exercise::class.java)

        //init UI
        exerciseNameText = exercise_setup_name_edittext
        exerciseDescText = exercise_setup_description_edittext
        exerciseVidLocText = eSetup_videoLocation_edittext
        exerciseSaveBtn = eSetup_saveBTN
        exerciseDeleteBtn = eSetup_deleteBTN
        exerciseUpdateBtn = eSetup_updateBTN
        exerciseStepsList = eSetup_steps_list
        exerciseTotal = eSetup_total_TextView
        exerciseAddStepsBtn = eSetup_addStepsBTN
        exerciseMinTime = eSetup_fastTime_TextView

        //Get Defaults
        defaultLevel = R.integer.defaultlevel
        defaultReps = R.integer.defaultreps

        //get Exercise Id
        val exerciseId = intent.getLongExtra(EXTRA_EXERCISE_ID, -1)

        // Difficulty Checking and Images
        if(Build.VERSION.SDK_INT > 22) {
            staron = getDrawable(R.drawable.diffstaron)
            staroff = getDrawable(R.drawable.diffstaroff)
        } else {
            staron = this.resources.getDrawable(R.drawable.diffstaron)
            staroff = this.resources.getDrawable(R.drawable.diffstaroff)
        }

        //Check if New Exercise
        if(exerciseId < 1){
            exercise = Exercise()
            stepsList = mutableListOf("Add Steps by tapping the Add Step button, delete steps by long pressing on it")
            stepsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, stepsList)
            exerciseStepsList.adapter = stepsAdapter
            exerciseStepsList.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, i, _ ->
                deleteStep(i)
                true
            }
        } else {
            exerciseQuery = exerciseBox.query().equal(Exercise_.id, exerciseId).build()
            exercise = exerciseQuery.findUnique() as Exercise
            oldExercise(exercise!!)
        }

        exercise_diff_level_1.setOnClickListener {
            initDifficulty(1)
        }

        exercise_diff_level_2.setOnClickListener {
            initDifficulty(2)
        }

        exercise_diff_level_3.setOnClickListener {
            initDifficulty(3)
        }

        //Set Click Listeners
        exerciseSaveBtn.setOnClickListener {
            saveExercise()
        }
        exerciseUpdateBtn.setOnClickListener {
            updateExercise(exercise!!)
        }
        exerciseDeleteBtn.setOnClickListener {
            deleteExercise(exercise!!)
        }

        exerciseVidLocText.setOnClickListener {
            getFileString()
        }

        exerciseAddStepsBtn.setOnClickListener {
            addStep()
        }
    }

    private fun initDifficulty(level: Int){
        when(level){
            0->{
                levelofdifficulty = defaultLevel
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

    private fun oldExercise(exercise: Exercise){
        this.exercise = exercise
        Log.i(TAG, "Setting up old Exercise")
        try{
            exerciseNameText.setText(exercise.name)
            Log.d(TAG, "Exercise Name: " + exercise.name)
            exerciseDescText.setText(exercise.description)
            initDifficulty(exercise.difficultylevel)
            try{
                exerciseTotal.text = exercise.overallTotal.toString()
            } catch( e : Resources.NotFoundException){
                exerciseTotal.text = "0"
            }
            exerciseMinTime.text = NACiveUtils.getTime(exercise.minTime)
            exerciseVidLocText.text = exercise.videoLocation
            stepsList = NACiveUtils.returnStepsList(exercise.stepsStrings as String) as MutableList<String>
            if(stepsList.size == 0){
                stepsList = mutableListOf("Add Steps by tapping the Add Step button, delete steps by long pressing on it")
            }
            stepsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, stepsList)
            exerciseStepsList.adapter = stepsAdapter
            exerciseStepsList.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, i, _ ->
                deleteStep(i)
                true
            }
        } catch(e: NullPointerException){
            Log.e(TAG, "Error Setting Old Exercise" + e)
        }
    }

    private fun saveExercise(){
        val exercise = Exercise()
        exercise.name = exerciseNameText.text.toString()
        exercise.description = exerciseDescText.text.toString()
        exercise.overallTotal = 0
        exercise.videoLocation = exerciseVidLocText.text as String
        Log.d(TAG, "Exercise StepsList: " + stepsList)
        exercise.stepsStrings = NACiveUtils.stringFromList(stepsList)
        Log.d(TAG, "Result String: " + NACiveUtils.stringFromList(stepsList))
        exercise.difficultylevel = levelofdifficulty
        exerciseBox.put(exercise)
        Log.i(TAG, "New Exercise Id: " + exercise.id)
        Toast.makeText(this, "New Exercise Saved", Toast.LENGTH_LONG).show()
        saveAlertPopup()
    }

    private fun updateExercise(exercise : Exercise){
        this.exercise = exercise
        Log.i(TAG, "Updating Exercise Id: " + exercise.id)
        exercise.name = exerciseNameText.text.toString()
        exercise.description = exerciseDescText.text.toString()
        exercise.videoLocation = exerciseVidLocText.text.toString()
        exercise.stepsStrings = NACiveUtils.stringFromList(stepsList)
        exercise.difficultylevel = levelofdifficulty
        exerciseBox.put(exercise)
        Log.i(TAG, "Exercise has been updated Exercise Id: " + exercise.id)
        saveAlertPopup()
    }

    private fun deleteExercise(exercise : Exercise){
        this.exercise = exercise
        exerciseBox.remove(exercise.id)
    }

    private fun addStep(){
        val act = this
        var bodyPart = ""
        alert{
            customView {
                verticalLayout {
                stepsShortDesc = editText()
                stepsShortDesc.hint = "Short Description of Step"
                stepsLongDesc = editText()
                stepsLongDesc.hint = "Longer Description of Step"
                val bodyPartSelect = spinner()
                val bpsAdapter = ArrayAdapter<BodyParts>(act, android.R.layout.simple_spinner_dropdown_item, BodyParts.values())
                    bodyPartSelect.adapter = bpsAdapter
                    bodyPartSelect.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(p0: AdapterView<*>?) {
                            bodyPart = BodyParts.DEFAULT.toString()
                        }

                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            bodyPart = BodyParts.values()[p2].toString()
                        }
                    }
                yesButton{
                    val stepString = "${stepsShortDesc.text} : ${stepsLongDesc.text} : " + bodyPart + ","
                    stepsList.add(stepString)
                    stepsAdapter.notifyDataSetChanged()
                    }
                noButton {

                }
            }
            }
        }.show()
    }

    private fun deleteStep(i: Int){
        stepsList.removeAt(i)
        stepsAdapter.notifyDataSetChanged()
    }

    fun setStepsAdapter(){
        stepsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, stepsList)
    }

    private fun getFileString(){
        intent = Intent()
                .setType("video/*")
                .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select a Video"), 123);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123 && resultCode == Activity.RESULT_OK){
            filePath = data!!.data.toString()
            exerciseVidLocText.text = filePath
        }
    }

    private fun saveAlertPopup(){
        //Save window popup
        alert{
            customView{
                linearLayout{
                    button{
                        text = getString(R.string.edit_work_out_string)
                        onClick {
                            val intent = Intent(applicationContext, EditWorkOut::class.java)
                            startActivity(intent)
                        }
                    }
                    button{
                        text = getString(R.string.back_to_setup_string)
                        onClick {
                            val intent = Intent(applicationContext, Setup::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        }.show()
    }
}