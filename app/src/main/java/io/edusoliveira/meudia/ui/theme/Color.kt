package io.edusoliveira.meudia.ui.theme

import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Paleta monocromática: preto, prata e branco. Nenhuma cor saturada no app —
// a hierarquia vem só do brilho (branco = destaque, prata = secundário,
// grafite = apoio). Os nomes semânticos antigos continuam existindo para não
// espalhar mudança pelo resto do código, mas todos apontam para esta escala.
// ---------------------------------------------------------------------------

// Escala de pretos (fundo -> superfícies elevadas)
val Black = Color(0xFF000000)
val Ink900 = Color(0xFF0B0B0C)
val Ink800 = Color(0xFF121213)
val Ink700 = Color(0xFF1A1B1C)
val Ink600 = Color(0xFF232426)

// Escala de prata (texto e acentos)
val White = Color(0xFFFFFFFF)
val SilverBright = Color(0xFFE9EBEE)
val Silver = Color(0xFFC3C7CD)
val SilverMuted = Color(0xFF8E9296)
val SilverDark = Color(0xFF63666B)
val SilverDeep = Color(0xFF3B3E42)

// Superfícies do tema escuro (padrão do app)
val DarkBackground = Black
val BgDeep = Black
val BgGlow = Color(0xFF141416)
val DarkSurface = Ink800
val DarkSurfaceElevated = Ink700
val DarkOnSurface = White
val DarkOnSurfaceMuted = SilverMuted

// Superfícies do tema claro (disponível, mas o app abre no escuro)
val LightBackground = Color(0xFFF4F4F5)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceElevated = Color(0xFFEAEAEC)
val LightOnSurface = Color(0xFF111112)
val LightOnSurfaceMuted = Color(0xFF5E6165)

// Acento do app: prata (nomes "Gold" mantidos como alias histórico)
val Gold = Silver
val GoldLight = SilverBright
val GoldGlow = Color(0x24FFFFFF)

// Tons escuros usados como "onPrimary" sobre prata
val Navy = Ink700
val NavyLight = Ink600
val NavyDark = Black

// Bordas em fio de cabelo sobre o preto
val HairlineBorder = Color(0x12FFFFFF)
val HairlineBorderStrong = Color(0x1FFFFFFF)

// Gradiente do card "hero": grafite no topo fechando em preto puro
val HeroGradientStart = Color(0xFF2A2C2F)
val HeroGradientEnd = Black

// Semânticas financeiras — diferenciadas por brilho, não por cor
val IncomeGreen = White
val ExpenseTerracotta = Silver
val PendingAmber = SilverMuted
val Crimson = SilverBright
val CrimsonGlow = Color(0x24FFFFFF)

// Categorias: degraus distintos da escala de prata, para os gráficos ainda
// separarem visualmente uma categoria da outra sem introduzir cor
val CategoryMoradia = Color(0xFFE9EBEE)
val CategoryCartao = Color(0xFFD7DADE)
val CategoryTransporte = Color(0xFFC3C7CD)
val CategoryAssinatura = Color(0xFFAFB3B8)
val CategoryOutro = Color(0xFF9B9FA4)
val CategoryEmprestimo = Color(0xFF878B90)
val CategoryDoacao = Color(0xFF73777C)
val CategoryEducacao = Color(0xFF5F6367)
val CategoryViagem = Color(0xFF4B4E52)

// Blocos de rotina/cronograma (uso futuro, mesma escala monocromática)
val NetColor = Color(0xFFE9EBEE)
val IaColor = Color(0xFFC3C7CD)
val FacColor = Color(0xFF9B9FA4)
val EngColor = Color(0xFF73777C)
val GymColor = Color(0xFF4B4E52)
