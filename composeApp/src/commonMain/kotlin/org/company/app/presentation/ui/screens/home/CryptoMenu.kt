package org.company.app.presentation.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import bitnero.composeapp.generated.resources.Res
import bitnero.composeapp.generated.resources.add
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.company.app.domain.model.crypto.ChartBalance
import org.company.app.domain.model.crypto.CryptoCurrency
import org.company.app.presentation.ui.components.LoadingBox
import org.company.app.presentation.ui.components.chart.MarketChartView
import org.company.app.presentation.ui.components.MultipleModalBottomSheetLayout
import org.company.app.presentation.ui.components.MultipleModalState
import org.company.app.presentation.ui.components.chart.WalletChartView
import org.company.app.presentation.ui.components.chart.WalletEmpty
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoMenu(
    cryptoCurrency: CryptoCurrency,
    viewModel: CryptoMenuViewModel = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    val effectFlow = viewModel.effects.receiveAsFlow()
    val scope = rememberCoroutineScope()

    var passphrase by remember {
        mutableStateOf<List<String>?>(emptyList())
    }

    val walletPassphraseModalBottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val modalBottomSheetStateWalletPassphrase =
        MultipleModalState(
            walletPassphraseModalBottomSheetState
        ) {
            WalletCreationPassphrase(
                cryptoCurrency,
                modifier = Modifier.fillMaxWidth().height(30.dp),
                passphrase
            ) {
                scope.launch {
                    walletPassphraseModalBottomSheetState.hide()
                }
            }
        }

    MultipleModalBottomSheetLayout(
        multipleModalStates = arrayOf(modalBottomSheetStateWalletPassphrase),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Wallet", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                println("state.walletState = ${state.walletState}")
                when (state.walletState) {
                    WalletState.UNKNOWN, WalletState.CREATING -> LoadingBox()
                    WalletState.READY -> {
                        println("state.walletState = ${state.walletState}")
                        if (state.walletBalance != null
                            && state.walletBalance?.toInt() != 0
                            && state.marketPrice != null
                        ) {
                            println("if wallet chart view ")
                            WalletChartView(
                                walletBalance = ChartBalance(
                                    time = Clock.System.now().toEpochMilliseconds(),
                                    marketValue = state.marketPrice!!,
                                    balance = state.walletBalance!!,
                                ),
                                walletChartBalance = state.walletChartBalance,
                                fiatCurrency = state.fiatCurrency,
                                cryptoCurrency = cryptoCurrency
                            )
                        } else {
                            println("if empty wallet")
                            WalletEmpty(fiatCurrency = state.fiatCurrency, cryptoCurrency)
                        }
                    }

                    WalletState.NOT_CREATED -> Button(
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier.size(50.dp),
                        onClick = {
                            viewModel.emitEvent(CryptoMenuEvent.OnCreateWalletClicked)
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.add),
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text("Cours", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                MarketChartView(
                    marketPrice = state.marketPrice,
                    chartPrices = state.marketChartPrices,
                    fiatCurrency = state.fiatCurrency
                )
            }
        }

        LaunchedEffect(effectFlow, state) {
            effectFlow.onEach { effect ->
                when (effect) {
                    is CryptoMenuEffect.ShowCreatedWalletSheet -> {
                        passphrase = effect.mnemonics
                        walletPassphraseModalBottomSheetState.show()
                    }
                }
            }.collect()
        }
    }
}