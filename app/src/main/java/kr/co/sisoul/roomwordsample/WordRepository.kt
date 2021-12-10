package kr.co.sisoul.roomwordsample

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

/*
 * DAO는 전체 DB가 아닌 저장소 생성자에 전달 됨
 * DAO에 DB의 모든 읽기/쓰기 메서드가 포함되어 있으므로 DAO 액세스만 필요하기 때문이며 DB를 노출 할 필요가 없음
 * Room은 기본 스레드 밖에서 정지 쿼리를 실행함
 */
class WordRepository(private val wordDao: WordDao) {
    val allWords: Flow<List<Word>> = wordDao.getAlphabetizedWords()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(word: Word) {
        wordDao.insert(word)
    }
}