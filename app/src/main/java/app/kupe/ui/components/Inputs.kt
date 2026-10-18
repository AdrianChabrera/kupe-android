package app.kupe.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import app.kupe.R
import app.kupe.ui.theme.Coral
import app.kupe.ui.theme.ErrorRed
import app.kupe.ui.theme.FieldSurface
import app.kupe.ui.theme.CoralSoft
import app.kupe.ui.theme.Ink

@Composable
fun kupeFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Coral,
    unfocusedBorderColor = CoralSoft,
    errorBorderColor = ErrorRed,
    focusedContainerColor = FieldSurface,
    unfocusedContainerColor = FieldSurface,
    disabledContainerColor = FieldSurface,
    errorContainerColor = FieldSurface,
    focusedTextColor = Ink,
    unfocusedTextColor = Ink,
    cursorColor = Coral,
)

@Composable
fun KupeTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fieldModifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Column(modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Coral,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = fieldModifier.fillMaxWidth(),
            enabled = enabled,
            isError = error != null,
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = kupeFieldColors(),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            supportingText = if (error != null) { { Text(error) } } else null,
        )
    }
}

@Composable
fun KupeSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusManager: FocusManager = LocalFocusManager.current,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = kupeFieldColors(),
        placeholder = { Text(stringResource(R.string.search_hint)) },
        trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Coral) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, autoCorrectEnabled = false),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
    )
}
