package au.org.nac.nactive.NACtive

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import au.org.nac.nactive.NACtiveApp
import au.org.nac.nactive.R
import au.org.nac.nactive.Utils.NACiveUtils
import au.org.nac.nactive.adapters.ExerciseAdapter
import au.org.nac.nactive.model.*
import au.org.nac.nactive.model.Constants.Companion.USERIDKEY
import au.org.nac.nactive.model.Constants.Companion.WORKOUTIDKEY
import com.mcxiaoke.koi.ext.onItemClick
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.query.Query
import kotlinx.android.synthetic.main.activity_edit_work_out.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class EditWorkOut : AppCompatActivity() {

    private lateinit var bpSpinner : Spinner
    private lateinit var bpAdapter : ArrayAdapter<BodyParts>
    private lateinit var selectedBodyPart : BodyParts
    private lateinit var exerciseListView: ListView
    private lateinit var currentExerciseListView : ListView
    private lateinit var workoutscheduleListView : ListView
    private lateinit var workOutFreqRand : RadioButton
    private lateinit var workOutFreqSched : RadioButton
    private lateinit var workOutFreqAlways : RadioButton
    private lateinit var wEName : TextView
    private lateinit var wSaveBtn : Button
    private lateinit var wUpdateBtn : Button

    private val TAG = "WORKOUTEDITOR"

    private lateinit var exerciseAdapter : ExerciseAdapter
    private lateinit var currentExerciseAdapter : ExerciseAdapter
    private lateinit var exercisesQuery : Query<Exercise>
    private lateinit var exerciseBox : Box<Exercise>

    private lateinit var workoutBox : Box<WorkOutSession>
    private lateinit var workoutQuery : Query<WorkOutSession>
    private var workOut : WorkOutSession = WorkOutSession()
    private var workOutId : Long = 0

    private lateinit var userBox : Box<User>
    private lateinit var userQuery : Query<User>
    private var user : User? = User()
    private var userId : Long = 0

    private var isFreq = CurrentUser.freq

    private lateinit var fullexerciseList : MutableList<Exercise>
    private lateinit var exerciseList : MutableList<Exercise>

    private var isNewExercise = false

    companion object {
        internal val EXTRA_WORKOUT_ID = WORKOUTIDKEY
        internal val EXTRA_USER_ID = USERIDKEY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_work_out)

        Log.i(TAG, "_-ObjectBoxes-_") //Open the Boxes from objectBox
        exerciseBox = (application as NACtiveApp).boxStore.boxFor<Exercise>()
        workoutBox = (application as NACtiveApp).boxStore.boxFor<WorkOutSession>()
        userBox = (application as NACtiveApp).boxStore.boxFor<User>()

        //Id's
        workOutId = intent.getLongExtra(EXTRA_WORKOUT_ID, -1)
        userId = intent.getLongExtra(EXTRA_USER_ID, -1)
        Log.i(TAG, "User Id: $userId || WorkOut Id: $workOutId")

        if(userId < 1){ //check if new user
            user = User()
            Log.i(TAG, "New User")
        } else {
            userQuery = userBox.query().equal(User_.id, userId).build()
            user = userQuery.findUnique() as User
            Log.i(TAG, "Old User Logged In")
            Log.d(TAG, "Old User: " + user)
        }

        if(workOutId < 1){ //Check whether workout id exists
            workOut = WorkOutSession()
            isFreq = ScheduleFrequency.ALWAYS
            exerciseList = mutableListOf()
            Log.i(TAG, "New Work Out")
            isNewExercise = true
        } else {
            workoutQuery = workoutBox.query().equal(WorkOutSession_.id, workOutId).build()
            workOut = workoutQuery.findUnique() as WorkOutSession
            isFreq = NACiveUtils.returnFreqEnum(workOut.frequencySchedule.toString())
            val workOutEList = workOut.exercises
            exerciseList = mutableListOf()
            if(workOut.exercises.isNotEmpty()){ //Add Exercises from WorkOut Exercise List
                Log.i(TAG, "Adding Exercises")
                Log.d(TAG, workOutEList.toString())
                var i = 0
                while(i < workOut.exercises.size){
                    exerciseList.add(workOutEList[i])
                    Log.d(TAG, "Exercise $i Added: ${workOutEList[i]}")
                    i++
                }
            } else {
                Log.i(TAG, "WorkOut Exercise List is Empty")
                Log.d(TAG, workOutEList.toString())
            }
            Log.i(TAG, "Old Work Out")
            Log.d(TAG, "Old Work Out: $workOut")
        }
        exercisesQuery = exerciseBox.query().order(Exercise_.name).build()
        Log.i(TAG, "View Setup")
        setUpView()
    }

    private fun setUpView(){
        //Buttons
        Log.i(TAG, "--Button Setup--")
        wSaveBtn = wSetup_Save_btn
        wSaveBtn.setOnClickListener {
            saveNewWorkOut()
        }
        wUpdateBtn = wSetup_Update_btn
        if(isNewExercise){
            wUpdateBtn.visibility = View.INVISIBLE
        }
        wUpdateBtn.setOnClickListener {
            if(!isNewExercise){
                updateWorkOut(workOut)
            }
        }
        Log.i(TAG, "--Button Setup--")

        //Full Exercise List
        Log.i(TAG, "--Full Exercise List--")
        exerciseListView = wExercisesRV
        exerciseAdapter = ExerciseAdapter()
        exerciseListView.adapter = exerciseAdapter
        exerciseListView.onItemClickListener = AdapterView.OnItemClickListener{_,_,p2,_ ->
            exerciseList.add(exerciseAdapter.getItem(p2))
            currentExerciseAdapter.notifyDataSetChanged()
            Log.i(TAG, "Item Clicked")
            Log.d(TAG, "Item Added: " + exerciseAdapter.getItem(p2))
        }
        Log.i(TAG, "--Full Exercise List--")
        Log.i(TAG, "--Current Exercise List--")
        currentExerciseListView = wSetup_Current_Exercises
        currentExerciseAdapter = ExerciseAdapter()
        currentExerciseListView.adapter = currentExerciseAdapter
        currentExerciseListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, p2, _ ->
            Log.i(TAG, "Item Clicked")
            Log.d(TAG, "Item Removed: " + currentExerciseAdapter.getItem(p2))
            exerciseList.removeAt(p2)
            currentExerciseAdapter.notifyDataSetChanged()
        }
        Log.i(TAG, "--Current Exercise List--")
        Log.i(TAG, "Update Exercises")
        updateExercises()

        //Body Parts Spinner
        Log.i(TAG, "--Body Parts Spinner Setup--")
        bpSpinner = wAoFSpinner
        bpAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, BodyParts.values())
        bpSpinner.adapter = bpAdapter
        var defaultPosition = bpAdapter.getPosition(BodyParts.DEFAULT)

        if(!isNewExercise){
            val selectBodyParts = NACiveUtils.returnBodyPart(workOut.areaOfFocus.toString())
            defaultPosition = bpAdapter.getPosition(selectBodyParts)
        }

        bpSpinner.setSelection(defaultPosition)
        bpSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedBodyPart = BodyParts.values()[p2]
                if(bpAdapter.getItem(p2) != BodyParts.DEFAULT){
                    Log.i(TAG, "Body Part Selected: " + selectedBodyPart)
                    exerciseAdapter.setExercises(getExerciseBodyParts(bpAdapter.getItem(p2)))
                    exerciseAdapter.notifyDataSetChanged()
                } else {
                    Log.i(TAG, "Body Part Selected: Default")
                    exerciseAdapter.setExercises(fullexerciseList)
                    exerciseAdapter.notifyDataSetChanged()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i(TAG, "Nothing Selected")
                selectedBodyPart = BodyParts.DEFAULT
                exerciseAdapter.setExercises(fullexerciseList)
            }
        }
        Log.i(TAG, "--Body Parts Spinner Setup--")

        workoutscheduleListView = wSetup_work_out_schedule_list

        Log.i(TAG, "--Radio Button Group--")
        workOutFreqAlways = wFreq_Always_RB
        workOutFreqAlways.setOnClickListener {
            isFreq = ScheduleFrequency.ALWAYS
            updateFreq(isFreq)
        }
        workOutFreqRand = wFreq_Random_RB
        workOutFreqRand.setOnClickListener {
            isFreq = ScheduleFrequency.RANDOM
            updateFreq(isFreq)
        }
        workOutFreqSched = wFreq_Scheduled_RB
        workOutFreqSched.setOnClickListener {
            isFreq = ScheduleFrequency.SCHEDULED
            updateFreq(isFreq)
        }
        Log.i(TAG, "--Radio Button Group--")

        updateFreq(isFreq)

        Log.i(TAG, "--TextViews--")
        wEName = wSetup_name_editText
        if(!isNewExercise){
            wEName.text = workOut.name
        }
    }

    private fun updateExercises(){
        fullexerciseList = exercisesQuery.find()
        Log.d(TAG, "Full Exercise List: " + fullexerciseList)
        exerciseAdapter.setExercises(fullexerciseList)
        currentExerciseAdapter.setExercises(exerciseList)
        Log.d(TAG, "Current Exercise List: " + exerciseList)
        Log.i(TAG, "Exercises Updated")
    }

    private fun updateFreq(sf : ScheduleFrequency){
        when(sf){
            ScheduleFrequency.ALWAYS -> {
                if(workoutscheduleListView.visibility == View.VISIBLE){
                    workoutscheduleListView.visibility = View.GONE
                }
            }
            ScheduleFrequency.SCHEDULED -> {
                if(workoutscheduleListView.visibility == View.GONE){
                    workoutscheduleListView.visibility = View.VISIBLE
                }
            }
            ScheduleFrequency.RANDOM -> {
                if(workoutscheduleListView.visibility == View.VISIBLE){
                    workoutscheduleListView.visibility = View.GONE
                }
            }
            else -> {
                Log.i(TAG, "Freq Set to Else")
                if(workoutscheduleListView.visibility == View.VISIBLE){
                    workoutscheduleListView.visibility = View.GONE
                }
            }
        }
    }

    private fun saveNewWorkOut(){
        val workOut = WorkOutSession()
        Log.i(TAG, "Adding New Workout")
        workOut.name = wEName.text.toString()
        workOut.areaOfFocus = selectedBodyPart.friendlyBodyPart
        workOut.frequencySchedule = isFreq.toString()
        workOut.exercises.clear()
        var i = 0
        while(i < exerciseList.size){
            workOut.exercises.add(exerciseList[i])
            i++
        }
        workoutBox.put(workOut)
        when(isFreq){
            ScheduleFrequency.ALWAYS -> {
                NACiveUtils.setUserCurrentWorkOutSession(user as User, userBox, workOutId)
                NACiveUtils.setUserNextWorkOutSession(user as User, userBox, workOutId)
            }
            ScheduleFrequency.SCHEDULED -> {
                //TODO Set Schedule Freq
            }
            ScheduleFrequency.RANDOM -> {
                //TODO set Random Freq
            }
            else -> {
                NACiveUtils.setUserCurrentWorkOutSession(user as User, userBox, workOutId)
                NACiveUtils.setUserNextWorkOutSession(user as User, userBox, workOutId)
            }
        }
        workOutId = workOut.id
        Log.i(TAG, "New Workout: " + workOut.id + " Added")
        if(wUpdateBtn.visibility == View.INVISIBLE){
            wUpdateBtn.visibility = View.VISIBLE
        }
        if(isNewExercise){
            isNewExercise = false
        }
        saveAlertPopUp()
    }

    private fun updateWorkOut(workOut: WorkOutSession){
        //Update WorkOut
        this.workOut = workOut
        Log.i(TAG, "Updating WorkOut Session" + workOut.id)
        workOut.name = wEName.text.toString()
        workOut.areaOfFocus = selectedBodyPart.friendlyBodyPart
        workOut.frequencySchedule = isFreq.toString()
        workOut.exercises.clear()
        var i = 0
        while(i < exerciseList.size){
            workOut.exercises.add(exerciseList[i])
            i++
        }
        workoutBox.put(workOut)
        Log.i(TAG, "WorkOut Updated: " + workOut.id)
    }

    private fun getExerciseBodyParts(bodyPart : BodyParts) : List<Exercise>{
        val listSize = fullexerciseList.size
        Log.d(TAG, "Getting Body Parts List Size:" + listSize)
        var i = 0
        val newList = mutableListOf<Exercise>()
        while(i<listSize){
            val eSteps = fullexerciseList[i].stepsStrings as String
            if(eSteps.contains(bodyPart.toString(), false)){
                newList.add(fullexerciseList[i])
            }
            i++
        }

        Log.d(TAG, "NewList: " +  newList)
        return newList
    }

    private fun saveAlertPopUp(){
        alert{
            customView{
                linearLayout{
                    button{
                        text = getString(R.string.start_workout_string)
                        onClick{
                            val intent = Intent(applicationContext, ExercisesActivity::class.java)
                            intent.putExtra(WORKOUTIDKEY, workOutId)
                            startActivity(intent)
                        }
                    }
                    button{
                        text = getString(R.string.back_to_setup_string)
                        onClick{
                            val intent = Intent(applicationContext, Setup::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        }.show()
    }
}