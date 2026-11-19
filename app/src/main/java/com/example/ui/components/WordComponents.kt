package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.DisplayedWord
import com.example.model.SparkBatch
import com.example.model.WordCategory
import java.util.Locale

@Composable
fun Modifier.editorialPaperBackground(): Modifier {
  val bgColor = MaterialTheme.colorScheme.background
  val primaryGlow = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
  val secondaryGlow = MaterialTheme.colorScheme.secondary.copy(alpha = 0.06f)
  val dotColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)

  return this.drawBehind {
    drawRect(color = bgColor)

    drawCircle(
      brush =
        Brush.radialGradient(
          colors = listOf(primaryGlow, Color.Transparent),
          center = Offset(size.width * 0.85f, size.height * 0.14f),
          radius = size.maxDimension * 0.55f,
        ),
      center = Offset(size.width * 0.85f, size.height * 0.14f),
      radius = size.maxDimension * 0.55f,
    )

    drawCircle(
      brush =
        Brush.radialGradient(
          colors = listOf(secondaryGlow, Color.Transparent),
          center = Offset(size.width * 0.12f, size.height * 0.72f),
          radius = size.maxDimension * 0.5f,
        ),
      center = Offset(size.width * 0.12f, size.height * 0.72f),
      radius = size.maxDimension * 0.5f,
    )

    val step = 28.dp.toPx()
    var y = step
    while (y < size.height) {
      var x = step
      while (x < size.width) {
        drawCircle(color = dotColor, radius = 1.dp.toPx(), center = Offset(x, y))
        x += step
      }
      y += step
    }
  }
}

@Composable
fun WordSparkHeader(
  rollCount: Int,
  historyCount: Int,
  showHistoryButton: Boolean,
  onHistoryClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      SparkEmblem(modifier = Modifier.size(38.dp))
      Column {
        Text(
          text = stringResource(R.string.app_name),
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
          text = stringResource(R.string.app_tagline),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      if (rollCount > 0) {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.primaryContainer,
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
          modifier = Modifier.testTag("roll_counter_badge"),
        ) {
          Text(
            text = "#${rollCount.toString().padStart(2, '0')}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          )
        }
      }

      if (showHistoryButton) {
        Surface(
          onClick = onHistoryClick,
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surfaceVariant,
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
          modifier =
            Modifier.minimumInteractiveComponentSize()
              .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
              .testTag("history_button"),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Icon(
              imageVector = Icons.Outlined.History,
              contentDescription = stringResource(R.string.history_title),
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(20.dp),
            )
            if (historyCount > 0) {
              Text(
                text = historyCount.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun SparkEmblem(modifier: Modifier = Modifier) {
  val primary = MaterialTheme.colorScheme.primary
  val cream = MaterialTheme.colorScheme.onPrimary
  val ink = MaterialTheme.colorScheme.onBackground

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = primary,
    shadowElevation = 2.dp,
  ) {
    Canvas(modifier = Modifier.padding(7.dp)) {
      val w = size.width
      val h = size.height
      val cx = w / 2f
      val cy = h * 0.44f
      val outer = w * 0.36f
      val inner = w * 0.11f

      val star =
        Path().apply {
          moveTo(cx, cy - outer)
          lineTo(cx + inner, cy - inner)
          lineTo(cx + outer, cy)
          lineTo(cx + inner, cy + inner)
          lineTo(cx, cy + outer)
          lineTo(cx - inner, cy + inner)
          lineTo(cx - outer, cy)
          lineTo(cx - inner, cy - inner)
          close()
        }
      drawPath(path = star, color = cream)

      val bar =
        Path().apply {
          moveTo(w * 0.16f, h * 0.88f)
          lineTo(w * 0.84f, h * 0.72f)
          lineTo(w * 0.84f, h * 0.90f)
          lineTo(w * 0.16f, h * 1.0f)
          close()
        }
      drawPath(path = bar, color = ink.copy(alpha = 0.85f))
    }
  }
}

@Composable
fun CategoryFilterRow(
  selectedCategory: WordCategory,
  onSelectCategory: (WordCategory) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyRow(
    modifier = modifier.fillMaxWidth(),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items(WordCategory.entries, key = { it.name }) { category ->
      val selected = category == selectedCategory
      Surface(
        onClick = { onSelectCategory(category) },
        shape = CircleShape,
        color =
          if (selected) MaterialTheme.colorScheme.onBackground
          else MaterialTheme.colorScheme.surfaceVariant,
        border =
          if (selected) null
          else BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier =
          Modifier.heightIn(min = 40.dp)
            .minimumInteractiveComponentSize()
            .testTag("category_chip_${category.name.lowercase()}"),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          if (selected) {
            Icon(
              imageVector = Icons.Rounded.Check,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.background,
              modifier = Modifier.size(15.dp),
            )
          }
          Text(
            text = category.label,
            style = MaterialTheme.typography.labelLarge,
            color =
              if (selected) MaterialTheme.colorScheme.background
              else MaterialTheme.colorScheme.onSurface,
          )
        }
      }
    }
  }
}

@Composable
fun EmptySparkStage(
  selectedCategory: WordCategory,
  modifier: Modifier = Modifier,
) {
  val primary = MaterialTheme.colorScheme.primary
  val secondary = MaterialTheme.colorScheme.secondary

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    tonalElevation = 1.dp,
  ) {
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .drawBehind {
            drawCircle(
              color = primary.copy(alpha = 0.08f),
              radius = size.minDimension * 0.34f,
              center = Offset(size.width * 0.5f, size.height * 0.36f),
            )
            drawCircle(
              color = secondary.copy(alpha = 0.07f),
              radius = size.minDimension * 0.2f,
              center = Offset(size.width * 0.72f, size.height * 0.25f),
            )
          }
          .padding(horizontal = 28.dp, vertical = 40.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(64.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Outlined.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(30.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = stringResource(R.string.empty_stage_title),
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = stringResource(R.string.empty_stage_subtitle),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )

      Spacer(modifier = Modifier.height(16.dp))

      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
      ) {
        Text(
          text = "DECK: ${selectedCategory.label.uppercase(Locale.US)}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
      }
    }
  }
}

@Composable
fun SingleHeroWordCard(
  item: DisplayedWord,
  onCopy: (String) -> Unit,
  onTogglePin: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  val accentColor = categoryAccentColor(item.entry.category)
  val entranceScale = remember(item.generationId) { Animatable(0.88f) }
  val entranceAlpha = remember(item.generationId) { Animatable(0f) }

  LaunchedEffect(item.generationId) {
    entranceScale.animateTo(
      targetValue = 1f,
      animationSpec = spring(dampingRatio = 0.68f, stiffness = Spring.StiffnessMediumLow),
    )
  }
  LaunchedEffect(item.generationId) {
    entranceAlpha.animateTo(targetValue = 1f, animationSpec = tween(220))
  }

  Surface(
    onClick = { onCopy(item.entry.word) },
    shape = MaterialTheme.shapes.large,
    color = MaterialTheme.colorScheme.surfaceVariant,
    border =
      BorderStroke(
        width = if (item.isPinned) 2.dp else 1.dp,
        color = if (item.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
      ),
    tonalElevation = 2.dp,
    shadowElevation = 2.dp,
    modifier =
      modifier
        .fillMaxWidth()
        .graphicsLayer {
          scaleX = entranceScale.value
          scaleY = entranceScale.value
          alpha = entranceAlpha.value
        }
        .testTag("hero_word_card"),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
          CategoryTagPill(category = item.entry.category, accentColor = accentColor)
          PosTagPill(abbrev = item.entry.partOfSpeech.abbrev)
        }

        PinButton(
          isPinned = item.isPinned,
          onClick = { onTogglePin(item.slotIndex) },
          testTag = "pin_button_${item.slotIndex}",
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      Text(
        text = item.entry.word,
        style = MaterialTheme.typography.displayLarge,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("word_text_${item.slotIndex}"),
      )

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "“${item.entry.cue}”",
        style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )

      Spacer(modifier = Modifier.height(28.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Icon(
          imageVector = Icons.Outlined.ContentCopy,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(16.dp),
        )
        Text(
          text = "Tap card to copy",
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.primary,
        )
      }
    }
  }
}

@Composable
fun WordCardItem(
  item: DisplayedWord,
  onCopy: (String) -> Unit,
  onTogglePin: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  val accentColor = categoryAccentColor(item.entry.category)
  val alphaAnim = remember(item.generationId) { Animatable(0f) }
  val slideAnim = remember(item.generationId) { Animatable(24f) }

  LaunchedEffect(item.generationId) {
    alphaAnim.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 200, delayMillis = item.slotIndex * 45),
    )
  }
  LaunchedEffect(item.generationId) {
    slideAnim.animateTo(
      targetValue = 0f,
      animationSpec = spring(dampingRatio = 0.74f, stiffness = Spring.StiffnessMedium),
    )
  }

  Surface(
    onClick = { onCopy(item.entry.word) },
    shape = MaterialTheme.shapes.medium,
    color = MaterialTheme.colorScheme.surfaceVariant,
    border =
      BorderStroke(
        width = if (item.isPinned) 1.75.dp else 1.dp,
        color = if (item.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
      ),
    tonalElevation = 1.dp,
    modifier =
      modifier
        .fillMaxWidth()
        .graphicsLayer {
          alpha = alphaAnim.value.coerceIn(0f, 1f)
          translationY = slideAnim.value
          val s = 0.95f + 0.05f * alphaAnim.value
          scaleX = s
          scaleY = s
        }
        .testTag("word_card_${item.slotIndex}"),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 8.dp, top = 14.dp, bottom = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        modifier =
          Modifier.width(4.dp)
            .height(46.dp)
            .clip(CircleShape)
            .background(if (item.isPinned) MaterialTheme.colorScheme.primary else accentColor)
      )

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = item.entry.word,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false).testTag("word_text_${item.slotIndex}"),
          )
          PosTagPill(abbrev = item.entry.partOfSpeech.abbrev)
        }
        Spacer(modifier = Modifier.height(3.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = item.entry.category.label.uppercase(Locale.US),
            style = MaterialTheme.typography.labelSmall,
            color = accentColor,
          )
          Text(
            text = "•",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            text = item.entry.cue,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }

      PinButton(
        isPinned = item.isPinned,
        onClick = { onTogglePin(item.slotIndex) },
        testTag = "pin_button_${item.slotIndex}",
      )
    }
  }
}

@Composable
private fun CategoryTagPill(category: WordCategory, accentColor: Color) {
  Surface(
    shape = CircleShape,
    color = accentColor.copy(alpha = 0.14f),
  ) {
    Text(
      text = category.label.uppercase(Locale.US),
      style = MaterialTheme.typography.labelSmall,
      color = accentColor,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
    )
  }
}

@Composable
private fun PosTagPill(abbrev: String) {
  Surface(
    shape = CircleShape,
    color = MaterialTheme.colorScheme.surface,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
  ) {
    Text(
      text = abbrev,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
    )
  }
}

@Composable
private fun PinButton(
  isPinned: Boolean,
  onClick: () -> Unit,
  testTag: String,
) {
  IconButton(
    onClick = onClick,
    modifier =
      Modifier.minimumInteractiveComponentSize()
        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
        .testTag(testTag),
  ) {
    Icon(
      imageVector = if (isPinned) Icons.Rounded.PushPin else Icons.Outlined.PushPin,
      contentDescription =
        stringResource(if (isPinned) R.string.unpin_word else R.string.pin_word),
      tint =
        if (isPinned) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
fun SparkControlDock(
  wordsPerPress: Int,
  onSelectCount: (Int) -> Unit,
  onSparkClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val buttonScale by
    animateFloatAsState(
      targetValue = if (isPressed) 0.96f else 1f,
      animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
      label = "sparkButtonScale",
    )

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.background.copy(alpha = 0.94f),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    shadowElevation = 6.dp,
  ) {
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .navigationBarsPadding()
          .padding(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = stringResource(R.string.words_count_label),
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
          modifier =
            Modifier.clip(CircleShape)
              .background(MaterialTheme.colorScheme.surface)
              .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
              .padding(3.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          listOf(1, 3, 5).forEach { count ->
            val selected = count == wordsPerPress
            Box(
              modifier =
                Modifier.clip(CircleShape)
                  .background(
                    if (selected) MaterialTheme.colorScheme.onBackground else Color.Transparent
                  )
                  .clickable(role = Role.RadioButton) { onSelectCount(count) }
                  .sizeIn(minWidth = 48.dp, minHeight = 38.dp)
                  .padding(horizontal = 14.dp, vertical = 7.dp)
                  .testTag("count_option_$count"),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color =
                  if (selected) MaterialTheme.colorScheme.background
                  else MaterialTheme.colorScheme.onSurface,
              )
            }
          }
        }
      }

      Button(
        onClick = onSparkClick,
        interactionSource = interactionSource,
        shape = CircleShape,
        colors =
          ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
          ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp, pressedElevation = 0.dp),
        modifier =
          Modifier.fillMaxWidth()
            .heightIn(min = 54.dp)
            .graphicsLayer {
              scaleX = buttonScale
              scaleY = buttonScale
            }
            .testTag("spark_words_button"),
      ) {
        Icon(
          imageVector = Icons.Rounded.AutoAwesome,
          contentDescription = null,
          modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text =
            stringResource(
              if (wordsPerPress == 1) R.string.spark_single_button_label
              else R.string.spark_button_label
            ),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
      }
    }
  }
}

@Composable
fun HistoryListContent(
  history: List<SparkBatch>,
  onRestoreBatch: (SparkBatch) -> Unit,
  onClearHistory: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = stringResource(R.string.history_title),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )

      if (history.isNotEmpty()) {
        TextButton(
          onClick = onClearHistory,
          modifier = Modifier.minimumInteractiveComponentSize().testTag("clear_history_button"),
        ) {
          Icon(
            imageVector = Icons.Outlined.DeleteOutline,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = stringResource(R.string.clear_history))
        }
      }
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))

    if (history.isEmpty()) {
      Box(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = stringResource(R.string.history_empty),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
        )
      }
    } else {
      LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        items(history, key = { it.rollNumber }) { batch ->
          Surface(
            onClick = { onRestoreBatch(batch) },
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth().testTag("history_batch_${batch.rollNumber}"),
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text(
                  text = "#${batch.rollNumber.toString().padStart(2, '0')} • ${batch.category.label}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.primary,
                )
                Text(
                  text = "Tap to load",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              Text(
                text = batch.words.joinToString("  ·  ") { it.word },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun CopyAllButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    onClick = onClick,
    shape = CircleShape,
    color = MaterialTheme.colorScheme.surface,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    modifier = modifier.minimumInteractiveComponentSize().testTag("copy_all_button"),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Icon(
        imageVector = Icons.Rounded.ContentCopy,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(14.dp),
      )
      Text(
        text = stringResource(R.string.copy_all),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
      )
    }
  }
}

@Composable
private fun categoryAccentColor(category: WordCategory): Color {
  return when (category) {
    WordCategory.VIVID -> MaterialTheme.colorScheme.primary
    WordCategory.NATURE -> MaterialTheme.colorScheme.tertiary
    WordCategory.ACTION -> MaterialTheme.colorScheme.secondary
    WordCategory.ABSTRACT -> MaterialTheme.colorScheme.secondary
    WordCategory.WHIMSY -> MaterialTheme.colorScheme.primary
    WordCategory.ALL -> MaterialTheme.colorScheme.primary
  }
}
