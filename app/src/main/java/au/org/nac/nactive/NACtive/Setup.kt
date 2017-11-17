package au.org.nac.nactive.nactive

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import au.org.nac.nactive.NACtiveApp
import au.org.nac.nactive.R
import au.org.nac.nactive.adapters.ExerciseAdapter
import au.org.nac.nactive.adapters.WorkOutAdapter
import au.org.nac.nactive.model.*
import au.org.nac.nactive.model.Constants.Companion.EXERCISEIDKEY
import au.org.nac.nactive.model.Constants.Companion.WORKOUTIDKEY
import io.objectbox.Box
import io.objectbox.query.Query
import kotlinx.android.synthetic.main.activity_setup.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class Setup : AppCompatActivity() {

    private var currentUser = CurrentUser.name
    private var currentUserId = CurrentUser.userId
    private var user : User? = User()
    private var TAG = "SETUP"
    private var userId : Long = 0

    private lateinit var userBox : Box<User>
    private lateinit var userQuery : Query<User>
    private lateinit var exerciseBox : Box<Exercise>
    private lateinit var exerciseQuery : Query<Exercise>
    private lateinit var workoutBox : Box<WorkOutSession>
    private lateinit var workoutQuery : Query<WorkOutSession>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        userBox = (application as NACtiveApp).boxStore.boxFor(User::class.java)
        exerciseBox = (application as NACtiveApp).boxStore.boxFor(Exercise::class.java)
        workoutBox = (application as NACtiveApp).boxStore.boxFor(WorkOutSession::class.java)

        if(currentUserId != userId){
            try{
                userQuery = userBox.query().equal(User_.id, currentUserId).build()
                user = userQuery.findUnique()
                Log.i(TAG, "User Set to: " + user?.id)
            } catch(e: NoSuchElementException) {
                Log.i(TAG, "User Collection is Empty")
                userId = 0
            }
        }

        setup_session_btn.setOnClickListener {
            chooseWorkOutAlert()
        }

        setup_exercise_btn.setOnClickListener {
            chooseExerciseAlert()
        }

        setup_home_btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        setup_appsettings_btn.setOnClickListener {
            val intent = Intent(this, Options::class.java)
            startActivity(intent)
        }
    }

    private fun chooseExerciseAlert(){
        //Get Exercises
        exerciseQuery = exerciseBox.query().order(Exercise_.name).build()
        alert{
            customView{
                //Custom View for the Alert Window
                verticalLayout {
                    val exerciseListView = listView() //Create the ListView
                    val elAdapter = ExerciseAdapter() //Init ExerciseAdapter
                    exerciseListView.adapter = elAdapter
                    elAdapter.setExercises(exerciseQuery.find()) //Query Database for All Exercises

                    exerciseListView.onItemClickListener = AdapterView.OnItemClickListener{ _, _, p2, _ ->
                        intent = Intent(applicationContext, EditExercise::class.java)
                        intent.putExtra(EXERCISEIDKEY, elAdapter.getItemId(p2))
                        startActivity(intent) //Start New Activity using the Id from the Item Click

                    }
                    button{
                        text = getString(R.string.new_exercise_string)
                        onClick{
                            intent = Intent(applicationContext, EditExercise::class.java)
                            startActivity(intent)
                        }
                    }
                    cancelButton {
                        //close dialog hopefully?
                    }
                }
            }
        }.show()
    }

    private fun chooseWorkOutAlert(){
        workoutQuery = workoutBox.query().order(WorkOutSession_.name).build()
        alert{
            customView{
                verticalLayout{
                    val workOutListView = listView()
                    val wlAdapter = WorkOutAdapter()
                    workOutListView.adapter = wlAdapter
                    wlAdapter.setWorkOuts(workoutQuery.find())
                    workOutListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, p2, _ ->
                        intent = Intent(applicationContext, EditWorkOut::class.java)
                        intent.putExtra(WORKOUTIDKEY, wlAdapter.getItem(p2).id)
                        startActivity(intent)
                    }
                    button{
                        text = getString(R.string.new_workout_string)
                        onClick{
                            intent = Intent(applicationContext, EditWorkOut::class.java)
                            startActivity(intent)
                        }
                    }
                    cancelButton {
                        //close box
                    }
                }
            }
        }.show()
    }
}
