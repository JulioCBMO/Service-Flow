package com.example.serviceflow.repository

import com.example.serviceflow.model.OrdemServico
import com.example.serviceflow.model.User
import com.example.serviceflow.network.ApiService
import com.example.serviceflow.network.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp

class ServiceFlowRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val apiService: ApiService = RetrofitInstance.api

    fun getCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                trySend(null)
                return@AuthStateListener
            }
            firestore.collection("users").document(firebaseUser.uid).get()
                .addOnSuccessListener { doc ->
                    val user = doc.toObject<User>()?.copy(id = firebaseUser.uid)
                        ?: User(id = firebaseUser.uid, email = firebaseUser.email ?: "", nome = firebaseUser.email ?: "")
                    trySend(user)
                }
                .addOnFailureListener { trySend(null) }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun login(email: String, password: String): Result<User> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user ?: throw Exception("Usuário não encontrado")
        val doc = firestore.collection("users").document(firebaseUser.uid).get().await()
        val user = doc.toObject<User>()?.copy(id = firebaseUser.uid) ?: User(id = firebaseUser.uid, email = email, nome = email)
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun logout() {
        auth.signOut()
    }

    fun getTodasOrdens(): Flow<List<OrdemServico>> = callbackFlow {
        val subscription = firestore.collection("ordens")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val ordens = snapshot?.documents?.mapNotNull { it.toObject<OrdemServico>()?.copy(id = it.id) }
                    ?.sortedByDescending { it.dataCriacao } ?: emptyList()
                trySend(ordens)
            }
        awaitClose { subscription.remove() }
    }

    fun getOrdensDoFuncionario(funcionarioId: String): Flow<List<OrdemServico>> = callbackFlow {
        val subscription = firestore.collection("ordens")
            .whereEqualTo("funcionarioId", funcionarioId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val ordens = snapshot?.documents?.mapNotNull { it.toObject<OrdemServico>()?.copy(id = it.id) }
                    ?.sortedByDescending { it.dataCriacao } ?: emptyList()
                trySend(ordens)
            }
        awaitClose { subscription.remove() }
    }

    fun getFuncionarios(): Flow<List<User>> = callbackFlow {
        val subscription = firestore.collection("users")
            .whereEqualTo("tipo", "funcionario")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val users = snapshot?.documents?.mapNotNull { it.toObject<User>()?.copy(id = it.id) } ?: emptyList()
                trySend(users)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun criarOrdem(titulo: String, descricao: String, departamento: String, funcionarioId: String, funcionarioNome: String): Result<Unit> = try {
        val numero = "OS-${System.currentTimeMillis()}"
        val ordem = OrdemServico(
            numero = numero,
            titulo = titulo,
            descricao = descricao,
            dataCriacao = Timestamp.now(),
            departamento = departamento,
            funcionarioId = funcionarioId,
            funcionarioNome = funcionarioNome,
            status = "pendente"
        )
        firestore.collection("ordens").add(ordem).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun concluirOrdem(ordemId: String, realizadoPor: String): Result<Unit> = try {
        val updates = mapOf(
            "status" to "concluida",
            "realizadoPor" to realizadoPor,
            "dataConclusao" to Timestamp.now()
        )
        firestore.collection("ordens").document(ordemId).update(updates).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
}
