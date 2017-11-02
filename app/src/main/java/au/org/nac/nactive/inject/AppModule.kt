package au.org.nac.nactive.inject

import android.content.Context
import au.org.nac.nactive.NACtiveApp
import au.org.nac.nactive.model.Exercise
import au.org.nac.nactive.model.User
import au.org.nac.nactive.model.WorkOutSession
import dagger.Module
import dagger.Provides
import io.objectbox.Box
import javax.annotation.Signed
import javax.inject.Singleton

/**
 * App Module
 */

@Module class AppModule(val app: NACtiveApp){
    @Provides
    @Singleton fun provideApp() = app

    @Provides
    @Singleton fun provideUserBox(): Box<User> = app.boxStore.boxFor(User::class.java) as Box<User>

    @Provides
    @Singleton fun provideWorkOutBox() : Box<WorkOutSession> = app.boxStore.boxFor(WorkOutSession::class.java) as Box<WorkOutSession>

    @Provides
    @Singleton fun provideExerciseBox() : Box<Exercise> = app.boxStore.boxFor(Exercise::class.java) as Box<Exercise>
}
