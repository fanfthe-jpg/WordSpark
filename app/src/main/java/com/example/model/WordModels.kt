package com.example.model

enum class WordCategory(val label: String) {
  ALL("All"),
  VIVID("Vivid"),
  NATURE("Nature"),
  ACTION("Action"),
  ABSTRACT("Abstract"),
  WHIMSY("Whimsy"),
}

enum class PartOfSpeech(val abbrev: String) {
  NOUN("noun"),
  VERB("verb"),
  ADJ("adj"),
  ADV("adv"),
}

data class WordEntry(
  val word: String,
  val partOfSpeech: PartOfSpeech,
  val category: WordCategory,
  val cue: String,
)

data class DisplayedWord(
  val slotIndex: Int,
  val entry: WordEntry,
  val isPinned: Boolean = false,
  val generationId: Int,
)

data class SparkBatch(
  val rollNumber: Int,
  val category: WordCategory,
  val words: List<WordEntry>,
)
