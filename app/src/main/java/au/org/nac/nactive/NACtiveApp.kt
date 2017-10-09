package au.org.nac.nactive

import android.app.Application
import au.org.nac.nactive.model.Models
import au.org.nac.nactive.inject.AppComponent
import au.org.nac.nactive.inject.AppModule
import au.org.nac.nactive.inject.DaggerAppComponent
import com.chibatching.kotpref.Kotpref
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.TableCreationMode

//import io.objectbox.BoxStore

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
    }

    val data: KotlinReactiveEntityStore<Persistable> by lazy {
        val source = DatabaseSource(this, Models.DEFAULT, 2)
        source.setTableCreationMode(TableCreationMode.DROP_CREATE)
        KotlinReactiveEntityStore<Persistable>(KotlinEntityDataStore(source.configuration))
    }


    /*companion object {
        @JvmField
        var context : Context? = null

        // Not really needed since we can access the variable directly.
        @JvmStatic fun getMyApplicationContext(): Context? {
            return context
        }
    }*/

    /*//lateinit var boxStore: BoxStore
    private set*/

    override fun onCreate(){
        super.onCreate()
        component.inject(this)

        Kotpref.init(this)

        //boxStore = MyObjectBox.builder().androidContext(this).build()
    }
}
