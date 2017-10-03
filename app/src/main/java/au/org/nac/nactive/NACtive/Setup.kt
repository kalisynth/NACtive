package au.org.nac.nactive.NACtive

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import au.org.nac.nactive.NACtiveApp
import au.org.nac.nactive.R
import au.org.nac.nactive.model.CurrentUser
import au.org.nac.nactive.model.User
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.reactivex.KotlinReactiveEntityStore
import kotlinx.android.synthetic.main.activity_setup.*

class Setup : AppCompatActivity() {

    private var currentUser = CurrentUser.name
    private lateinit var data : KotlinReactiveEntityStore<Persistable>
    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        data = (application as NACtiveApp).data

        if(currentUser.isEmpty()){
            val personId = 0
        } else {
            user = data.select(User::class).where(User::name.eq(currentUser)).get().single()
        }

        setup_user_btn.setOnClickListener {
            val intent = Intent(this, EditUser::class.java)
            startActivity(intent)
        }

        setup_session_btn.setOnClickListener {
            //TODO Setup Session Editor
        }

        setup_exercise_btn.setOnClickListener {
            val intent = Intent(this, EditExercise::class.java)
            startActivity(intent)
        }

        setup_home_btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
