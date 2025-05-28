package org.company.app.presentation.ui.components.action_menu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bitnero.composeapp.generated.resources.Res
import bitnero.composeapp.generated.resources.add
import bitnero.composeapp.generated.resources.arrow_down
import bitnero.composeapp.generated.resources.arrow_up
import bitnero.composeapp.generated.resources.close
import bitnero.composeapp.generated.resources.exchange
import bitnero.composeapp.generated.resources.minus
import org.company.app.domain.model.crypto.CryptoCurrency
import org.jetbrains.compose.resources.painterResource

@Composable
fun ActionMenu(
    cryptoCurrency: CryptoCurrency,
    modifier: Modifier = Modifier,
    onBuyClick: () -> Unit,
    onSellClick: () -> Unit,
    onSendClick: () -> Unit,
    onReceiveClick: () -> Unit,
    onSwapClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.clickable(onClick = onBuyClick).padding(vertical = 4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.add),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Buy",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.clickable(onClick = onSellClick).padding(vertical = 4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.minus),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Sell",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.clickable(onClick = onSendClick).padding(vertical = 4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.arrow_up),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Send",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.clickable(onClick = onReceiveClick).padding(vertical = 4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.arrow_down),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Receive",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.clickable(onClick = onSwapClick).padding(vertical = 4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.exchange),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Swap",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(64.dp))


        CloseButton(cryptoCurrency) {
            onCloseClick()
        }
    }
}

@Composable
private fun CloseButton(cryptoCurrency: CryptoCurrency, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        label = "scale"
    )

    Box(
        modifier = Modifier.size(110.dp).padding(bottom = 16.dp)
            .scale(scale) // effet d'enfoncement
            //.shadow(elevation, CircleShape, clip = false) // ombre animée
            .clip(CircleShape) // bouton rond
            .background(Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null // supprime le ripple
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            drawCircle(
                color = cryptoCurrency.color,
                radius = size.minDimension / 2
            )
        }
        Icon(
            painter = painterResource(resource = Res.drawable.close),
            contentDescription = "Floating Button",
            tint = MaterialTheme.colorScheme.surface
        )
    }
}