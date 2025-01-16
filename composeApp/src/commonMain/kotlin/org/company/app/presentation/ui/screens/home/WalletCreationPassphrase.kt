package org.company.app.presentation.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bitnero.composeapp.generated.resources.Res
import bitnero.composeapp.generated.resources.check
import bitnero.composeapp.generated.resources.copy
import io.ktor.websocket.Frame
import org.company.app.presentation.ui.components.LoadingBox
import org.jetbrains.compose.resources.painterResource

@Composable
fun WalletCreationPassphrase(
    cryptoMenuItem: CryptoMenuItem,
    modifier: Modifier = Modifier,
    passphrase: List<String>?,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier.height(16.dp))
        Button(
            onClick = {
                passphrase?.joinToString(" ")?.let { AnnotatedString(it) }
                    ?.let { clipboardManager.setText(it) }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = cryptoMenuItem.color)
        ) {
            Icon(
                painter = painterResource(Res.drawable.copy),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = MaterialTheme.colorScheme.surface
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Copy", color = MaterialTheme.colorScheme.surface)
        }
        passphrase?.let {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 32.dp)
            ) {
                items(passphrase) { word ->
                    Text(
                        word,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        } ?: LoadingBox()
        Button(
            onClick = onDismiss,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground),
            modifier = Modifier.size(50.dp),
        ) {
            Icon(
                painter = painterResource(Res.drawable.check),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(modifier.height(32.dp))
    }
}