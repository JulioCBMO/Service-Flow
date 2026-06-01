package com.example.serviceflow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.serviceflow.model.OrdemServico
import com.example.serviceflow.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OSCard(
    os: OrdemServico,
    onClick: () -> Unit,
    mostrarFuncionario: Boolean = true
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Azul050
                ) {
                    Text(
                        text = os.numero,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Azul500
                    )
                }
                StatusBadge(status = os.status)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = os.titulo,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(2.dp))
            val meta = buildString {
                if (mostrarFuncionario && os.funcionarioNome.isNotBlank()) {
                    append(os.funcionarioNome)
                    append(" · ")
                }
                append(dateFormat.format(os.dataCriacao.toDate()))
            }
            Text(
                text = meta,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (os.departamento.isNotBlank()) {
                Text(
                    text = os.departamento,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bg, fg, label) = when (status) {
        "concluida" -> Triple(Verde050, Verde600, "Concluída")
        else -> Triple(Amber050, Amber600, "Pendente")
    }
    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = fg
        )
    }
}

@Composable
fun FiltroChips(
    opcoes: List<Pair<String, String>>, // value, label
    selecionado: String,
    onSelect: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        opcoes.forEach { (value, label) ->
            FilterChip(
                selected = selecionado == value,
                onClick = { onSelect(value) },
                label = { Text(label, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Azul050,
                    selectedLabelColor = Azul500
                )
            )
        }
    }
}

@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.5.sp,
        modifier = modifier
    )
}
