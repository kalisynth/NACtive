package au.org.nac.nactive.nactive

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import au.org.nac.nactive.NACtiveApp
import au.org.nac.nactive.R
import au.org.nac.nactive.utilities.addOtago
import au.org.nac.nactive.model.Exercise
import com.mcxiaoke.koi.ext.onClick
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import kotlinx.android.synthetic.main.activity_options.*

class Options : AppCompatActivity() {

    private lateinit var exerciseBox : Box<Exercise>
    private lateinit var context : Context
    private lateinit var addOtagoBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        context = this

        exerciseBox = (application as NACtiveApp).boxStore.boxFor()

        setUpView()
    }

    private fun setUpView(){
        addOtagoBtn = option_Otago_add_all_btn

        addOtagoBtn.onClick {
            addOtago.addAll(context, exerciseBox)
        }
    }
}

//TODO Options - Text Size, Images / Text / Text and Images options, reset database options(hidden?)
