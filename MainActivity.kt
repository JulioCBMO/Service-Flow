package com.example.serviceflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.serviceflow.model.OrdemServico
import com.example.serviceflow.model.TipoUsuario
import com.example.serviceflow.ui.screens.*
import com.example.serviceflow.ui.theme.ServiceFlowTheme
import com.example.serviceflow.viewmodel.AdminViewModel
import com.example.serviceflow.viewmodel.AuthViewModel
import com.example.serviceflow.viewmodel.FuncionarioViewModel
import com.example.serviceflow.viewmodel.LoginState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ServiceFlowTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ServiceFlowApp()
                }
            }
        }
    }
}

@Composable
fun ServiceFlowApp() {
    val authViewModel: AuthViewModel = viewModel()
    val currentUser by authViewModel.currentUser.collectAsState()
    val loginState by authViewModel.loginState.collectAsState()
    
    var tipoUsuarioManual by remember { mutableStateOf<TipoUsuario?>(null) }
    var selectedOS by remember { mutableStateOf<OrdemServico?>(null) }

    // Sincroniza o tipo de usuário se o usuário já estiver logado (auto-login)
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            if (tipoUsuarioManual == null) {
                tipoUsuarioManual = if (user.tipo.equals("admin", ignoreCase = true)) {
                    TipoUsuario.ADMIN
                } else {
                    TipoUsuario.FUNCIONARIO
                }
            }
        }
    }

    if (currentUser == null && tipoUsuarioManual == null) {
        LoginScreen(
            viewModel = authViewModel,
            onLoginSuccess = { tipo -> tipoUsuarioManual = tipo }
        )
    } else {
        // Se temos um usuário logado ou acabamos de logar com sucesso
        val user = currentUser
        
        if (user == null || tipoUsuarioManual == null) {
            // Transição: logado mas carregando perfil ou esperando sincronia
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val isAdmin = tipoUsuarioManual == TipoUsuario.ADMIN
            
            if (selectedOS != null) {
                DetalheOSScreen(
                    os = selectedOS!!,
                    isAdmin = isAdmin,
                    viewModel = if (!isAdmin) viewModel<FuncionarioViewModel>() else null,
                    onBack = { selectedOS = null }
                )
            } else {
                if (isAdmin) {
                    AdminDashboardScreen(
                        currentUser = user,
                        viewModel = viewModel(),
                        onOSClick = { os -> selectedOS = os },
                        onLogout = { 
                            authViewModel.logout()
                            tipoUsuarioManual = null
                        }
                    )
                } else {
                    FuncionarioDashboardScreen(
                        currentUser = user,
                        viewModel = viewModel(),
                        onOSClick = { os -> selectedOS = os },
                        onLogout = { 
                            authViewModel.logout()
                            tipoUsuarioManual = null
                        }
                    )
                }
            }
        }
    }
}
