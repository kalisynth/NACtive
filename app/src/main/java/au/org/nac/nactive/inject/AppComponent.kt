package au.org.nac.nactive.inject

import au.org.nac.nactive.NACtiveApp
import dagger.Component
import javax.inject.Singleton

/**
 * App Comp
 */

@Singleton
        @Component(modules = arrayOf(AppModule::class))
        interface AppComponent{
    fun inject(app: NACtiveApp)
}
