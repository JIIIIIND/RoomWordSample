package kr.co.sisoul.roomwordsample

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/*
 * - Room의 Database class는 abstract이고 RoomDatabase를 확장해야 함
 * - DB에 속한 항목을 선언하고 버전 번호를 설정, 각 항목은 db에 만들어질 테이블에 상응함
 * - 실제 앱에서는 현재 스키마를 버전 제어 시스템으로 확인할 수 있도록 스키마를 내보내는 데 사용할 Room Directory를 설정하는 것이 좋음
 * - DB의 여러 인스턴스가 동시에 열리는 것을 막기 위해 싱글톤으로 정의함
 * - 각 @Dao의 추상 "getter"를 통해 DAO를 노출
 */
@Database(entities = arrayOf(Word::class), version = 1, exportSchema = false)
public abstract class WordRoomDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.wordDao())
                }
            }
        }

        suspend fun populateDatabase(wordDao: WordDao) {
            wordDao.deleteAll()

            var word = Word("Hello")
            wordDao.insert(word)
            word = Word("World!")
            wordDao.insert(word)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: WordRoomDatabase? = null

        /*
         * Room 데이터베이스 작업을 UI Thread에서 할 수 없으므로 IO Dispatcher에서 코루틴이 실행되도록 함
         * 코루틴 범위도 매개변수로 가져올 수 있도록 수정
         */
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): WordRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordRoomDatabase::class.java,
                    "word_database"
                )
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}