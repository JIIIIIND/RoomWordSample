package kr.co.sisoul.roomwordsample

import androidx.lifecycle.*
import kotlinx.coroutines.launch

/*
 * ViewModel에는 ViewModel보다 수명이 짧은(Activity/Fragment/View) Context 참조를 가지지 않도록 주의해야 함
 * 메모리 누수 발생
 */
class WordViewModel(private val repository: WordRepository) : ViewModel() {

    val allWords: LiveData<List<Word>> = repository.allWords.asLiveData()

    fun insert(word: Word) = viewModelScope.launch {
        repository.insert(word)
    }
}

class WordViewModelFactory(private val repository: WordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}