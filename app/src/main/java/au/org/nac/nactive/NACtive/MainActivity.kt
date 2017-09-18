package au.org.nac.nactive.NACtive

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import au.org.nac.nactive.R
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_setup_btn.onClick {
             val intent = Intent(this, Setup::class.java)
            startActivity(intent)
        }

        main_exercise_btn.onClick {
            val intent = Intent(this, ExercisesActivity::class.java)
            startActivity(intent)
        }

        main_information_btn.onClick{
            val intent = Intent(this, Information::class.java)
            startActivity(intent)
        }
    }
}
