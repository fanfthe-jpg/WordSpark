package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.model.WordCategory
import com.example.ui.WordSparkScreen
import com.example.ui.WordSparkViewModel
import com.example.ui.theme.WordSparkTheme
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h891dp-xxhdpi")
class ExampleRobolectricTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun `app_name matches WordSpark`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("WordSpark", appName)
  }

  @Test
  fun `pressing spark button reveals random words and updates roll count`() {
    val viewModel = WordSparkViewModel(random = Random(42))

    composeTestRule.setContent {
      WordSparkTheme {
        WordSparkScreen(viewModel = viewModel)
      }
    }

    composeTestRule.onNodeWithText("Ready to spark").assertIsDisplayed()
    composeTestRule.onNodeWithTag("spark_words_button").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("word_card_0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("word_card_1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("word_card_2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("roll_counter_badge").assertIsDisplayed()
    assertEquals(3, viewModel.uiState.value.displayedWords.size)
    assertEquals(1, viewModel.uiState.value.rollCount)
  }

  @Test
  fun `switching count to 1 displays hero word card and pinning preserves word`() {
    val viewModel = WordSparkViewModel(random = Random(7))

    composeTestRule.setContent {
      WordSparkTheme {
        WordSparkScreen(viewModel = viewModel)
      }
    }

    composeTestRule.onNodeWithTag("spark_words_button").performClick()
    composeTestRule.waitForIdle()

    val firstSlotWord = viewModel.uiState.value.displayedWords[0].entry.word
    val secondSlotWord = viewModel.uiState.value.displayedWords[1].entry.word

    composeTestRule.onNodeWithTag("pin_button_0").performClick()
    composeTestRule.waitForIdle()
    assertTrue(viewModel.uiState.value.displayedWords[0].isPinned)

    composeTestRule.onNodeWithTag("spark_words_button").performClick()
    composeTestRule.waitForIdle()

    assertEquals(firstSlotWord, viewModel.uiState.value.displayedWords[0].entry.word)
    assertNotEquals(secondSlotWord, viewModel.uiState.value.displayedWords[1].entry.word)

    composeTestRule.onNodeWithTag("count_option_1").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("hero_word_card").assertIsDisplayed()
  }

  @Test
  fun `category filter restricts sparked words to chosen category`() {
    val viewModel = WordSparkViewModel(random = Random(99))

    composeTestRule.setContent {
      WordSparkTheme {
        WordSparkScreen(viewModel = viewModel)
      }
    }

    composeTestRule.onNodeWithTag("category_chip_nature").performClick()
    composeTestRule.onNodeWithTag("spark_words_button").performClick()
    composeTestRule.waitForIdle()

    val words = viewModel.uiState.value.displayedWords
    assertEquals(3, words.size)
    assertTrue(words.all { it.entry.category == WordCategory.NATURE })
  }
}
