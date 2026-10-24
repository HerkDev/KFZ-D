package de.herk.kfzd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.activity.compose.BackHandler
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import de.herk.kfzd.R
import de.herk.kfzd.data.model.GeographicalAuthorityType
import de.herk.kfzd.data.model.PlateEntry
import de.herk.kfzd.data.model.PlateType
import de.herk.kfzd.data.matcher.IdentifierMatcher
import de.herk.kfzd.data.matcher.IdentifierInputAdmission
import de.herk.kfzd.data.repository.GeographicalPlateRepository
import de.herk.kfzd.data.repository.InMemoryPlateRepository
import de.herk.kfzd.ui.theme.DkfzTopBar
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

private val TopBarBlue = DkfzTopBar

private const val REFERENCE_CONTENT_WIDTH_PX = 1080f
private const val MIN_CONTENT_SCALE = 0.65f

private data class DkfzScreenMetrics(
    val scale: Float,
) {
    fun dp(value: Float) = (value * scale).dp
    fun sp(value: Float): TextUnit = (value * scale).sp
}

@Composable
private fun rememberDkfzScreenMetrics(maxWidth: Dp): DkfzScreenMetrics {
    val density = LocalDensity.current.density
    val referenceWidthDp = REFERENCE_CONTENT_WIDTH_PX / density
    return remember(maxWidth, density) {
        DkfzScreenMetrics(
            scale = (maxWidth.value / referenceWidthDp)
                .coerceIn(MIN_CONTENT_SCALE, 1f)
        )
    }
}

private enum class InformationPage {
    NONE,
    OVERVIEW,
    MIT_LICENSE,
    THIRD_PARTY_NOTICES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DkfzApp() {
    var query by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<PlateEntry?>(null) }
    val context = LocalContext.current
    val repository = remember(context) { GeographicalPlateRepository(context) }
    val matcher = remember { IdentifierMatcher(repository) }
    val inputAdmission = remember { IdentifierInputAdmission(matcher) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var inputBoundsInRoot by remember { mutableStateOf<Rect?>(null) }
    var scaffoldOriginInRoot by remember { mutableStateOf(Offset.Zero) }
    LaunchedEffect(Unit) {
        if (query.isEmpty()) {
            withFrameNanos { }
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }
    var informationPage by remember { mutableStateOf(InformationPage.NONE) }
    BackHandler(enabled = informationPage != InformationPage.NONE) {
        informationPage = if (informationPage == InformationPage.OVERVIEW) {
            InformationPage.NONE
        } else {
            InformationPage.OVERVIEW
        }
    }

    when (informationPage) {
        InformationPage.OVERVIEW -> {
            InformationScreen(
                onBack = { informationPage = InformationPage.NONE },
                onLicenseClick = { informationPage = InformationPage.MIT_LICENSE },
                onNoticesClick = { informationPage = InformationPage.THIRD_PARTY_NOTICES }
            )
            return
        }
        InformationPage.MIT_LICENSE -> {
            LegalDocumentScreen(
                title = stringResource(R.string.information_license_name),
                resourceId = R.raw.license_mit,
                onBack = { informationPage = InformationPage.OVERVIEW }
            )
            return
        }
        InformationPage.THIRD_PARTY_NOTICES -> {
            LegalDocumentScreen(
                title = stringResource(R.string.information_third_party_notices),
                resourceId = R.raw.third_party_notices,
                onBack = { informationPage = InformationPage.OVERVIEW }
            )
            return
        }
        InformationPage.NONE -> Unit
    }

    val colors = MaterialTheme.colorScheme

    Scaffold(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                val bounds = coordinates.boundsInRoot()
                scaffoldOriginInRoot = Offset(bounds.left, bounds.top)
            }
            .pointerInput(inputBoundsInRoot, scaffoldOriginInRoot) {
                awaitPointerEventScope {
                    var downPosition: Offset? = null
                    while (true) {
                        val change = awaitPointerEvent(PointerEventPass.Final).changes.firstOrNull() ?: continue
                        if (change.pressed) {
                            if (downPosition == null) downPosition = change.position
                        } else if (downPosition != null) {
                            val inputBounds = inputBoundsInRoot?.let { bounds ->
                                Rect(
                                    bounds.left - scaffoldOriginInRoot.x,
                                    bounds.top - scaffoldOriginInRoot.y,
                                    bounds.right - scaffoldOriginInRoot.x,
                                    bounds.bottom - scaffoldOriginInRoot.y
                                )
                            }
                            if (inputBounds?.contains(downPosition!!) != true && inputBounds?.contains(change.position) != true) {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                            downPosition = null
                        }
                    }
                }
            },
        containerColor = colors.background
    ) { contentPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            val metrics = rememberDkfzScreenMetrics(maxWidth)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                HeroHeader(
                    metrics = metrics,
                    onInfoClick = { informationPage = InformationPage.OVERVIEW }
                )
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(colors.background)
                ) {
                    val scrollViewportHeight = maxHeight
                    val surfaceOverlap = metrics.dp(42f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeight(scrollViewportHeight + surfaceOverlap)
                            .offset(y = -surfaceOverlap)
                            .clip(CurvedContentSurfaceShape(scale = metrics.scale))
                            .background(colors.background)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(scrollViewportHeight)
                                .offset(y = metrics.dp(26f))
                                .clip(
                                    CurvedContentSurfaceShape(
                                        scale = metrics.scale,
                                        contentOffsetDp = 26f
                                    )
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(scrollViewportHeight)
                                    .verticalScroll(rememberScrollState())
                                    .padding(
                                        start = metrics.dp(24f),
                                        top = metrics.dp(20f),
                                        end = metrics.dp(24f),
                                        bottom = metrics.dp(32f)
                                    )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .widthIn(max = metrics.dp(400f))
                                        .align(Alignment.CenterHorizontally)
                                ) {
                                    Text(
                                        text = stringResource(R.string.license_plate_input),
                                        fontSize = metrics.sp(16f),
                                        fontWeight = FontWeight.Medium,
                                        color = colors.onSurface,
                                        modifier = Modifier.padding(top = metrics.dp(2f))
                                    )
                                    Spacer(modifier = Modifier.height(metrics.dp(6f)))
                                    OutlinedTextField(
                                        value = query,
                                        onValueChange = { value ->
                                            val candidate = value.uppercase()
                                            if (inputAdmission.accepts(query, candidate)) {
                                                query = candidate
                                                result = repository.findByIdentifier(query)
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(metrics.dp(56f))
                                            .focusRequester(focusRequester)
                                            .onGloballyPositioned { inputBoundsInRoot = it.boundsInRoot() },
                                        singleLine = true,
                                        shape = RoundedCornerShape(metrics.dp(12f)),
                                        trailingIcon = { SearchIcon(metrics) },
                                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                                            fontSize = metrics.sp(18f),
                                            color = colors.onSurface,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = colors.onSurface,
                                            unfocusedTextColor = colors.onSurface,
                                            focusedLabelColor = colors.onSurface,
                                            unfocusedLabelColor = colors.onSurface,
                                            cursorColor = colors.primary,
                                            focusedBorderColor = colors.primary,
                                            unfocusedBorderColor = colors.outline,
                                            focusedContainerColor = colors.surface,
                                            unfocusedContainerColor = colors.surface
                                        ),
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Characters,
                                            keyboardType = KeyboardType.Text,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = { focusManager.clearFocus() }
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(metrics.dp(32f)))
                                    SearchResult(
                                        result = result,
                                        isWaiting = query.isEmpty() || !matcher.match(query).isExact,
                                        metrics = metrics,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = metrics.dp(8f),
                            bottom = metrics.dp(12f)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.legal_notice),
                        textAlign = TextAlign.Center,
                        fontSize = metrics.sp(12f),
                        lineHeight = metrics.sp(12f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.data_status),
                        textAlign = TextAlign.Center,
                        fontSize = metrics.sp(12f),
                        lineHeight = metrics.sp(12f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroHeader(
    metrics: DkfzScreenMetrics,
    onInfoClick: () -> Unit
) {
    val menuDescription = stringResource(R.string.menu_content_description)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(metrics.dp(204f))
    ) {
        Image(
            painter = painterResource(R.drawable.german_landscape_header),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.34f),
                            Color.Black.copy(alpha = 0.10f),
                            Color.Black.copy(alpha = 0.52f)
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(metrics.dp(64f))
        ) {
            IconButton(
                onClick = onInfoClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = metrics.dp(4f), end = metrics.dp(12f))
                    .size(metrics.dp(48f))
                    .semantics { contentDescription = menuDescription }
            ) {
                HamburgerIcon(metrics)
            }
        }
    }
}

private class CurvedContentSurfaceShape(
    private val scale: Float,
    private val contentOffsetDp: Float = 0f,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val waveHeight = with(density) { (55f * scale).dp.toPx() }
        val contentOffset = with(density) { (contentOffsetDp * scale).dp.toPx() }
        fun waveY(fraction: Float) = waveHeight * fraction - contentOffset
        val path = Path().apply {
            moveTo(0f, waveY(0.12f))
            cubicTo(
                size.width * 0.16f,
                waveY(0.04f),
                size.width * 0.34f,
                waveY(0.60f),
                size.width * 0.60f,
                waveY(0.65f),
            )
            cubicTo(
                size.width * 0.76f,
                waveY(0.68f),
                size.width * 0.91f,
                waveY(0.51f),
                size.width,
                waveY(0.30f),
            )
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
private fun HamburgerIcon(metrics: DkfzScreenMetrics) {
    Canvas(modifier = Modifier.size(metrics.dp(24f))) {
        val stroke = metrics.dp(2f).toPx()
        val start = size.width * 0.15f
        val end = size.width * 0.85f
        for (position in listOf(0.25f, 0.5f, 0.75f)) {
            drawLine(Color.White, Offset(start, size.height * position), Offset(end, size.height * position), stroke, StrokeCap.Round)
        }
    }
}

@Composable
private fun SearchIcon(metrics: DkfzScreenMetrics) {
    val searchDescription = stringResource(R.string.search_content_description)
    val iconColor = MaterialTheme.colorScheme.onSurfaceVariant
    Canvas(
        modifier = Modifier
            .size(metrics.dp(24f))
            .semantics { contentDescription = searchDescription }
    ) {
        val stroke = metrics.dp(2f).toPx()
        drawCircle(
            color = iconColor,
            radius = metrics.dp(7f).toPx(),
            center = Offset(metrics.dp(10f).toPx(), metrics.dp(10f).toPx()),
            style = Stroke(width = stroke)
        )
        drawLine(
            color = iconColor,
            start = Offset(metrics.dp(15f).toPx(), metrics.dp(15f).toPx()),
            end = Offset(metrics.dp(20f).toPx(), metrics.dp(20f).toPx()),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun InformationScreen(
    onBack: () -> Unit,
    onLicenseClick: () -> Unit,
    onNoticesClick: () -> Unit
) {
    val context = LocalContext.current
    val versionName = remember(context) {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
    }
    val backDescription = stringResource(R.string.back_content_description)
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(56.dp)
                    .background(TopBarBlue)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .semantics { contentDescription = backDescription }
                ) {
                    Canvas(modifier = Modifier.size(24.dp)) {
                        drawLine(Color.White, Offset(15.dp.toPx(), 4.dp.toPx()), Offset(7.dp.toPx(), 12.dp.toPx()), 2.dp.toPx(), StrokeCap.Round)
                        drawLine(Color.White, Offset(7.dp.toPx(), 12.dp.toPx()), Offset(15.dp.toPx(), 20.dp.toPx()), 2.dp.toPx(), StrokeCap.Round)
                    }
                }
                Text(
                    text = stringResource(R.string.information_title),
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(top = 96.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Text(
                text = stringResource(R.string.application_name),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.developer_label),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.developer_name),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.information_version_label),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = versionName,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.information_data_status_label),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.information_data_status_value),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.legal_notice),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.information_open_source_label),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.information_license_name),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.clickable(onClick = onLicenseClick),
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.information_third_party_notices),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.clickable(onClick = onNoticesClick),
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LegalDocumentScreen(
    title: String,
    resourceId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val document = remember(context, resourceId) {
        context.resources.openRawResource(resourceId).bufferedReader(Charsets.UTF_8).use { it.readText() }
    }
    val backDescription = stringResource(R.string.back_content_description)
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(56.dp)
                    .background(TopBarBlue)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .semantics { contentDescription = backDescription }
                ) {
                    Canvas(modifier = Modifier.size(24.dp)) {
                        drawLine(Color.White, Offset(15.dp.toPx(), 4.dp.toPx()), Offset(7.dp.toPx(), 12.dp.toPx()), 2.dp.toPx(), StrokeCap.Round)
                        drawLine(Color.White, Offset(7.dp.toPx(), 12.dp.toPx()), Offset(15.dp.toPx(), 20.dp.toPx()), 2.dp.toPx(), StrokeCap.Round)
                    }
                }
                Text(
                    text = title,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Text(
                text = document,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun geographicalTypeLabel(type: GeographicalAuthorityType): String = when (type) {
    GeographicalAuthorityType.DISTRICT -> stringResource(R.string.authority_type_district)
    GeographicalAuthorityType.INDEPENDENT_CITY -> stringResource(R.string.authority_type_independent_city)
    GeographicalAuthorityType.CITY -> stringResource(R.string.authority_type_city)
    GeographicalAuthorityType.STATE_CAPITAL -> stringResource(R.string.authority_type_state_capital)
    GeographicalAuthorityType.CITY_STATE -> stringResource(R.string.authority_type_city_state)
    GeographicalAuthorityType.REGION -> stringResource(R.string.authority_type_region)
    GeographicalAuthorityType.CITY_REGION -> stringResource(R.string.authority_type_city_region)
    GeographicalAuthorityType.REGIONAL_ASSOCIATION -> stringResource(R.string.authority_type_regional_association)
    GeographicalAuthorityType.SPECIAL_ASSOCIATION -> stringResource(R.string.authority_type_special_association)
}

@Composable
private fun typeLabel(result: PlateEntry?): String {
    if (result?.type == PlateType.GEOGRAPHICAL && result.authorities.isNotEmpty()) {
        val labels = mutableListOf<String>()
        for (authority in result.authorities) {
            labels += geographicalTypeLabel(authority.authorityType)
        }
        return labels.distinct().joinToString("\n")
    }
    return when (result?.type) {
        PlateType.TECHNICAL_RELIEF, PlateType.FEDERAL_AUTHORITY, PlateType.CONSTITUTIONAL_BODY, PlateType.GOVERNMENT -> stringResource(R.string.federal_authority_type)
        PlateType.FEDERAL_FINANCE_ADMINISTRATION -> stringResource(R.string.federal_finance_administration_type)
        PlateType.FEDERAL_CONSTITUTIONAL_COURT -> stringResource(R.string.federal_constitutional_court_type)
        PlateType.STATE_POLICE -> stringResource(R.string.state_police_type)
        PlateType.MILITARY -> stringResource(R.string.military_type)
        PlateType.DIPLOMATIC, PlateType.DIPLOMATIC_CORPS, PlateType.INTERNATIONAL_ORGANISATION -> stringResource(R.string.diplomatic_type)
        PlateType.GEOGRAPHICAL, null -> stringResource(R.string.geographical_type)
    }
}
@Composable
private fun SearchResult(
    result: PlateEntry?,
    isWaiting: Boolean,
    metrics: DkfzScreenMetrics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(metrics.dp(12f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = metrics.dp(2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = metrics.dp(16f))
        ) {
            ResultSection(
                label = stringResource(R.string.result_authority),
                value = if (isWaiting) stringResource(R.string.no_result) else (result?.authorities?.map { it.name }?.takeIf { it.isNotEmpty() } ?: result?.authorityNames)?.joinToString("\n") ?: stringResource(R.string.no_result_value),
                metrics = metrics
            )
            HorizontalDivider(thickness = metrics.dp(1f), color = MaterialTheme.colorScheme.outlineVariant)
            ResultSection(
                label = stringResource(R.string.result_state),
                value = if (isWaiting) stringResource(R.string.no_result_value) else result?.federalState ?: stringResource(R.string.no_result_value),
                metrics = metrics
            )
            HorizontalDivider(thickness = metrics.dp(1f), color = MaterialTheme.colorScheme.outlineVariant)
            ResultSection(
                label = stringResource(R.string.result_type),
                value = if (isWaiting) stringResource(R.string.no_result_value) else typeLabel(result),
                metrics = metrics
            )
        }
    }
}

@Composable
private fun ResultSection(
    label: String,
    value: String,
    metrics: DkfzScreenMetrics,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = metrics.dp(16f)),
        verticalArrangement = Arrangement.spacedBy(metrics.dp(1f))
    ) {
        Text(
            text = label,
            fontSize = metrics.sp(14f),
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = metrics.sp(19f),
            lineHeight = metrics.sp(19f),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
