package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.CopyAllButton
import com.example.ui.components.EmptySparkStage
import com.example.ui.components.HistoryListContent
import com.example.ui.components.SingleHeroWordCard
import com.example.ui.components.SparkControlDock
import com.example.ui.components.WordCardItem
import com.example.ui.components.WordSparkHeader
import com.example.ui.components.editorialPaperBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordSparkScreen(
  viewModel: WordSparkViewModel = viewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val haptic = LocalHapticFeedback.current
  val clipboardManager = LocalClipboardManager.current
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  fun copyWord(word: String) {
    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    clipboardManager.setText(AnnotatedString(word))
    scope.launch {
      snackbarHostState.currentSnackbarData?.dismiss()
      snackbarHostState.showSnackbar(context.getString(R.string.copied_single, word))
    }
  }

  fun copyAllWords() {
    val words = uiState.displayedWords.map { it.entry.word }
    if (words.isEmpty()) return
    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    clipboardManager.setText(AnnotatedString(words.joinToString(", ")))
    scope.launch {
      snackbarHostState.currentSnackbarData?.dismiss()
      snackbarHostState.showSnackbar(context.getString(R.string.copied_batch, words.size))
    }
  }

  BoxWithConstraints(modifier = Modifier.fillMaxSize().editorialPaperBackground()) {
    val isExpanded = maxWidth >= 840.dp

    Scaffold(
      containerColor = Color.Transparent,
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
      snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
      bottomBar = {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
          SparkControlDock(
            wordsPerPress = uiState.wordsPerPress,
            onSelectCount = { count ->
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              viewModel.selectWordsPerPress(count)
            },
            onSparkClick = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              viewModel.sparkWords()
            },
            modifier = if (isExpanded) Modifier.widthIn(max = 640.dp) else Modifier,
          )
        }
      },
    ) { innerPadding ->
      Row(
        modifier =
          Modifier.fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(innerPadding)
      ) {
        Column(
          modifier =
            Modifier.weight(1f)
              .fillMaxHeight()
              .then(if (!isExpanded) Modifier else Modifier.padding(end = 8.dp)),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Column(modifier = Modifier.widthIn(max = 640.dp).fillMaxSize()) {
            WordSparkHeader(
              rollCount = uiState.rollCount,
              historyCount = uiState.history.size,
              showHistoryButton = !isExpanded,
              onHistoryClick = { viewModel.setHistorySheetOpen(true) },
            )

            CategoryFilterRow(
              selectedCategory = uiState.selectedCategory,
              onSelectCategory = { category ->
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.selectCategory(category)
              },
            )

            Spacer(modifier = Modifier.height(10.dp))

            AnimatedContent(
              targetState = uiState.displayedWords.isEmpty(),
              transitionSpec = { fadeIn() togetherWith fadeOut() },
              label = "stageTransition",
              modifier = Modifier.weight(1f).fillMaxWidth(),
            ) { isEmpty ->
              if (isEmpty) {
                Box(
                  modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                  contentAlignment = Alignment.Center,
                ) {
                  EmptySparkStage(selectedCategory = uiState.selectedCategory)
                }
              } else {
                Column(modifier = Modifier.fillMaxSize()) {
                  Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    val pinnedCount = uiState.displayedWords.count { it.isPinned }
                    Text(
                      text =
                        if (pinnedCount > 0) "${uiState.displayedWords.size} words • $pinnedCount pinned"
                        else "${uiState.displayedWords.size} ${if (uiState.displayedWords.size == 1) "word" else "words"}",
                      style = MaterialTheme.typography.labelMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (uiState.displayedWords.size > 1) {
                      CopyAllButton(onClick = ::copyAllWords)
                    }
                  }

                  if (uiState.displayedWords.size == 1) {
                    Box(
                      modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp),
                      contentAlignment = Alignment.Center,
                    ) {
                      val item = uiState.displayedWords.first()
                      SingleHeroWordCard(
                        item = item,
                        onCopy = ::copyWord,
                        onTogglePin = { slot ->
                          haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                          viewModel.togglePin(slot)
                        },
                      )
                    }
                  } else {
                    LazyColumn(
                      modifier = Modifier.weight(1f).fillMaxWidth(),
                      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                      verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                      items(
                        items = uiState.displayedWords,
                        key = { "${it.slotIndex}_${it.generationId}" },
                      ) { item ->
                        WordCardItem(
                          item = item,
                          onCopy = ::copyWord,
                          onTogglePin = { slot ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.togglePin(slot)
                          },
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }

        if (isExpanded) {
          Surface(
            modifier =
              Modifier.widthIn(min = 280.dp, max = 340.dp)
                .fillMaxHeight()
                .padding(top = 16.dp, end = 20.dp, bottom = 16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
          ) {
            HistoryListContent(
              history = uiState.history,
              onRestoreBatch = { batch ->
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.restoreBatch(batch)
              },
              onClearHistory = { viewModel.clearHistory() },
            )
          }
        }
      }
    }

    if (!isExpanded && uiState.isHistorySheetOpen) {
      val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
      ModalBottomSheet(
        onDismissRequest = { viewModel.setHistorySheetOpen(false) },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
      ) {
        HistoryListContent(
          history = uiState.history,
          onRestoreBatch = { batch ->
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            viewModel.restoreBatch(batch)
          },
          onClearHistory = { viewModel.clearHistory() },
          modifier = Modifier.padding(bottom = 24.dp),
        )
      }
    }
  }
}
