package au.org.nac.nactive.inject

import au.org.nac.nactive.NACtiveApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * App Module
 */

@Module class AppModule(val app: NACtiveApp){
    @Provides
    @Singleton fun provideApp() = app
}
