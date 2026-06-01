package com.example.serviceflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.serviceflow.model.OrdemServico
import com.example.serviceflow.model.User
import com.example.serviceflow.ui.components.OSCard
import com.example.serviceflow.ui.components.SectionHeader
import com.example.serviceflow.ui.components.FiltroChips
import com.example.serviceflow.ui.theme.Azul500
import com.example.serviceflow.viewmodel.AdminViewModel
import com.example.serviceflow.viewmodel.AdminAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    currentUser: User,
    viewModel: AdminViewModel,
    onOSClick: (OrdemServico) -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val action by viewModel.action.collectAsState()
    var termoBusca by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(action) {
        if (action is AdminAction.OrdemCriada) {
            showCreateDialog = false
            viewModel.resetAction()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Painel Admin") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Sair")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Azul500, titleContentColor = Color.White, actionIconContentColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }, containerColor = Azul500, contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "Nova OS")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(12.dp).fillMaxSize()) {
            OutlinedTextField(
                value = termoBusca,
                onValueChange = { termoBusca = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por título") },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            SectionHeader("Filtrar por status")
            FiltroChips(
                opcoes = listOf("todas" to "Todas", "pendente" to "Pendentes", "concluida" to "Concluídas"),
                selecionado = uiState.filtroStatus,
                onSelect = { viewModel.setFiltroStatus(it) }
            )
            Spacer(Modifier.height(8.dp))
            
            val ordens = viewModel.ordensFiltradas().filter { 
                termoBusca.isBlank() || it.titulo.contains(termoBusca, ignoreCase = true) 
            }
            
            if (uiState.isLoading && ordens.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (ordens.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Nenhuma ordem encontrada.")
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { showCreateDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Criar Ordem de Serviço")
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    items(ordens, key = { it.id }) { os ->
                        OSCard(os = os, onClick = { onOSClick(os) })
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateOSDialog(
            funcionarios = uiState.funcionarios,
            onDismiss = { showCreateDialog = false },
            onConfirm = { titulo, desc, dept, func ->
                viewModel.criarOrdem(titulo, desc, dept, func)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOSDialog(
    funcionarios: List<User>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, User) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }
    var selectedFuncionario by remember { mutableStateOf<User?>(null) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Ordem de Serviço") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = departamento, onValueChange = { departamento = it }, label = { Text("Departamento") }, modifier = Modifier.fillMaxWidth())
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedFuncionario?.nome ?: "Selecionar Funcionário",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Atribuir a") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        funcionarios.forEach { func ->
                            DropdownMenuItem(
                                text = { Text(func.nome) },
                                onClick = {
                                    selectedFuncionario = func
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedFuncionario != null) {
                        onConfirm(titulo, descricao, departamento, selectedFuncionario!!)
                    }
                },
                enabled = titulo.isNotBlank() && selectedFuncionario != null
            ) {
                Text("Criar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
