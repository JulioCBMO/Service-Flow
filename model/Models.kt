package com.example.serviceflow.model

import com.google.firebase.Timestamp

data class User(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val tipo: String = "funcionario",
    val departamento: String = ""
)

data class OrdemServico(
    val id: String = "",
    val numero: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val dataCriacao: Timestamp = Timestamp.now(),
    val departamento: String = "",
    val funcionarioId: String = "",
    val funcionarioNome: String = "",
    val status: String = "pendente",
    val realizadoPor: String = "",
    val dataConclusao: Timestamp? = null
)

enum class StatusOS { PENDENTE, CONCLUIDA }
enum class TipoUsuario { ADMIN, FUNCIONARIO }
