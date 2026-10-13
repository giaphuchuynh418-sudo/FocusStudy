package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// Light Scheme (Day Focus)
// Glare-free, natural paper-like warmth
// ==========================================
val LightPrimary = Color(0xFF4338CA)               // Rich intellectual indigo
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFE0E7FF)      // Soft lavender blue
val LightOnPrimaryContainer = Color(0xFF1E1B4B)

val LightSecondary = Color(0xFFD97706)             // Warm desk lamp amber
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFFEF3C7)    // Gentle warm cream
val LightOnSecondaryContainer = Color(0xFF78350F)

val LightTertiary = Color(0xFF0D9488)              // Sage / calming teal
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFCCFBF1)
val LightOnTertiaryContainer = Color(0xFF134E4A)

val LightBackground = Color(0xFFF8FAFC)            // Soft off-white
val LightOnBackground = Color(0xFF0F172A)
val LightSurface = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF0F172A)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightOnSurfaceVariant = Color(0xFF475569)

val LightOutline = Color(0xFFCBD5E1)
val LightOutlineVariant = Color(0xFFE2E8F0)
val LightInverseSurface = Color(0xFF1E293B)
val LightInverseOnSurface = Color(0xFFF8FAFC)
val LightInversePrimary = Color(0xFF818CF8)

val LightError = Color(0xFFDC2626)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFEE2E2)
val LightOnErrorContainer = Color(0xFF7F1D1D)

// M3 Surface Containers (Light)
val LightSurfaceDim = Color(0xFFE2E8F0)
val LightSurfaceBright = Color(0xFFFFFFFF)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFF8FAFC)
val LightSurfaceContainer = Color(0xFFF1F5F9)
val LightSurfaceContainerHigh = Color(0xFFE2E8F0)
val LightSurfaceContainerHighest = Color(0xFFCBD5E1)

// ==========================================
// Dark Scheme (Night Study / Low-Eye-Strain)
// Deep obsidian-slate base, soft luminance
// ==========================================
val NightPrimary = Color(0xFF818CF8)               // Soothing periwinkle (not blinding)
val NightOnPrimary = Color(0xFF1E1B4B)
val NightPrimaryContainer = Color(0xFF2E2A72)      // Deep twilight indigo
val NightOnPrimaryContainer = Color(0xFFE0E7FF)

val NightSecondary = Color(0xFFFBBF24)             // Warm incandescent amber glow
val NightOnSecondary = Color(0xFF451A03)
val NightSecondaryContainer = Color(0xFF5A2A04)    // Warm dark amber container
val NightOnSecondaryContainer = Color(0xFFFEF3C7)

val NightTertiary = Color(0xFF2DD4BF)              // Soft night mint/teal
val NightOnTertiary = Color(0xFF042F2E)
val NightTertiaryContainer = Color(0xFF114B44)
val NightOnTertiaryContainer = Color(0xFFCCFBF1)

val NightBackground = Color(0xFF090D16)            // Deep midnight obsidian (gentle on OLED)
val NightOnBackground = Color(0xFFE2E8F0)          // Soft parchment (prevents eye strain)
val NightSurface = Color(0xFF111827)               // Midnight slate surface
val NightOnSurface = Color(0xFFE2E8F0)
val NightSurfaceVariant = Color(0xFF1E293B)        // Muted slate container
val NightOnSurfaceVariant = Color(0xFF94A3B8)      // Warm slate secondary text

val NightOutline = Color(0xFF334155)
val NightOutlineVariant = Color(0xFF1E293B)
val NightInverseSurface = Color(0xFFE2E8F0)
val NightInverseOnSurface = Color(0xFF0F172A)
val NightInversePrimary = Color(0xFF4338CA)

val NightError = Color(0xFFF87171)                 // Soft coral red
val NightOnError = Color(0xFF450A0A)
val NightErrorContainer = Color(0xFF7F1D1D)
val NightOnErrorContainer = Color(0xFFFEE2E2)

// M3 Surface Containers (Night)
val NightSurfaceDim = Color(0xFF090D16)
val NightSurfaceBright = Color(0xFF1E293B)
val NightSurfaceContainerLowest = Color(0xFF060910)
val NightSurfaceContainerLow = Color(0xFF0D121F)
val NightSurfaceContainer = Color(0xFF111827)
val NightSurfaceContainerHigh = Color(0xFF182238)
val NightSurfaceContainerHighest = Color(0xFF222F4C)

// Backward-compatible color aliases
val IndigoPrimary = LightPrimary
val IndigoOnPrimary = LightOnPrimary
val IndigoPrimaryContainer = LightPrimaryContainer
val IndigoOnPrimaryContainer = LightOnPrimaryContainer
val AmberSecondary = LightSecondary
val AmberOnSecondary = LightOnSecondary
val AmberSecondaryContainer = LightSecondaryContainer
val AmberOnSecondaryContainer = LightOnSecondaryContainer
val TealTertiary = LightTertiary
val TealOnTertiary = LightOnTertiary
val TealTertiaryContainer = LightTertiaryContainer
val TealOnTertiaryContainer = LightOnTertiaryContainer
val BackgroundLight = LightBackground
val SurfaceLight = LightSurface
val SurfaceVariantLight = LightSurfaceVariant
val OnSurfaceLight = LightOnSurface
val OnSurfaceVariantLight = LightOnSurfaceVariant
val OutlineLight = LightOutline

val IndigoPrimaryDark = NightPrimary
val IndigoOnPrimaryDark = NightOnPrimary
val IndigoPrimaryContainerDark = NightPrimaryContainer
val IndigoOnPrimaryContainerDark = NightOnPrimaryContainer
val AmberSecondaryDark = NightSecondary
val AmberOnSecondaryDark = NightOnSecondary
val AmberSecondaryContainerDark = NightSecondaryContainer
val AmberOnSecondaryContainerDark = NightOnSecondaryContainer
val TealTertiaryDark = NightTertiary
val TealOnTertiaryDark = NightOnTertiary
val TealTertiaryContainerDark = NightTertiaryContainer
val TealOnTertiaryContainerDark = NightOnTertiaryContainer
val BackgroundDark = NightBackground
val SurfaceDark = NightSurface
val SurfaceVariantDark = NightSurfaceVariant
val OnSurfaceDark = NightOnSurface
val OnSurfaceVariantDark = NightOnSurfaceVariant
val OutlineDark = NightOutline

// Subject Accent Colors
val SubjectBlue = Color(0xFF3B82F6)
val SubjectEmerald = Color(0xFF10B981)
val SubjectPurple = Color(0xFF8B5CF6)
val SubjectRose = Color(0xFFF43F5E)
val SubjectOrange = Color(0xFFF97316)
val SubjectCyan = Color(0xFF06B6D4)
