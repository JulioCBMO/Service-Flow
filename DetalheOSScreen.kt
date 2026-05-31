package com.serviceflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serviceflow.model.OrdemServico
import com.serviceflow.ui.components.StatusBadge
import com.serviceflow.ui.theme.Azul050
import com.serviceflow.ui.theme.Azul500
import com.serviceflow.viewmodel.FuncAction
import com.serviceflow.viewmodel.FuncionarioViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheOSScreen(
    os: OrdemServico,
    isAdmin: Boolean,
    viewModel: FuncionarioViewModel?,
    onBack: () -> Unit
) {
    val action by viewModel?.action?.collectAsState() ?: remember { mutableStateOf(FuncAction.Idle) }.let { s -> derivedStateOf { s.value } }.let { s -> remember { s } }.let { s -> s }
    var realizadoPor by remember { mutableStateOf(os.realizadoPor) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

    LaunchedEffect(action) {
        when (action) {
            is FuncAction.OrdemConcluida -> {
                snackbarMessage = "Ordem concluída com sucesso!"
                viewModel?.resetAction()
                onBack()
            }
            is FuncAction.Error -> {
                snackbarMessage = (action as FuncAction.Error).message
                viewModel?.resetAction()
            }
            else -> {}
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(os.numero, fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 12.dp)) {
                        StatusBadge(status = os.status)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Azul500,
                    titleContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header azul com título
            Surface(color = Azul500) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(
                        text = os.titulo,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (os.departamento.isNotBlank()) {
                            Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.2f)) {
                                Text(os.departamento, color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
                            }
                        }
                        Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.2f)) {
                            Text(
                                if (os.status == "pendente") "Pendente" else "Concluída",
                                color = Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Informações
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        InfoRow("Número", os.numero)
                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        InfoRow("Criado em", dateFormat.format(os.dataCriacao.toDate()))
                        if (os.departamento.isNotBlank()) {
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                            InfoRow("Departamento", os.departamento)
                        }
                        if (os.funcionarioNome.isNotBlank()) {
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                            InfoRow("Responsável", os.funcionarioNome)
                        }
                        if (os.dataConclusao != null) {
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                            InfoRow("Concluída em", dateFormat.format(os.dataConclusao.toDate()))
                        }
                    }
                }

                // Descrição
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("DESCRIÇÃO", fontSize = 11.sp, fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp)
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = os.descricao,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Campo "O que foi realizado"
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("O QUE FOI REALIZADO", fontSize = 11.sp, fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp)

                    if (os.status == "concluida") {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = if (os.realizadoPor.isNotBlank()) os.realizadoPor else "—",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    } else if (!isAdmin) {
                        OutlinedTextField(
                            value = realizadoPor,
                            onValueChange = { realizadoPor = it },
                            placeholder = { Text("Descreva o que foi executado...", fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = { viewModel?.concluirOrdem(os.id, realizadoPor) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B6D11))
                        ) {
                            Text("✓  Concluir Ordem de Serviço", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(chave: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(chave, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}
