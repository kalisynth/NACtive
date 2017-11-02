package au.org.nac.nactive.NACtive

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
import com.mcxiaoke.koi.ext.onItemClick
import io.objectbox.Box
import io.objectbox.query.Query
import kotlinx.android.synthetic.main.activity_edit_work_out.*

class EditWorkOut : AppCompatActivity() {

    private lateinit var bpSpinner : Spinner
    private lateinit var bpAdapter : ArrayAdapter<BodyParts>
    private lateinit var selectedBodyPart : BodyParts
    private lateinit var exerciseListView: ListView
    private lateinit var currentExerciseListView : ListView
    private lateinit var workOutFreqRand : RadioButton
    private lateinit var workOutFreqSched : RadioButton
    private lateinit var workOutFreqAlways : RadioButton
    private lateinit var wSchedCalendar : CalendarView
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
    private var workOut : WorkOutSession? = WorkOutSession()
    private var workOutId : Long = 0

    private lateinit var userBox : Box<User>
    private lateinit var userQuery : Query<User>
    private var user : User? = User()
    private var userId : Long = 0

    private var isFreq = CurrentUser.freq

    private lateinit var exerciseList : MutableList<Exercise>

    companion object {
        internal val EXTRA_WORKOUT_ID = "workoutId"
        internal val EXTRA_USER_ID = "userId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_work_out)

        exerciseBox = (application as NACtiveApp).boxStore.boxFor(Exercise::class.java)
        workoutBox = (application as NACtiveApp).boxStore.boxFor(WorkOutSession::class.java)

        workOutId = intent.getLongExtra(EXTRA_WORKOUT_ID, -1)
        userId = intent.getLongExtra(EXTRA_USER_ID, -1)

        if(userId < 1){
            user = User()
        } else {
            userQuery = userBox.query().equal(User_.id, userId).build()
            user = userQuery.findUnique() as User
        }

        if(workOutId < 1){
            if(userId < 1){
                workOut = WorkOutSession()
                isFreq = ScheduleFrequency.ALWAYS
                exerciseList = mutableListOf()
            } else {
                workoutQuery = workoutBox.query().equal(WorkOutSession_.frequencySchedule, user!!.currentWorkOutId).build()
                workOut = workoutQuery.findUnique() as WorkOutSession
                isFreq = NACiveUtils.returnFreqEnum(workOut!!.frequencySchedule.toString())
                exerciseList = workOut!!.exercises as MutableList<Exercise>
            }
        } else {
            workoutQuery = workoutBox.query().equal(WorkOutSession_.id, workOutId).build()
            workOut = workoutQuery.findUnique() as WorkOutSession
            isFreq = NACiveUtils.returnFreqEnum(workOut!!.frequencySchedule.toString())
            exerciseList = workOut!!.exercises as MutableList<Exercise>
        }

        exercisesQuery = exerciseBox.query().order(Exercise_.name).build()
        updateExercises()
        setUpView()
    }

    private fun setUpView(){
        bpSpinner = wAoFSpinner
        bpAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, BodyParts.values())

        exerciseListView = wExercisesRV
        currentExerciseListView = wSetup_Current_Exercises
        exerciseAdapter = ExerciseAdapter()
        currentExerciseAdapter = ExerciseAdapter()
        wSaveBtn = wSetup_Save_btn
        wUpdateBtn = wSetup_Update_btn

        exerciseListView.adapter = exerciseAdapter
        exerciseListView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                exerciseList.add(exerciseAdapter.getItem(p2))
                currentExerciseAdapter.notifyDataSetChanged()
                updateExercises()
                Log.d(TAG, "Item Selected")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //Do Nothing? that seems reasonable
            }
        }

        exerciseListView.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                exerciseList.add(exerciseAdapter.getItem(p2))
                currentExerciseAdapter.notifyDataSetChanged()
                Log.d(TAG, "Item Clicked")
            }
        }

        currentExerciseListView.adapter = currentExerciseAdapter
        currentExerciseListView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                exerciseList.remove(currentExerciseAdapter.getItem(p2))
                currentExerciseAdapter.notifyDataSetChanged()
                updateExercises()
                Log.d(TAG, "Item Selected")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //Do Nothing Also? still seems reasonable... probably wrong though.
            }
        }

        currentExerciseListView.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                exerciseList.removeAt(p2)
                currentExerciseAdapter.notifyDataSetChanged()
                Log.d(TAG, "Item Clicked")
            }
        }

        bpSpinner.adapter = bpAdapter

        val defpos = bpAdapter.getPosition(BodyParts.DEFAULT)

        bpSpinner.setSelection(defpos)

        bpSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedBodyPart = BodyParts.values()[p2]
                if(bpAdapter.getItem(p2) != BodyParts.DEFAULT){
                    Log.i(TAG, "Body Part Selected: " + selectedBodyPart)
                    exerciseAdapter.setExercises(getExerciseBodyParts(exerciseList, bpAdapter.getItem(p2)))
                    exerciseAdapter.notifyDataSetChanged()
                } else {
                    Log.i(TAG, "Body Part Selected: Default")
                    exerciseAdapter.setExercises(exerciseList)
                    exerciseAdapter.notifyDataSetChanged()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i(TAG, "Nothing Selected")
                selectedBodyPart = BodyParts.DEFAULT
                exerciseAdapter.setExercises(exerciseList)
            }
        }

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

        wSaveBtn.setOnClickListener {
            saveNewWorkOut()
        }

        wUpdateBtn.setOnClickListener {
            if(workOut != null){
                updateWorkOut(workOut as WorkOutSession)
            }
        }

        wSchedCalendar = wCalendar

        updateFreq(isFreq)
    }

    private fun updateExercises(){
        val exercises = exercisesQuery.find()
        exerciseAdapter.setExercises(exercises)
        currentExerciseAdapter.setExercises(exerciseList)
    }

    private fun updateFreq(sf : ScheduleFrequency){
        when(sf){
            ScheduleFrequency.ALWAYS -> {
                //TODO ALWAYS
                if(wSchedCalendar.visibility == View.VISIBLE){
                    wSchedCalendar.visibility = View.GONE
                }
            }
            ScheduleFrequency.SCHEDULED -> {
                if(wSchedCalendar.visibility == View.GONE){
                    wSchedCalendar.visibility = View.VISIBLE
                }
                //TODO SCHEDULED
            }
            ScheduleFrequency.RANDOM -> {
                //TODO RANDOM
                if(wSchedCalendar.visibility == View.VISIBLE){
                    wSchedCalendar.visibility = View.GONE
                }
            }
            else -> {
                Log.i(TAG, "Freq Set to Else")
                if(wSchedCalendar.visibility == View.VISIBLE){
                    wSchedCalendar.visibility = View.GONE
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
        workOut.exercises = exerciseList
        //TODO Save Exercises to WorkOut
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
        Log.i(TAG, "New Workout: " + workOut.id + " Added")
    }

    private fun updateWorkOut(workOut: WorkOutSession){
        this.workOut = workOut
        Log.i(TAG, "Updating WorkOut Session" + workOut.id)
        workOut.name = wEName.text.toString()
        workOut.areaOfFocus = selectedBodyPart.friendlyBodyPart
        workOut.frequencySchedule = isFreq.toString()
        workOut.exercises = exerciseList
        //TODO update exercises in WorkOut
        workoutBox.put(workOut)
        Log.i(TAG, "WorkOut Updated: " + workOut.id)
    }

    private fun getExerciseBodyParts(exerciseList : List<Exercise>, bodyPart : BodyParts) : List<Exercise>{
        val listSize = exerciseList.size
        Log.d(TAG, "Getting Body Parts List Size:" + listSize)
        val i = 0
        val newList = mutableListOf<Exercise>()
        while(i<listSize){
            val eSteps = exerciseList[i].stepsStrings as String
            if(eSteps.contains(bodyPart.toString(), false)){
                newList.add(exerciseList[i])
            }
        }

        Log.d(TAG, "NewList: " +  newList)
        return newList
    }
}