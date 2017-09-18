package au.org.nac.nactive

import android.app.Application
import android.content.Context
import au.org.nac.nactive.inject.AppComponent
import au.org.nac.nactive.inject.AppModule
import au.org.nac.nactive.inject.DaggerAppComponent
import com.chibatching.kotpref.Kotpref

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

    companion object {
        @JvmField
        var context : Context? = null

        // Not really needed since we can access the variable directly.
        @JvmStatic fun getMyApplicationContext(): Context? {
            return context
        }
    }

    override fun onCreate(){
        super.onCreate()
        component.inject(this)

        Kotpref.init(this)
    }
}
