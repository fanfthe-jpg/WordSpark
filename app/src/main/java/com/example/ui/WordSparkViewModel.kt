package com.example.ui

import androidx.lifecycle.ViewModel
import com.example.data.WordLexicon
import com.example.model.DisplayedWord
import com.example.model.SparkBatch
import com.example.model.WordCategory
import com.example.model.WordEntry
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WordSparkUiState(
  val selectedCategory: WordCategory = WordCategory.ALL,
  val wordsPerPress: Int = 3,
  val displayedWords: List<DisplayedWord> = emptyList(),
  val rollCount: Int = 0,
  val history: List<SparkBatch> = emptyList(),
  val isHistorySheetOpen: Boolean = false,
)

class WordSparkViewModel(
  private val random: Random = Random.Default,
) : ViewModel() {

  private val _uiState = MutableStateFlow(WordSparkUiState())
  val uiState: StateFlow<WordSparkUiState> = _uiState.asStateFlow()

  private val recentWordsBuffer = ArrayDeque<String>()
  private var nextGenerationId = 1

  fun sparkWords() {
    _uiState.update { state ->
      val pool = WordLexicon.wordsFor(state.selectedCategory)
      val targetCount = state.wordsPerPress
      val currentSlots = state.displayedWords

      val pinnedBySlot =
        currentSlots
          .filter { it.isPinned && it.slotIndex < targetCount }
          .associateBy { it.slotIndex }

      val usedWords = pinnedBySlot.values.mapTo(mutableSetOf()) { it.entry.word }
      val currentDisplayedSet = currentSlots.mapTo(mutableSetOf()) { it.entry.word }

      val nextSlots = ArrayList<DisplayedWord>(targetCount)
      val rolledEntries = ArrayList<WordEntry>(targetCount)

      for (slot in 0 until targetCount) {
        val pinned = pinnedBySlot[slot]
        if (pinned != null) {
          nextSlots.add(pinned)
          rolledEntries.add(pinned.entry)
        } else {
          val picked = pickFreshWord(pool, usedWords, currentDisplayedSet)
          usedWords.add(picked.word)
          rememberRecentWord(picked.word)
          val genId = nextGenerationId++
          nextSlots.add(
            DisplayedWord(
              slotIndex = slot,
              entry = picked,
              isPinned = false,
              generationId = genId,
            )
          )
          rolledEntries.add(picked)
        }
      }

      val nextRollCount = state.rollCount + 1
      val batch =
        SparkBatch(
          rollNumber = nextRollCount,
          category = state.selectedCategory,
          words = rolledEntries,
        )

      state.copy(
        displayedWords = nextSlots,
        rollCount = nextRollCount,
        history = (listOf(batch) + state.history).take(25),
      )
    }
  }

  fun selectCategory(category: WordCategory) {
    _uiState.update { state -> state.copy(selectedCategory = category) }
  }

  fun selectWordsPerPress(count: Int) {
    if (count !in listOf(1, 3, 5)) return
    _uiState.update { state ->
      val current = state.displayedWords
      if (current.isEmpty()) {
        return@update state.copy(wordsPerPress = count)
      }

      val adjusted =
        if (count < current.size) {
          val pinned = current.filter { it.isPinned }
          val unpinned = current.filter { !it.isPinned }
          (pinned + unpinned)
            .take(count)
            .sortedBy { it.slotIndex }
            .mapIndexed { idx, item -> item.copy(slotIndex = idx) }
        } else if (count > current.size) {
          val pool = WordLexicon.wordsFor(state.selectedCategory)
          val used = current.mapTo(mutableSetOf()) { it.entry.word }
          val extras =
            (current.size until count).map { slot ->
              val picked = pickFreshWord(pool, used, used)
              used.add(picked.word)
              rememberRecentWord(picked.word)
              DisplayedWord(
                slotIndex = slot,
                entry = picked,
                isPinned = false,
                generationId = nextGenerationId++,
              )
            }
          current + extras
        } else {
          current
        }

      state.copy(wordsPerPress = count, displayedWords = adjusted)
    }
  }

  fun togglePin(slotIndex: Int) {
    _uiState.update { state ->
      val updated =
        state.displayedWords.map { item ->
          if (item.slotIndex == slotIndex) item.copy(isPinned = !item.isPinned) else item
        }
      state.copy(displayedWords = updated)
    }
  }

  fun restoreBatch(batch: SparkBatch) {
    _uiState.update { state ->
      val restored =
        batch.words.mapIndexed { index, entry ->
          DisplayedWord(
            slotIndex = index,
            entry = entry,
            isPinned = false,
            generationId = nextGenerationId++,
          )
        }
      state.copy(
        displayedWords = restored,
        wordsPerPress = batch.words.size.coerceIn(1, 5),
        selectedCategory = batch.category,
        isHistorySheetOpen = false,
      )
    }
  }

  fun clearHistory() {
    _uiState.update { it.copy(history = emptyList()) }
  }

  fun setHistorySheetOpen(open: Boolean) {
    _uiState.update { it.copy(isHistorySheetOpen = open) }
  }

  private fun pickFreshWord(
    pool: List<WordEntry>,
    usedWords: Set<String>,
    currentDisplayedSet: Set<String>,
  ): WordEntry {
    val strictCandidates =
      pool.filter {
        it.word !in usedWords &&
          it.word !in currentDisplayedSet &&
          it.word !in recentWordsBuffer
      }
    if (strictCandidates.isNotEmpty()) {
      return strictCandidates[random.nextInt(strictCandidates.size)]
    }

    val nonCurrentCandidates =
      pool.filter { it.word !in usedWords && it.word !in currentDisplayedSet }
    if (nonCurrentCandidates.isNotEmpty()) {
      return nonCurrentCandidates[random.nextInt(nonCurrentCandidates.size)]
    }

    val unusedCandidates = pool.filter { it.word !in usedWords }
    if (unusedCandidates.isNotEmpty()) {
      return unusedCandidates[random.nextInt(unusedCandidates.size)]
    }

    return pool[random.nextInt(pool.size)]
  }

  private fun rememberRecentWord(word: String) {
    recentWordsBuffer.addLast(word)
    while (recentWordsBuffer.size > 30) {
      recentWordsBuffer.removeFirst()
    }
  }
}
