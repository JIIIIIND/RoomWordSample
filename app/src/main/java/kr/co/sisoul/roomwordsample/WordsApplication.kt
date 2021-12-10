package kr.co.sisoul.roomwordsample

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/*
 * DB를 채우는 작업은 UI 수명 주기와 관련이 없으므로 viewModelScope와 같은 CoroutineScope를 사용하면 안됨
 */
class WordsApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { WordRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { WordRepository(database.wordDao()) }
}