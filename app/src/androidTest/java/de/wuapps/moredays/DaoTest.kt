package de.wuapps.moredays

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.dao.ChallengeDao
import de.wuapps.moredays.database.entity.Challenge
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class DaoTest {
    private lateinit var challengeDao: ChallengeDao
    private lateinit var db: MoreDaysDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MoreDaysDatabase::class.java).build()
        challengeDao = db.challengeDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun getDueByStatusEmptyDb() {
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DATE, 1);
        val future = Calendar.getInstance()
        future.add(Calendar.DATE, 10);
        // test get due running challenges, with status ok and start date ok
        val challengeRunning = Challenge()
        challengeRunning.status = Challenge.CHALLENGE_RUNNING
        challengeRunning.deadline = tomorrow
        val challengeFailed = Challenge()
        challengeFailed.status = Challenge.CHALLENGE_FAILED
        challengeRunning.uid = runBlocking { challengeDao.insert(challengeRunning) }
        challengeFailed.uid = runBlocking { challengeDao.insert(challengeFailed)}
        var actual = runBlocking { challengeDao.getDueByStatus(Challenge.CHALLENGE_RUNNING).firstOrNull()}
        assertEquals("test get due running challenges, with status ok and start date ok, check not null", true, actual != null)
        if (actual != null) {
            assertEquals(
                "test get due running challenges, with status ok and start date ok, size of returned list",
                1,
                actual.size
            )
            if (actual.size == 1)
                assertEquals(
                    "test get due running challenges, with status ok and start date ok",
                    challengeRunning.uid,
                    actual.get(0).uid
                )
        }
        // test get due running challenges, with status running and start tomorrow - should not happen
        runBlocking { challengeDao.deleteAll()}
        val challenge = Challenge()
        challenge.startDate = tomorrow
        challenge.deadline = future
        challenge.uid = runBlocking { challengeDao.insert(challenge) }
        actual = runBlocking { challengeDao.getDueByStatus(Challenge.CHALLENGE_RUNNING).firstOrNull()}
        assertEquals("test get due running challenges, check not null", true, actual != null)
        if (actual != null) {
            assertEquals(
                "test get due running challenges, check no matches -- ignore challenges starting tomorrow",
                0,
                actual.size
            )
        }
    }

}