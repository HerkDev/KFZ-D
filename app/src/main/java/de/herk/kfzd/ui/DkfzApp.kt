package de.herk.kfzd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import de.herk.kfzd.R
import de.herk.kfzd.data.model.GeographicalAuthorityType
import de.herk.kfzd.data.model.PlateEntry
import de.herk.kfzd.data.model.PlateType
import de.herk.kfzd.data.matcher.IdentifierMatcher
import de.herk.kfzd.data.repository.GeographicalPlateRepository
import de.herk.kfzd.data.repository.InMemoryPlateRepository
import de.herk.kfzd.ui.theme.DkfzBackground
import de.herk.kfzd.ui.theme.DkfzDivider
import de.herk.kfzd.ui.theme.DkfzInputBackground
import de.herk.kfzd.ui.theme.DkfzInputBorder
import de.herk.kfzd.ui.theme.DkfzInputFocusedBorder
import de.herk.kfzd.ui.theme.DkfzPrimaryText
import de.herk.kfzd.ui.theme.DkfzSecondaryText
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
private val ScreenBackground = DkfzBackground
private val MainScreenBackground = Color(0xFFF2F5F8)
private val MainContentShape = RoundedCornerShape(12.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DkfzApp() {
    var query by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<PlateEntry?>(null) }
    val context = LocalContext.current
    val repository = remember(context) { GeographicalPlateRepository(context) }
    val matcher = remember { IdentifierMatcher(repository) }
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
    var showInformation by remember { mutableStateOf(false) }
    val menuDescription = stringResource(R.string.menu_content_description)

    if (showInformation) {
        InformationScreen(onBack = { showInformation = false })
        return
    }

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
        containerColor = MainScreenBackground,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(56.dp)
                    .background(TopBarBlue)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 448.dp)
                        .align(androidx.compose.ui.Alignment.Center)
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.align(androidx.compose.ui.Alignment.CenterStart)
                    )
                }
                Image(
                    painter = painterResource(R.drawable.nationality_d),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(width = 72.dp, height = 47.dp)
                )
                IconButton(
                    onClick = { showInformation = true },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .semantics { contentDescription = menuDescription }
                ) {
                    HamburgerIcon()
                }
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 400.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                Text(
                    text = stringResource(R.string.license_plate_input),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = DkfzPrimaryText,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { value ->
                        val candidate = value.uppercase()
                        val canEdit = if (candidate.length <= query.length) {
                            matcher.canAcceptInput(candidate)
                        } else {
                            matcher.canAcceptNextCharacter(query, candidate)
                        }
                        if (canEdit) {
                            query = candidate
                            result = repository.findByIdentifier(query)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .focusRequester(focusRequester)
                        .onGloballyPositioned { inputBoundsInRoot = it.boundsInRoot() },
                    singleLine = true,
                    shape = MainContentShape,
                    trailingIcon = { SearchIcon() },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        color = DkfzPrimaryText,
                        fontWeight = FontWeight.SemiBold
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DkfzPrimaryText,
                        unfocusedTextColor = DkfzPrimaryText,
                        focusedLabelColor = DkfzPrimaryText,
                        unfocusedLabelColor = DkfzPrimaryText,
                        cursorColor = DkfzInputFocusedBorder,
                        focusedBorderColor = DkfzInputFocusedBorder,
                        unfocusedBorderColor = DkfzInputBorder,
                        focusedContainerColor = DkfzInputBackground,
                        unfocusedContainerColor = DkfzInputBackground
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
                Spacer(modifier = Modifier.height(32.dp))
                    SearchResult(result = result, isWaiting = query.isEmpty() || !matcher.match(query).isExact, modifier = Modifier.fillMaxWidth())
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Text(
                    text = stringResource(R.string.legal_notice),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = DkfzSecondaryText
                )
                Text(
                    text = stringResource(R.string.data_status),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = DkfzSecondaryText
                )
            }
        }
    }
}

@Composable
private fun HamburgerIcon() {
    Canvas(modifier = Modifier.size(24.dp)) {
        val stroke = 2.dp.toPx()
        val start = size.width * 0.15f
        val end = size.width * 0.85f
        for (position in listOf(0.25f, 0.5f, 0.75f)) {
            drawLine(Color.White, Offset(start, size.height * position), Offset(end, size.height * position), stroke, StrokeCap.Round)
        }
    }
}

@Composable
private fun SearchIcon() {
    val searchDescription = stringResource(R.string.search_content_description)
    Canvas(
        modifier = Modifier
            .size(24.dp)
            .semantics { contentDescription = searchDescription }
    ) {
        val stroke = 2.dp.toPx()
        drawCircle(
            color = DkfzSecondaryText,
            radius = 7.dp.toPx(),
            center = Offset(10.dp.toPx(), 10.dp.toPx()),
            style = Stroke(width = stroke)
        )
        drawLine(
            color = DkfzSecondaryText,
            start = Offset(15.dp.toPx(), 15.dp.toPx()),
            end = Offset(20.dp.toPx(), 20.dp.toPx()),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun InformationScreen(onBack: () -> Unit) {
    val backDescription = stringResource(R.string.back_content_description)
    Scaffold(
        containerColor = MainScreenBackground,
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
                color = DkfzPrimaryText,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.developer_label),
                color = DkfzSecondaryText,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.developer_name),
                color = DkfzPrimaryText,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.information_version_label),
                color = DkfzSecondaryText,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.information_version_value),
                color = DkfzPrimaryText,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.information_data_status_label),
                color = DkfzSecondaryText,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.information_data_status_value),
                color = DkfzPrimaryText,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.legal_notice),
                color = DkfzSecondaryText,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.information_open_source_label),
                color = DkfzSecondaryText,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.information_license_name),
                color = DkfzPrimaryText,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.information_third_party_notices),
                color = DkfzPrimaryText,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MainContentShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            ResultSection(
                label = stringResource(R.string.result_authority),
                value = if (isWaiting) stringResource(R.string.no_result_value) else (result?.authorities?.map { it.name }?.takeIf { it.isNotEmpty() } ?: result?.authorityNames)?.joinToString("\n") ?: stringResource(R.string.no_result_value)
            )
            HorizontalDivider(thickness = 1.dp, color = DkfzDivider)
            ResultSection(
                label = stringResource(R.string.result_state),
                value = if (isWaiting) stringResource(R.string.no_result_value) else result?.federalState ?: stringResource(R.string.no_result_value)
            )
            HorizontalDivider(thickness = 1.dp, color = DkfzDivider)
            ResultSection(
                label = stringResource(R.string.result_type),
                value = if (isWaiting) stringResource(R.string.no_result_value) else typeLabel(result)
            )
        }
    }
}

@Composable
private fun ResultSection(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DkfzSecondaryText
        )
        Text(
            text = value,
            fontSize = 19.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.SemiBold,
            color = DkfzPrimaryText
        )
    }
}
