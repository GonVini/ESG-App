package com.esgapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.esgapp.data.AirQualityRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ESGApp()
            }
        }
    }
}

private object Routes {
    const val Login = "login"
    const val Home = "home"
    const val Simulador = "simulador"
    const val QualidadeAr = "qualidade_ar"
    const val Dicas = "dicas"
}

@Composable
fun ESGApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Login) {
        composable(Routes.Login) { LoginScreen(navController) }
        composable(Routes.Home) { HomeScreen(navController) }
        composable(Routes.Simulador) { SimuladorScreen(navController) }
        composable(Routes.QualidadeAr) { QualidadeArScreen(navController) }
        composable(Routes.Dicas) { DicasScreen(navController) }
    }
}

@Composable
fun LoginScreen(navController: NavHostController) {
    var nome by rememberSaveable { mutableStateOf("") }

    ScreenContainer(title = "Tela 1 - Login") {
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Seu nome") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { navController.navigate(Routes.Home) },
            modifier = Modifier.fillMaxWidth(),
            enabled = nome.isNotBlank()
        ) {
            Text("Entrar no app ESG")
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {
    ScreenContainer(title = "Tela 2 - Início") {
        Text("MVP ESG - Simulador simples para hábitos sustentáveis.")
        Text("Escolha uma funcionalidade:", fontWeight = FontWeight.Bold)

        Button(onClick = { navController.navigate(Routes.Simulador) }, modifier = Modifier.fillMaxWidth()) {
            Text("Simulador de impacto ambiental")
        }
        Button(onClick = { navController.navigate(Routes.QualidadeAr) }, modifier = Modifier.fillMaxWidth()) {
            Text("Consultar qualidade do ar")
        }
        Button(onClick = { navController.navigate(Routes.Dicas) }, modifier = Modifier.fillMaxWidth()) {
            Text("Dicas e metas pessoais")
        }
    }
}

@Composable
fun SimuladorScreen(navController: NavHostController) {
    var kmCarro by rememberSaveable { mutableStateOf("10") }
    var kmOnibus by rememberSaveable { mutableStateOf("5") }

    val emissaoCarro = (kmCarro.toDoubleOrNull() ?: 0.0) * 0.21
    val emissaoOnibus = (kmOnibus.toDoubleOrNull() ?: 0.0) * 0.08
    val total = emissaoCarro + emissaoOnibus

    ScreenContainer(title = "Tela 3 - Simulador") {
        OutlinedTextField(
            value = kmCarro,
            onValueChange = { kmCarro = it },
            label = { Text("Km de carro por dia") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = kmOnibus,
            onValueChange = { kmOnibus = it },
            label = { Text("Km de ônibus por dia") },
            modifier = Modifier.fillMaxWidth()
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Estimativa diária de CO₂")
                Text("Carro: ${"%.2f".format(emissaoCarro)} kg")
                Text("Ônibus: ${"%.2f".format(emissaoOnibus)} kg")
                Text("Total: ${"%.2f".format(total)} kg", fontWeight = FontWeight.Bold)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) {
                Text("Voltar")
            }
            Button(onClick = { navController.navigate(Routes.Dicas) }, modifier = Modifier.weight(1f)) {
                Text("Ver dicas")
            }
        }
    }
}

@Composable
fun QualidadeArScreen(navController: NavHostController) {
    var cidade by rememberSaveable { mutableStateOf("São Paulo") }
    var latitude by rememberSaveable { mutableStateOf("-23.55") }
    var longitude by rememberSaveable { mutableStateOf("-46.63") }
    var pm25 by remember { mutableDoubleStateOf(0.0) }
    var carregando by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf("Clique em atualizar para consumir a API.") }

    val scope = rememberCoroutineScope()

    ScreenContainer(title = "Tela 4 - Qualidade do ar") {
        Text("Serviço: Open-Meteo Air Quality (sem backend próprio)")
        OutlinedTextField(cidade, { cidade = it }, label = { Text("Cidade") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(latitude, { latitude = it }, label = { Text("Latitude") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(longitude, { longitude = it }, label = { Text("Longitude") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                val lat = latitude.toDoubleOrNull() ?: return@Button
                val lon = longitude.toDoubleOrNull() ?: return@Button
                scope.launch {
                    carregando = true
                    mensagem = "Consultando..."
                    val result = AirQualityRepository.fetchPm25(lat, lon)
                    result.onSuccess {
                        pm25 = it
                        mensagem = "PM2.5 atual em $cidade: ${"%.1f".format(it)} µg/m³"
                    }.onFailure {
                        mensagem = "Erro ao consultar: ${it.message}"
                    }
                    carregando = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !carregando
        ) {
            Text(if (carregando) "Carregando..." else "Atualizar indicador")
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(mensagem)
                val risco = when {
                    pm25 <= 12 -> "Bom"
                    pm25 <= 35 -> "Moderado"
                    else -> "Ruim"
                }
                Text("Classificação: $risco")
            }
        }

        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Voltar")
        }
    }
}

@Composable
fun DicasScreen(navController: NavHostController) {
    var meta by rememberSaveable { mutableStateOf("Reduzir 2 km de carro por dia") }

    ScreenContainer(title = "Tela 5 - Dicas e meta") {
        Text("Dicas rápidas ESG (ambiental):")
        Text("• Priorize transporte público ou carona.")
        Text("• Reaproveite água em tarefas domésticas.")
        Text("• Prefira produtos locais e com menos embalagem.")

        OutlinedTextField(
            value = meta,
            onValueChange = { meta = it },
            label = { Text("Sua meta da semana") },
            modifier = Modifier.fillMaxWidth()
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Meta salva: $meta",
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold
            )
        }

        Button(onClick = { navController.navigate(Routes.Home) }, modifier = Modifier.fillMaxWidth()) {
            Text("Voltar ao início")
        }
    }
}

@Composable
fun ScreenContainer(title: String, content: @Composable () -> Unit) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            content()
        }
    }
}
