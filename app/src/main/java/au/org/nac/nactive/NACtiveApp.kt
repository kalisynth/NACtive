package au.org.nac.nactive

import android.app.Application
import au.org.nac.nactive.inject.AppComponent
import au.org.nac.nactive.inject.AppModule
import au.org.nac.nactive.inject.DaggerAppComponent
import au.org.nac.nactive.model.MyObjectBox
import com.chibatching.kotpref.Kotpref
import io.objectbox.BoxStore

/**
 * NACTive Application Class
 */

class NACtiveApp : Application(){

    val component: AppComponent by lazy {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
    }

    companion object Constants {
        const val TAG = "NACtiveApp"
        const val EXTERNAL_DIR = false
    }

    lateinit var boxStore: BoxStore

    override fun onCreate(){
        super.onCreate()
        component.inject(this)

        Kotpref.init(this)

        boxStore = MyObjectBox.builder().androidContext(this).build()
    }
}
