# La Comida del Canco — Especificaciones Funcionales

> Documento independiente de plataforma. Describe el comportamiento, datos y UI completos de la app: la experiencia de usuario original (fiel a la versión Android 2021) y la nueva capa de administración remota.

---

## 1. Visión general

**La Comida del Canco** es una app de planificación de menú diario. El usuario es guiado a través de una serie de preguntas de tipo Sí/No para elegir los ingredientes de su almuerzo o cena del día. La app recuerda qué se eligió la última vez para ofrecer variedad automática: el ingrediente elegido va al final de su lista, de modo que la próxima vez será el último en aparecer.

### Concepto clave: el algoritmo de rotación
Cuando el usuario dice **Sí** a un ingrediente, ese ingrediente se mueve al **último puesto** de su categoría. Así, la próxima vez que entre en esa pantalla, el ingrediente que menos se ha elegido recientemente aparecerá primero. No hay aleatoriedad: es una cola FIFO rotativa basada en elecciones previas.

---

## 2. Modelo de datos

### 2.1 Estructura de un ingrediente

Cada ingrediente se almacena internamente como un string con este formato:

```
"nombre#cantidad.$instruccion_de_preparacion"
```

Ejemplos reales:
```
"arroz integral#140 gramos.$22 minutos."
"lentejas#90 gramos.$11 minutos."
"laurel#3 hojas.$A la olla."
"cacahuetes#50 gramos.$En crudo."
```

En Dart/Flutter esto se modelará como una clase:

```dart
class Ingrediente {
  final String nombre;       // parte antes del '#'
  final String cantidad;     // parte entre '#' y '$'
  final String preparacion;  // parte después del '$'
  final String imagenAsset;  // nombre del asset PNG correspondiente
}
```

> Nota de fidelidad: para una réplica 1:1, los strings originales deben conservarse exactamente (incluyendo mayúsculas/minúsculas, espacios y pequeñas inconsistencias de puntuación), porque el parseo real solo depende de `#` y `$`.

### 2.2 Categorías del Almuerzo

El almuerzo se compone de **4 categorías** que se preguntan en orden fijo:

| Orden | Categoría | Nº de items |
|-------|-----------|-------------|
| 1 | Cereales | 8 |
| 2 | Legumbres | 9 |
| 3 | Hortalizas y tubérculos | 14 |
| 4 | Condimentos (hidratos) | 7 |

#### Cereales (8)
```
arroz integral       → 140 gramos   → 22 minutos
pasta integral       → 110 gramos   → 12 minutos
trigo sarraceno      → 120 gramos   → 18 minutos
quinoa               → 120 gramos   → 9 minutos
amaranto             → 100 gramos   → 10 minutos
arroz blanco         → 150 gramos   → 20 minutos
maiz                 → 150 gramos   → En crudo
mijo                 → 110 gramos   → 9 minutos
```

#### Legumbres (9)
```
soja verde en grano  → 100 gramos   → 8 minutos
azuki                → 50 gramos    → 20 minutos
lentejas             → 90 gramos    → 11 minutos
alubias oscuras      → 50 gramos    → 13 minutos
habas                → 120 gramos   → En crudo
altramuces           → 100 gramos   → 5 minutos
garbanzos            → 70 gramos    → 10 minutos
guisantes            → 120 gramos   → En crudo
cacahuetes           → 50 gramos    → En crudo
```

#### Hortalizas y tubérculos (14)
```
coliflor             → 200 gramos   → 6 minutos
alcachofas           → 200 gramos   → 10 minutos
calabacín            → 250 gramos   → 12 minutos
remolacha            → 200 gramos   → 20 minutos
nabo                 → 200 gramos   → 20 minutos
puerro               → 200 gramos   → 15 minutos
pepino               → 200 gramos   → En crudo
pimiento de cualquier color → 200 gramos → En crudo
berenjena            → 250 gramos   → 15 minutos
batata               → 250 gramos   → 18 minutos
patata               → 250 gramos   → 22 minutos
rábanos              → 200 gramos   → En crudo
chirivía             → 200 gramos   → 20 minutos
calabaza             → 200 gramos   → 17 minutos
```

#### Condimentos del almuerzo (7)
```
laurel               → 3 hojas      → A la olla
hierbabuena          → Media cucharilla → Con el cocimiento
comino (de bote)     → 1/2 cucharilla → En crudo
pimentón dulce (de bote) → 1/2 cucharilla → En crudo
cúrcuma (de bote)    → 1/2 cucharilla → En crudo
jengibre             → 1/2 cucharilla → en la olla
mejorana             → 1/2 cucharilla → En la olla
```

### 2.3 Categorías de la Cena

La cena se compone de **3 categorías** (la proteína tiene una sub-categoría especial):

| Orden | Categoría | Nº de items |
|-------|-----------|-------------|
| 1 | Verduras | 13 |
| 2 | Tipo de proteína | 4 (uno de ellos expande a 9 pescados) |
| 3 | Condimentos cena | 8 |

#### Verduras (13)
```
chukrut              → 200 gramos   → 5 minutos al vapor
judías verdes        → 200 gramos   → 12 minutos al vapor
cardo                → 200 gramos   → 15 minutos al vapor
espárragos verdes    → 200 gramos   → 10 minutos al vapor
coles de Bruselas    → 200 gramos   → 10 minutos al vapor
apio                 → 200 gramos   → 10 minutos al vapor
hinojo               → 200 gramos   → 10 minutos al vapor
borraja              → 200 gramos   → 12 minutos al vapor
ajetes (brotes de ajo) → 200 gramos → 10 minutos al vapor
tallos de acelgas    → 200 gramos   → 10 minutos al vapor
aceitunas            → 120 gramos   → en crudo
brécol               → 200 gramos   → 10 minutos al vapor
grelos               → 200 gramos   → 12 minutos al vapor
```

#### Tipo de proteína (4 tipos, con caso especial "pescado")
```
huevo                → De 3 a 6 claras y 1 yema y media → 15 minutos
pescado              → [ver sub-lista]
champiñones          → 200 gramos   → 10 minutos al vapor
pollo                → 300 gramos o 3 filetes → 23 minutos a fuego lento
```

**Caso especial "pescado":** Cuando el tipo de proteína preguntado es `pescado`, en lugar de preguntar el tipo directamente, la app expande y pregunta por cada pescado de la sub-lista una a una. El pescado elegido se usa como proteína final. Si el usuario rechaza todos los pescados, se pasa al siguiente tipo de proteína.

#### Sub-lista de pescados (9)
```
arenques             → 240g ó 2 latas → No hace falta calentarlo
trucha               → 300 gramos en crudo → 19 minutos a fuego lento
bonito               → 200-300 gramos en crudo → De lata (o 19 minutos fuego lento)
sardinas             → 200-300 gramos → De lata (o 25 minutos fuego lento)
jureles              → 300-500 gramos en crudo → 25 minutos a fuego lento
caballa              → O dos latas, o una entera → De lata (o 19 minutos fuego lento)
atún                 → 3 latas pequeñas → En crudo
salmón               → 300 gramos en crudo → 23 minutos fuego lento
boquerones           → 300-500 gramos en crudo → 20 minutos
```

#### Condimentos de cena (8)
```
vino blanco          → Un chorreoncito bueno → A la sartén
albahaca             → Media cucharilla → A la sartén
levadura de cerveza  → 2 cucharadas → En crudo
cayena               → La punta de una cucharilla → En crudo
orégano              → Media cucharilla → A la sartén
tomillo              → Media cucharilla → A la sartén
curry                → Media cucharilla → En crudo
canela               → Media cucharilla → En crudo
```

### 2.4 Literales canónicos (exactos del código legado)

Para migración fiel, esta sección prevalece sobre cualquier simplificación textual:

```text
ALMUERZO.cereales
arroz integral#140 gramos.$22 minutos.
pasta integral#110 gramos.$12 minutos.
trigo sarraceno#120 gramos.$18 minutos.
quinoa#120 gramos.$9 minutos.
amaranto#100 gramos.$10 minutos.
arroz blanco#150 gramos.$20 minutos.
maiz#150 gramos.$En crudo.
mijo#110 gramos.$9 minutos.

ALMUERZO.legumbres
soja verde en grano#100 gramos.$8 minutos.
azuki#50 gramos.$20 minutos.
lentejas#90 gramos.$11 minutos.
alubias oscuras#50 gramos.$13 minutos.
habas#120 gramos$En crudo.
altramuces#100 gramos.$5 minutos.
garbanzos#70 gramos.$10 minutos.
guisantes#120 gramos.$En crudo.
cacahuetes#50 gramos.$En crudo.

ALMUERZO.hortalizas_tuberculos
coliflor#200 gramos.$6 minutos.
alcachofas#200 gramos.$10 minutos.
calabacín#250 gramos.$12 minutos.
remolacha#200 gramos.$20 minutos.
nabo#200 gramos.$20 minutos.
puerro#200 gramos.$15 minutos.
pepino#200 gramos.$En crudo.
pimiento de cualquier color#200 gramos.$En crudo.
berenjena#250 gramos.$15 minutos.
batata#250 gramos.$18 minutos.
patata#250 gramos.$22 minutos.
rábanos#200 gramos.$En crudo.
chirivía#200 gramos.$20 minutos.
calabaza#200 gramos.$17 minutos.

ALMUERZO.condimento_hidratos
laurel#3 hojas.$A la olla.
hierbabuena#Media cucharilla.$Con el cocimiento
comino (de bote)#1/2 cucharilla.$En crudo.
pimentón dulce (de bote)#1/2 cucharilla.$En crudo.
cúrcuma (de bote)#1/2 cucharilla.$En crudo.
jengibre#1/2cucharilla.$en la olla.
mejorana#1/2 cucharilla.$En la olla.

CENA.verduras
chukrut#200 gramos.$5 minutos al vapor.
judías verdes#200 gramos.$12 minutos al vapor
cardo#200 gramos.$15 minutos al vapor
espárragos verdes#200 gramos.$10 minutos al vapor.
coles de Bruselas#200gramos.$10 minutos al vapor.
apio#200 gramos.$10 minutos al vapor.
hinojo#200 gramos.$10 minutos al vapor.
borraja#200 gramos.$12 minutos al vapor.
ajetes (brotes de ajo)#200 gramos.$10 minutos al vapor.
tallos de acelgas#200 gramos.$10 minutos al vapor.
aceitunas#120 gramos.$en crudo.
brécol#200 gramos.$10 minutos al vapor.
grelos#200 gramos.$12 minutos al vapor.

CENA.tipo_proteina
huevo#De 3 a 6 claras y 1 yema y media.$15 minutos.
pescado# $ 
champiñones#200 gramos.$10 minutos al vapor.
pollo#300 gramos o 3 filetes.$23 minutos a fuego lento.

CENA.pescados
arenques#240g ó 2 latas.$No hace falta calentarlo.
trucha#300 gramos pesados en crudo.$19 minutos a fuego lento.
bonito#De 200 a 300 gramos pesados en crudo.$De lata (o 19 minutos fuego lento).
sardinas#De 200 a 300 gramos.$De lata (o 25 minutos fuego lento).
jureles#De 300 a 500 gramos pesados en crudo.$25 minutos a fuego lento.
caballa#O dos latas, o una caballa entera.$De lata (o 19 minutos fuego lento)
atún#3 latas pequeñas.$En crudo.
salmón#300 gramos pesados en crudo.$23 minutos fuego lento.
boquerones#De 300 a 500 gramos pesados en crudo.$20 minutos.

CENA.condimentos
vino blanco#Un chorreoncito bueno.$A la sartén.
albahaca#Media cucharilla.$A la sartén.
levadura de cerveza#2 cucharadas.$En crudo.
cayena#La punta de una cucharilla.$En crudo.
orégano#Media cucharilla.$A la sartén.
tomillo#Media cucharilla.$A la sartén.
curry#Media cucharilla.$En crudo.
canela#Media cucharilla.$En crudo.
```

---

## 3. Persistencia de datos

### 3.1 Qué se guarda

Se guardan dos tipos de datos:

**A) El orden actual de cada lista** (para mantener la rotación entre sesiones):
- Lista de cereales (8 posiciones, clave: índice numérico como string "0".."7")
- Lista de legumbres (9 posiciones)
- Lista de hortalizas (14 posiciones)
- Lista de condimentos almuerzo (7 posiciones)
- Lista de verduras cena (13 posiciones)
- Lista de tipo de proteínas (4 posiciones)
- Lista de pescados (9 posiciones)
- Lista de condimentos cena (8 posiciones)

**B) El último menú elegido** (para consulta rápida desde el inicio):
- Último almuerzo: cereal elegido, legumbre elegida, hortaliza elegida, condimento elegido (se guardan como strings completos con `#` y `$`)
- Última cena: verdura elegida, proteína elegida (o pescado), condimento elegido

### 3.2 Cuándo se guarda

- **El orden de las listas** se guarda justo antes de navegar a la pantalla de menú final (tanto si el usuario dice Sí al último elemento como si dice No y se agotan todos).
- **El último menú** se guarda al llegar a la pantalla de menú final (en el momento de mostrarla).

### 3.3 Valores vacíos

Si el usuario rechaza todos los ingredientes de una categoría sin elegir ninguno, se asigna un valor vacío con formato:
```
"(No hay cereales)# $ "
"(No hay legumbres)# $ "
"(No hay hortalizas)# $ "
"(No hay condimentos)# $ "
"(No hay verduras)# $ "
"(No hay proteinas)# $ "
```
En la pantalla final, si se detecta este valor, el slot de imagen se muestra transparente/vacío.

### 3.4 Tecnología equivalente en Flutter
`shared_preferences` (paquete oficial). Las claves son idénticas a las del original.

---

## 4. Algoritmo de rotación

Este es el corazón de la app. Se ejecuta en el momento en que el usuario dice **Sí** a un ingrediente.

### Descripción
Dado un array `lista` de tamaño `N` y el índice `i` del elemento elegido:
1. Crear una copia del array de tamaño `N`.
2. Poner `lista[i]` en la **última posición** de la copia.
3. Rellenar las posiciones `0..N-2` de la copia con todos los elementos de `lista` excepto `lista[i]`, manteniendo su orden relativo.
4. Reemplazar `lista` con la copia.

### Ejemplo
```
lista = [A, B, C, D, E]
Usuario elige C (índice 2)
resultado = [A, B, D, E, C]
```

La próxima sesión empezará preguntando por A de nuevo, pero C ya no aparecerá hasta que no se hayan ofrecido A, B, D y E primero.

### Implementación en Dart (referencia)
```dart
List<T> rotarLista<T>(List<T> lista, int indiceElegido) {
  final elegido = lista[indiceElegido];
  final resultado = [
    ...lista.sublist(0, indiceElegido),
    ...lista.sublist(indiceElegido + 1),
    elegido,
  ];
  return resultado;
}
```

---

## 5. Pantallas

### 5.1 Pantalla de inicio (Home)

**Propósito:** Punto de entrada. El usuario elige si va a planificar almuerzo o cena, o consultar el último menú guardado.

**Fondo:** Textura de azulejo (`azulejo.png`)

**Elementos:**
- Título: `"¿Qué comida vas a preparar?"` — texto negro, negrita, 35sp, centrado, margen superior 16dp
- Layout horizontal de 3 columnas de igual peso:
  - **Columna izquierda:** Botón cuadrado 160×160dp con imagen `almuerzo.png`. Acción: navega a Pantalla Selección Almuerzo.
  - **Columna central:** Dos textos apilados verticalmente:
    - `"ver último ALMUERZO"` — marrón `#6F4E00`, negrita+cursiva, auto-size max 25dp. Acción: abre Menú Final Almuerzo con los datos guardados. Si no existe dato guardado, muestra aviso `"aun no hay último almuerzo..."`.
    - `"ver última CENA"` — morado `#680077`, negrita+cursiva, auto-size max 25dp. Acción: abre Menú Final Cena con los datos guardados. Si no existe, aviso `"aun no hay última cena..."`.
  - **Columna derecha:** Botón cuadrado 160×160dp con imagen `cena.png`. Acción: navega a Pantalla Selección Cena.

**Comportamiento especial:** La pantalla mantiene la pantalla encendida (`keepScreenOn`).

---

### 5.2 Pantalla de selección de Almuerzo

**Propósito:** Guiar al usuario preguntando ingrediente a ingrediente en orden de categoría hasta completar el menú.

**Fondo:** Textura de mármol (`marmol.png`)

**Elementos:**
- Texto fijo en la parte superior: `"¿Hay"` — blanco, 32dp, negrita, centrado
- Texto dinámico del ingrediente preguntado (ej: `"arroz integral?"`) — blanco, 35dp, negrita, centrado. Se actualiza con cada pregunta.
- Layout horizontal de 3 columnas de igual peso:
  - **Columna izquierda:** Botón cuadrado 160×160dp con imagen `si.png`. Acción: `clickSi_Almuerzo`.
  - **Columna central:** Imagen del ingrediente actual, `centerInside`, ocupa todo el espacio disponible, con padding 10dp arriba/abajo.
  - **Columna derecha:** Botón cuadrado 160×160dp con imagen `no.png`. Acción: `clickNo_Almuerzo`.

**Flujo de preguntas:**
```
Bloque 1 — CEREALES (índice 0..7):
  → Si Sí: guarda cereal, pasa a Bloque 2, muestra primera legumbre
  → Si No: avanza al siguiente cereal
    → Si se acaban los cereales sin elección: pasa a Bloque 2

Bloque 2 — LEGUMBRES (índice 0..8):
  → Si Sí: guarda legumbre, pasa a Bloque 3, muestra primera hortaliza
  → Si No: avanza a la siguiente legumbre
    → Si se acaban sin elección: pasa a Bloque 3

Bloque 3 — HORTALIZAS/TUBÉRCULOS (índice 0..13):
  → Si Sí: guarda hortaliza, pasa a Bloque 4, muestra primer condimento
  → Si No: avanza a la siguiente hortaliza
    → Si se acaban sin elección: pasa a Bloque 4

Bloque 4 — CONDIMENTOS (índice 0..6):
  → Si Sí: guarda condimento, ejecuta rotaciones, guarda arrays, navega a Menú Final Almuerzo
  → Si No: avanza al siguiente condimento
    → Si se acaban sin elección: ejecuta rotaciones, guarda arrays, navega a Menú Final Almuerzo
```

**Al navegar a Menú Final Almuerzo** se pasan como parámetros: cereal elegido, legumbre elegida, hortaliza elegida, condimento elegido (todos como strings completos con `#` y `$`).

---

### 5.3 Pantalla de selección de Cena

**Propósito:** Igual que la de almuerzo pero para cena, con lógica especial para pescados.

**Fondo:** Mármol (`marmol.png`)

**Elementos:** Idénticos a la pantalla de almuerzo (mismos textos, misma disposición, botones Sí/No, imagen central del ingrediente).

**Flujo de preguntas:**
```
Bloque 1 — VERDURAS (índice 0..12):
  → Si Sí: guarda verdura, pasa a Bloque 2
  → Si No: avanza a la siguiente verdura
    → Si se acaban sin elección: pasa a Bloque 2

Bloque 2 — TIPO DE PROTEÍNA (índice 0..3):
  Caso A — el tipo actual NO es "pescado":
    → Si Sí: guarda proteína (del array tipoProteinas), pasa a Bloque 3
    → Si No: avanza al siguiente tipo
      → Si se acaban: pasa a Bloque 3

  Caso B — el tipo actual ES "pescado":
    La pantalla muestra pescados de la sub-lista (índice 0..8):
    → Si Sí: guarda el pescado específico como proteína e intenta rotar pescados (ver nota de rotación), pasa a Bloque 3
    → Si No al pescado: avanza al siguiente pescado
      → Si se acaban los pescados (índice >= 9): avanza al siguiente tipo de proteína
        → Si se acaban todos los tipos: pasa a Bloque 3

Bloque 3 — CONDIMENTOS CENA (índice 0..7):
  → Si Sí: guarda condimento, ejecuta rotaciones, guarda arrays, navega a Menú Final Cena
  → Si No: avanza al siguiente condimento
    → Si se acaban sin elección: ejecuta rotaciones, guarda arrays, navega a Menú Final Cena
```

**Rotaciones de la cena:**
- Al elegir verdura → rota array verduras
- Al elegir cualquier proteína → rota array tipoProteinas
- Si la proteína elegida fue pescado, el código **intenta** rotar también pescados; por el orden actual de evaluación, esa rotación solo ocurre en casos concretos (cuando tras rotar `tipoProteinas`, en `tipoProteinaPreguntada` sigue quedando `"pescado"`, típicamente cuando ya estaba al final).
- Al elegir condimento → rota array condimentosCena

---

### 5.4 Pantalla de Menú Final Almuerzo

**Propósito:** Mostrar el menú de almuerzo completo con nombre, cantidad, instrucción y foto de cada ingrediente.

**Fondo:** Mantel verde (`mantelverde.png`)

**Estructura (de arriba a abajo):**

**Barra de título/acciones** (horizontal en 3 partes):
- Izquierda: etiqueta `"Este es el ALMUERZO:"` — fondo verde con forma redondeada (`shape_sombra_esteeselalmuerzo`), texto blanco, negrita, auto-size.
- Centro: botón `"ver ultima cena"` — fondo con forma propia (`shape_boton_ultimacena`), altura 40dp. Acción: navega a Menú Final Cena con datos guardados (si existe; si no, aviso).
- Derecha: botón `"hacer otra comida"` — fondo con forma propia (`shape_boton_otracomida`), altura 40dp. Acción: navega a inicio (Home).

**Fila superior de ingredientes** (peso 1, horizontal):
- **Slot Cereal** (peso 1): fondo bandeja (`bandeja.png`), dividido en:
  - Columna izquierda (peso 1): nombre del cereal (negrita, auto-size max 25dp) + cantidad/instrucción (auto-size max 18dp), marcadas con `»` (carácter 187)
  - Columna derecha (peso 1): imagen del cereal
- **Slot Legumbre** (peso 1): misma estructura que el cereal

**Fila inferior de ingredientes** (peso 1, horizontal):
- **Slot Hortaliza** (peso 1): misma estructura
- **Slot Condimento** (peso 1): misma estructura

**Formato del texto de cantidad/instrucción:**
```
» 140 gramos.
» 22 minutos.
```
(dos líneas, cada una precedida por `»`)

**Comportamiento al abrir:**
1. Recibe los 4 ingredientes como parámetros de navegación.
2. Guarda en persistencia: los 4 ingredientes como "último almuerzo".
3. Si un ingrediente es el valor vacío `"(No hay X)# $ "`, muestra su imagen transparente.

---

### 5.5 Pantalla de Menú Final Cena

**Propósito:** Mostrar el menú de cena completo.

**Fondo:** Mesa de madera (`mesamadera.png`)

**Estructura (de arriba a abajo):**

**Barra de título/acciones** (idéntica en layout a la de almuerzo, pero):
- Izquierda: etiqueta `"Esta es la CENA:"` — fondo oscuro (`shape_sombra_estaeslacena`), texto negro.
- Centro: botón `"ver ultimo almuerzo"` — fondo `shape_boton_ultimoalmuerzo`. Acción: navega a Menú Final Almuerzo (si existe; si no, aviso).
- Derecha: botón `"hacer otra comida"` — igual que en almuerzo.

**Fila superior** (peso 1, horizontal):
- **Slot Verdura** (peso 1): fondo mantel amarillo (`mantelamarillo.png`), nombre + cantidad/instrucción + imagen
- **Slot Proteína** (peso 1): fondo mantel amarillo, misma estructura

**Fila inferior** (peso 1):
- Un espacio vacío (peso 1) a la izquierda
- **Slot Condimento** (peso 2, centrado): fondo mantel amarillo, misma estructura
- Un espacio vacío (peso 1) a la derecha

> La cena tiene solo 3 ingredientes, por eso el condimento ocupa el centro de la fila inferior con espacio en blanco a los lados.

**Comportamiento al abrir:** Igual que almuerzo: recibe 3 parámetros y guarda en persistencia como "última cena".

---

### 5.6 Variante de layout para pantallas grandes (`sw600dp`)

Existen layouts alternativos en `layout-sw600dp` para ambas pantallas finales:

- Barra superior con altura fija `70dp` (en lugar de `wrap_content`).
- Botones de acciones con `textSize` mayor (`20sp` vs `15sp`) y `layout_height="match_parent"` con márgenes verticales.
- Textos de ingrediente y cantidad con `autoSizeMaxTextSize` más grande (hasta `50dp`/`40dp` frente a `25dp`/`18dp`).
- Mayor `paddingBottom` en textos de cantidad (`30dp` vs `15dp`).

Para réplica funcional, mantener dos breakpoints de UI: base y `>=600dp` de ancho mínimo.

---

## 6. Mapa de navegación

```
Home
├─→ Selección Almuerzo ──────→ Menú Final Almuerzo
│                                   ├─→ Home (hacer otra comida)
│                                   └─→ Menú Final Cena (ver última cena)
│
├─→ Selección Cena ──────────→ Menú Final Cena
│                                   ├─→ Home (hacer otra comida)
│                                   └─→ Menú Final Almuerzo (ver último almuerzo)
│
├─→ Menú Final Almuerzo (ver último almuerzo, si existe)
└─→ Menú Final Cena (ver última cena, si existe)
```

**Notas de navegación:**
- Flujo primario esperado: Selección → Menú Final → Home.
- En la app Android original, el botón físico/sistema "Atrás" **no está interceptado**; el usuario puede retroceder por el stack de activities.

---

## 7. Catálogo de assets de imágenes

### 7.1 Imágenes de UI (botones e iconos)

| Asset | Uso |
|-------|-----|
| `almuerzo.png` | Botón Home → almuerzo |
| `cena.png` | Botón Home → cena |
| `si.png` | Botón Sí en selección |
| `no.png` | Botón No en selección |
| `tranparente.png` | Placeholder para ingrediente vacío |

> `bandeja_fondo.png` existe en `mipmap-xxxhdpi`, pero los layouts de menú final usan `@drawable/bandeja`.

### 7.2 Fondos de pantalla

| Asset | Pantalla |
|-------|----------|
| `azulejo.png` | Home |
| `marmol.png` | Selección Almuerzo y Cena |
| `mantelverde.png` | Menú Final Almuerzo |
| `mesamadera.png` | Menú Final Cena |
| `mantelamarillo.png` | Slots de la Cena Final |
| `bandeja.png` | Slots del Almuerzo Final |

### 7.3 Imágenes de ingredientes — Almuerzo

#### Cereales
| Ingrediente | Asset |
|-------------|-------|
| arroz integral | `arroz_integral_alimento.png` |
| pasta integral | `pasta_integral_alimento.png` |
| trigo sarraceno | `trigo_sarraceno_alimento.png` |
| quinoa | `quinoa_alimento.png` |
| amaranto | `amaranto_alimento.png` |
| arroz blanco | `arroz_blanco_alimento.png` |
| maiz | `maiz_alimento.png` |
| mijo | `mijo_alimento.png` |

#### Legumbres
| Ingrediente | Asset |
|-------------|-------|
| soja verde en grano | `soja_alimento.png` |
| azuki | `azuki_alimento.png` |
| lentejas | `lentejas_alimento.png` |
| habas | `habas_alimento.png` |
| altramuces | `altramuces_alimento.png` |
| garbanzos | `garbanzos_alimentos.png` |
| guisantes | `guisantes_alimentos.png` |
| alubias oscuras | `alubias_alimento.png` |
| cacahuetes | `cacahuetes_alimentos.png` |

#### Hortalizas y tubérculos
| Ingrediente | Asset |
|-------------|-------|
| coliflor | `coliflor_alimentos.png` |
| alcachofas | `alcachofa_alimentos.png` |
| calabacín | `calabacin.png` |
| remolacha | `remolacha_alimentos.png` |
| nabo | `nabo_alimentos.png` |
| puerro | `puerro_alimentos.png` |
| pepino | `pepino_alimentos.png` |
| pimiento de cualquier color | `pimientos_alimentos.png` |
| berenjena | `berenjena_alimentos.png` |
| batata | `batata_alimentos.png` |
| patata | `patata_alimentos.png` |
| rábanos | `rabano_alimentos.png` |
| chirivía | `chirivia_alimentos.png` |
| calabaza | `calabaza_alimentos.png` |

#### Condimentos del almuerzo
| Ingrediente | Asset |
|-------------|-------|
| laurel | `laurel_alimentos.png` |
| hierbabuena | `hiervabuena_alimentos.png` |
| comino (de bote) | `comino_alimentos.png` |
| pimentón dulce (de bote) | `pimenton_dulce_alimentos.png` |
| cúrcuma (de bote) | `curcuma_alimentos.png` |
| jengibre | `jengibre_alimentos.png` |
| mejorana | `mejorana_alimentos.png` |

### 7.4 Imágenes de ingredientes — Cena

#### Verduras
| Ingrediente | Asset |
|-------------|-------|
| chukrut | `chucrut_alimentos.png` |
| judías verdes | `judias_verdes_alimentos.png` |
| cardo | `cardo_aimentos.png` |
| espárragos verdes | `esparragos_alimentos.png` |
| coles de Bruselas | `coles_de_bruselas_alimentos.png` |
| apio | `apio_alimentos.png` |
| hinojo | `hinojo_alimentos.png` |
| borraja | `borraja_alimentos.png` |
| ajetes (brotes de ajo) | `ajetesalimentos.png` |
| tallos de acelgas | `tallos_acelgas_alimentos.png` |
| aceitunas | `aceitunasalimentos.png` |
| brécol | `brecol_alimentos.png` |
| grelos | `grelos_alimentos.png` |

#### Proteínas (no-pescado)
| Ingrediente | Asset |
|-------------|-------|
| huevo | `huevos_alimentos.png` |
| champiñones | `champinones_alimentos.png` |
| pollo | `pollo_alimentos.png` |

#### Pescados
| Ingrediente | Asset |
|-------------|-------|
| arenques | `arenques_alimentos.png` |
| trucha | `trucha_alimentos.png` |
| bonito | `bonito_alimentos.png` |
| sardinas | `sardinas_alimentos.png` |
| jureles | `jurel_alimentos.png` |
| caballa | `caballa_alimentos.png` |
| atún | `atun_alimentos.png` |
| salmón | `salmon_alimentos.png` |
| boquerones | `boquerones_alimentos.png` |

#### Condimentos de cena
| Ingrediente | Asset |
|-------------|-------|
| vino blanco | `vino_blanco_alimentos.png` |
| albahaca | `albahaca_alimentos.png` |
| levadura de cerveza | `levadura_de_cerveza.png` |
| cayena | `cayena_alimentos.png` |
| orégano | `oregano_alimentos.png` |
| tomillo | `tomillo_alimentos.png` |
| curry | `currry_alimentos.png` |
| canela | `canela_alimentos.png` |

---

## 8. Paleta de colores

| Elemento | Color |
|----------|-------|
| Texto título Home | `#000000` negro |
| Enlace "ver último ALMUERZO" | `#6F4E00` marrón tostado |
| Enlace "ver última CENA" | `#680077` morado |
| Texto en pantallas selección | `#FFFFFF` blanco |
| Texto en menús finales | `#000000` negro |
| Etiqueta "Este es el ALMUERZO" | texto blanco, fondo verde |
| Etiqueta "Esta es la CENA" | texto negro, fondo oscuro |

---

## 9. Comportamiento general

- **Pantalla siempre encendida:** Todas las pantallas de usuario mantienen el display activo mientras están abiertas.
- **Orientación (MVP):** Todas las pantallas (usuario, onboarding y admin) están bloqueadas en horizontal (landscape) para mantener coherencia con la app Android original.
- **Transición de modos:** Al pasar de Home a Dashboard admin (o volver), no hay cambio de orientación ni pantalla intermedia técnica.
- **Botón atrás:** No hay override en las pantallas de usuario; el sistema maneja la navegación hacia atrás.
- **Idioma:** Español exclusivamente.

---

## 10. Resumen de pantallas y estados de navegación (flujo usuario)

| Pantalla | Entrada desde | Sale hacia |
|----------|---------------|------------|
| Home | arranque de la app, botón "hacer otra comida" | Selección Almuerzo, Selección Cena, Menú Final Almuerzo*, Menú Final Cena* |
| Selección Almuerzo | Home | Menú Final Almuerzo |
| Selección Cena | Home | Menú Final Cena |
| Menú Final Almuerzo | Selección Almuerzo, Home*, Menú Final Cena | Home, Menú Final Cena* |
| Menú Final Cena | Selección Cena, Home*, Menú Final Almuerzo | Home, Menú Final Almuerzo* |

*Solo si existe dato guardado en persistencia, si no muestra aviso.

---

---

# PARTE II — Sistema de administración remota

> Todo lo descrito a partir de aquí es nuevo. No existe en la versión Android original.

---

## 11. Arquitectura multi-modo por dispositivo

### 11.1 Filosofía de diseño

La app tiene **una sola codebase Flutter** y una sola cuenta compartida por hogar/grupo. Esa cuenta funciona como contenedor de varios dispositivos (ej: familia), que comparten datos pero pueden tener distinto estado admin por dispositivo.

El principio funcional es:
- `admin = user + extras`.
- El admin **no entra a otra app**: usa la misma Home y el mismo flujo de menús, y además puede abrir panel admin.
- No hay selector visible de rol en la UI principal.

La separación entre uso normal y administración es, en MVP, principalmente de **modo de interfaz por dispositivo**:
- Modo usuario: flujo original simple, sin opciones de admin visibles.
- Modo admin: panel de gestión, activable solo por gesto oculto y solo en dispositivos habilitados.

### 11.2 Tres estados de control por dispositivo (MVP)

Son tres estados completamente distintos. Es fundamental no confundirlos al implementar:

| Estado | Naturaleza | Quién lo controla | Qué significa |
|--------|-----------|-------------------|---------------|
| `adminEnabled` | **Permiso** — cloud, campo en `devices/{deviceId}` en Firestore | Otro device en modo admin (vía callable) | ¿Tiene permiso este device para activar el modo admin? No implica que lo esté usando ahora. |
| `adminModeActive` | **Uso activo** — in-memory, solo dura mientras la app está abierta | El propio device, al completar el gesto | ¿Está el device actualmente ejecutando la UI de admin (dashboard, gestión, etc.)? Se resetea a `false` al cerrar la app. |
| `lastMode` | **Memoria de sesión** — local SharedPreferences, persiste entre aperturas | El propio device, al cambiar de modo | ¿En qué modo terminó el device la última vez que se cerró la app? Se usa al arrancar para restaurar el estado. |

**Regla fundamental:** `adminEnabled` es condición necesaria pero no suficiente para que `adminModeActive` sea `true`. El usuario debe ejecutar el gesto conscientemente. Tener `adminEnabled=true` no activa admin automáticamente, solo habilita el gesto.

Estados resultantes por dispositivo:
- `adminEnabled = false` → solo modo usuario; el gesto no tiene efecto.
- `adminEnabled = true` + `adminModeActive = false` → permiso disponible, pero actualmente en modo usuario.
- `adminEnabled = true` + `adminModeActive = true` → en modo admin activo (gesto ya ejecutado en esta sesión).

En MVP, los nuevos dispositivos de una cuenta se registran con `adminEnabled = true` por defecto, y luego cualquier device en modo admin puede desactivar ese permiso en otros devices.

### 11.3 Flujo de arranque y login

**Flujo de primer arranque:**

```
App se abre por primera vez en un device
    │
    ├─ ¿Hay sesión de cuenta activa (Firebase Auth)?
    │       SÍ → continúa
    │
    └─ NO → muestra login de cuenta
                Dos métodos disponibles:
                  - Google Sign-In (OAuth, cuenta Gmail)
                  - email/contraseña (puede ser una dirección Gmail u otra)
                Login correcto → continúa
                Login incorrecto → mantiene pantalla de login

Continuación:
1) La app obtiene/crea `householdId` para la cuenta.
2) Llama a callable `registerOrHeartbeatDevice(deviceId, alias, platform, appVersion, systemName, model)`.
3) Si el device es nuevo, asigna alias por defecto automáticamente (sin pantalla específica de renombrado de device).
4) Si `onboardingSeen=false`, muestra onboarding de cuenta/devices/modos (una sola vez por device).
5) Al abrir:
   - si `lastMode=admin` y `adminEnabled=true` → entra en modo admin
   - en cualquier otro caso → entra en modo usuario
```

### 11.4 Activación del modo admin por gesto

Desde la Home de usuario existe un gesto oculto simple (MVP) para abrir admin.

**Gesto definido (MVP):** pulsación larga de **2.5 segundos** sobre el título/logo de la Home.

Regla:
1. Se detecta el gesto.
2. La app consulta `devices/{deviceId}.adminEnabled` (campo cloud, ya cacheado por Firestore offline persistence).
3. Si `adminEnabled = true` → muestra `Modal Bottom Sheet` con acción principal `Entrar en modo admin`.
4. Al confirmar: pone `adminModeActive = true` (in-memory), guarda `lastMode = "admin"` (SharedPreferences) y abre Dashboard admin.
5. Si `adminEnabled = false` → el gesto no produce ningún efecto ni feedback visible.

No hay PIN en el MVP. La simplicidad se logra manteniendo la UI oculta y el control remoto por dispositivo.

### 11.5 Gestión remota de admin por dispositivo

Desde modo admin, un dispositivo puede gestionar otros dispositivos de la misma cuenta:
- Ver lista de dispositivos registrados.
- Activar/desactivar `adminEnabled` en cualquier otro dispositivo.
- Los cambios aplican en tiempo real (o en la próxima sincronización del dispositivo objetivo).

Reglas MVP:
1. Un dispositivo **no puede desactivarse a sí mismo** (`self-disable` prohibido).
2. Solo desde panel admin se expone la acción de cambiar `adminEnabled`, y el backend valida además `devices/{requesterDeviceId}.adminEnabled == true`.
3. Si a un dispositivo se le pone `adminEnabled = false`, desde ese momento su gesto deja de abrir admin.
4. La escritura de `adminEnabled` no se hace directa desde cliente: se hace vía callable `setDeviceAdminEnabled(...)` con validación server-side.

### 11.6 Nombre de dispositivo (alias) e identidad

El nombre visible del dispositivo (`alias`) es editable **solo desde modo admin** y **no define identidad**.

Reglas:
1. La identidad técnica del dispositivo es `deviceId` (estable en instalación/sesión), no el alias.
2. `adminEnabled` está ligado a `deviceId`, nunca al alias.
3. El cambio manual de alias solo está disponible desde `Gestión de dispositivos` en modo admin.
4. Un dispositivo con `adminEnabled=false` no tiene acceso a cambio de nombre.
5. Si un admin renombra un dispositivo con `adminEnabled=false`, ese dispositivo sigue `adminEnabled=false` hasta que otro admin lo habilite.
6. En primer registro, el alias por defecto será:
   - nombre del dispositivo del sistema si está disponible
   - fallback: `"<plataforma> <modelo>"` (ej: `"Android Pixel 8"`, `"iOS iPhone"`).
7. Cambiar alias nunca modifica `adminEnabled`.

### 11.7 Identidad de datos: `householdId`

Todos los dispositivos de la misma cuenta trabajan sobre el mismo `householdId`.
- Catálogo, historial y eventos se comparten entre dispositivos de la cuenta.
- La rotación de listas (orden FIFO local) sigue siendo por dispositivo para mantener fidelidad con la app original.

### 11.8 Datos locales exclusivos del dispositivo de usuario

Datos persistidos en almacenamiento local del device (SharedPreferences):

1. `rotationOrder*` (orden FIFO de ingredientes por categoría).
2. `lastMode` (`user` o `admin`).
3. `onboardingSeen` (si el tutorial inicial ya se completó).

Reglas:
1. Estos datos son por device y no se comparten entre dispositivos de la cuenta.
2. `rotationOrder*` no se sincroniza para preservar el comportamiento histórico local.
3. Si el usuario cambia de móvil o reinstala, estos valores se reinician.

### 11.9 Onboarding de cuenta, devices y modos

Objetivo: explicar desde el primer uso cómo funciona la cuenta compartida, qué es un device, y cómo se usa el modo admin sin que aparezcan controles complejos en la UI diaria.

Reglas:
1. Se muestra una sola vez por device tras login correcto (`onboardingSeen=false`).
2. No se puede omitir en MVP; requiere completar las pantallas y pulsar `Entendido`.
3. Al completar, se guarda `onboardingSeen=true`.
4. En aperturas normales ya no se muestra.
5. El onboarding ocurre antes de entrar en Home y no modifica el flujo de elección de comidas.

Contenido mínimo (4 pantallas):
1. `Cuenta y hogar`: una cuenta = un hogar/grupo; varios devices pueden compartir historial y catálogo.
2. `Modo usuario`: uso simple como la app Android original (hacer almuerzo/cena sin menús extra).
3. `Modo admin`: admin = usuario + extras (historial, catálogo, papelera, estadísticas, dispositivos).
4. `Cómo abrir admin`: pulsación larga de 2.5 s sobre logo/título en Home.

Contenido contextual por estado:
1. Si `adminEnabled=true`: explicar que ese device puede abrir admin por gesto.
2. Si `adminEnabled=false`: explicar que en ese device el gesto no abre admin y que solo otro device admin puede habilitarlo.

Casos de uso que se deben explicar en copy:
1. Devices de personas mayores pueden quedarse en modo usuario (sin acceso admin).
2. Devices de gestión familiar pueden mantener admin habilitado.
3. Habilitar/deshabilitar admin se hace por device, no por nombre de device.

---

## 12. Backend — Firebase

### 12.1 Stack elegido

| Servicio | Uso | Coste estimado (uso real) |
|----------|-----|--------------------------|
| Firebase Auth | Login de cuenta única compartida entre devices. Dos métodos soportados: **Google Sign-In** (OAuth, cuenta Gmail) o **email/contraseña** (puede ser también una dirección Gmail). Sesión por device, independiente entre devices. | Esperado dentro de Spark plan para este volumen |
| Cloud Firestore | Historial, catálogo, eventos y estado de dispositivos | Esperado dentro de free tier (50K lecturas/día, 20K escrituras/día) |
| Firebase Storage | Fotos de ingredientes subidas por el admin | Esperado dentro de free tier (5 GB) |
| Firebase Functions (callable) | Bootstrap seguro de hogar, registro de device y cambios sensibles de admin | Uso mínimo; esperado dentro de free tier |

**Conclusión de costes:** Con 1 hogar activo y bajo volumen, el uso esperado encaja holgadamente en el free tier. Aun así, se debe monitorizar mensualmente en Firebase Console.

### 12.2 Estructura de Firestore

```
households/
  {householdId}/
    profile:
      createdAt: Timestamp
      lastOpenedAtAnyDevice: Timestamp
      totalMenusCreated: number
      ownerUid: string

    devices/                         ← control por dispositivo
      {deviceId}/
        alias: string                ← nombre visible editable por usuario
        aliasSource: "auto" | "custom"
        systemName: string | null    ← nombre reportado por SO si disponible
        model: string | null         ← modelo técnico (ej: "Pixel 8", "iPhone15,3")
        adminEnabled: boolean
        adminEnabledUpdatedAt: Timestamp
        adminEnabledUpdatedByDeviceId: string | null
        platform: "android" | "ios" | "web" | "macos" | "windows" | "linux"
        lastSeenAt: Timestamp
        appVersion: string | null

    menus/                           ← subcolección: historial de menús
      {menuId}/
        type: "almuerzo" | "cena"
        createdAt: Timestamp
        createdByMode: "user"         ← en MVP siempre "user"
        createdByDeviceId: string
        ingredients:
          - { categoria, nombre, cantidad, preparacion, imageRef }
        deviceInfo: string            ← opcional, para debug

    appEvents/                       ← subcolección: eventos de uso
      {eventId}/
        event: "app_opened" | "menu_started" | "menu_completed" | "menu_abandoned"
        timestamp: Timestamp
        sourceMode: "user" | "admin"
        sourceDeviceId: string
        metadata: map

    catalogItems/                    ← catálogo editable por admin (por hogar)
      {itemId}/
        mealType: "almuerzo" | "cena"
        category: "cereales" | "legumbres" | "hortalizas" | "condimentos" | "verduras" | "proteinas" | "pescados"
        nombre: string
        cantidad: string
        preparacion: string
        imageRef: string | null       ← path de Storage (no URL pública fija)
        orden: number
        activo: boolean               ← false = en papelera
        deletedAt: Timestamp | null
        createdAt: Timestamp
        updatedAt: Timestamp
```

**Nota importante:** `catalogItems` en Firestore es la fuente de verdad a largo plazo por hogar. Al arrancar la app de usuario, **si tiene conexión**, descarga el catálogo activo y lo cachea localmente. **Si no tiene conexión**, usa el catálogo cacheado (o los datos hardcodeados del primer uso). El orden de rotación sigue siendo local.

### 12.3 Identidad y bootstrap seguro (cuenta única)

**Todos los dispositivos:**
1. Login con la misma cuenta del hogar (Google Sign-In o email/contraseña, según el método elegido al registrar la cuenta).
2. Al iniciar sesión, si no existe `householdId` local:
   - llama a callable `bootstrapAccountHousehold()`
   - la función crea (si hace falta) el hogar para `request.auth.uid`
   - devuelve `householdId` y se guarda localmente.
3. Registro de dispositivo:
   - callable `registerOrHeartbeatDevice(deviceId, alias, platform, appVersion, systemName, model)`.
   - si el device es nuevo, inicia con `adminEnabled = true`.
   - si el device ya existe, actualiza presencia (`lastSeenAt`) y metadatos técnicos (`platform`, `systemName`, `model`, `appVersion`).
   - en devices existentes no se actualiza alias desde heartbeat.
4. Renombrado manual de alias:
   - callable `setDeviceAlias(requesterDeviceId, targetDeviceId, alias)`.
   - valida `devices/{requesterDeviceId}.adminEnabled == true`.
   - solo permite actualizar campos de alias (`alias`, `aliasSource`, timestamps relacionados).
   - no permite modificar `adminEnabled`.
5. Cambios de admin por dispositivo:
   - callable `setDeviceAdminEnabled(requesterDeviceId, targetDeviceId, enabled)`
   - valida que ambos devices pertenezcan al `householdId`
   - valida `requesterDeviceId != targetDeviceId`
   - valida `devices/{requesterDeviceId}.adminEnabled == true`
   - actualiza `adminEnabled` del target y auditoría (`adminEnabledUpdatedAt`, `adminEnabledUpdatedByDeviceId`)
6. Logging de eventos:
   - `app_opened`: se registra en cada apertura con `sourceDeviceId`.
   - `sourceMode` para `app_opened` se define por la pantalla de entrada resuelta:
     - Dashboard admin directo (`lastMode=admin` y `adminEnabled=true`) → `sourceMode="admin"`.
     - Cualquier entrada a Home/flujo normal → `sourceMode="user"`.
   - `menu_started`, `menu_completed` y `menu_abandoned` se registran con `sourceMode="user"` en MVP.
7. Creación de menús:
   - En MVP los menús se generan solo desde flujo usuario.
   - Por eso `menus.createdByMode` se guarda siempre como `"user"`.
   - Se guarda también `createdByDeviceId` para trazabilidad multi-device.
8. Campos derivados del perfil:
   - `totalMenusCreated` se actualiza en backend (Cloud Function trigger `onCreate` de `menus/{menuId}`) con incremento atómico.
   - `lastOpenedAtAnyDevice` se actualiza en backend a partir de eventos `app_opened`.
   - Para vistas por device, se usa `devices/{deviceId}.lastSeenAt` y/o `appEvents` filtrado por `sourceDeviceId`.
   - El cliente no debe escribir estos campos derivados.

Este diseño elimina el pairing y la gestión de claims por rol para el MVP.

### 12.4 Reglas de seguridad Firestore (modelo cerrado)

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    function isSignedIn() {
      return request.auth != null;
    }

    function householdDoc(householdId) {
      return /databases/$(database)/documents/households/$(householdId);
    }

    function isHouseholdOwner(householdId) {
      return isSignedIn() && get(householdDoc(householdId)).data.ownerUid == request.auth.uid;
    }

    function doesNotTouchDerivedProfileFields() {
      return !request.resource.data.diff(resource.data).affectedKeys()
        .hasAny(['totalMenusCreated', 'lastOpenedAtAnyDevice']);
    }

    // Perfil hogar
    match /households/{householdId} {
      allow read: if isHouseholdOwner(householdId);
      allow create, delete: if false; // solo backend/callables
      allow update: if isHouseholdOwner(householdId) && doesNotTouchDerivedProfileFields();
    }

    // Dispositivos
    match /households/{householdId}/devices/{deviceId} {
      allow read: if isHouseholdOwner(householdId);
      // Sin escritura directa desde cliente. Se usan callables:
      // - registerOrHeartbeatDevice
      // - setDeviceAlias
      // - setDeviceAdminEnabled
      allow create, update, delete: if false;
    }

    // Menús históricos
    match /households/{householdId}/menus/{menuId} {
      allow read, write: if isHouseholdOwner(householdId);
    }

    // Eventos de app
    match /households/{householdId}/appEvents/{eventId} {
      allow read, write: if isHouseholdOwner(householdId);
    }

    // Catálogo editable
    match /households/{householdId}/catalogItems/{itemId} {
      allow read, write: if isHouseholdOwner(householdId);
    }
  }
}
```

**Nota MVP:** `adminEnabled` es un campo sensible. Se cambia exclusivamente por callable (`setDeviceAdminEnabled`) y no por escritura directa del cliente. El alias se cambia por callable independiente (`setDeviceAlias`) y requiere requester con `adminEnabled=true`; cambiar alias nunca altera `adminEnabled`. `totalMenusCreated` y `lastOpenedAtAnyDevice` son campos derivados y solo los actualiza backend.

### 12.5 Storage y permisos

- Las fotos se guardan por `householdId` en rutas:
  - `households/{householdId}/ingredients/{itemId}.jpg`
- La cuenta propietaria del hogar puede leer/escribir.
- En la app oficial, solo modo admin muestra acciones de alta/edición/borrado.

### 12.6 App Check

Se habilita Firebase App Check (Android Play Integrity / iOS App Attest o DeviceCheck) para reducir abuso de API y escrituras no legítimas desde clientes no oficiales.

### 12.7 Resolución de `imageRef` en cliente

`imageRef` guarda solo la ruta en Storage (por ejemplo `households/{householdId}/ingredients/{itemId}.jpg`), no una URL pública persistida.

Reglas de resolución:
1. El cliente construye una referencia de Storage con `imageRef`.
2. El cliente obtiene URL temporal de visualización con `getDownloadURL()` del SDK oficial.
3. La URL se usa solo para renderizar (no se guarda de vuelta en Firestore).
4. Si falla la resolución, se muestra placeholder o asset local de respaldo.

---

## 13. Cambios en la experiencia del modo usuario

La experiencia del usuario debe ser **funcionalmente idéntica** a la app original. Solo se permiten cambios de UX que no alteren el flujo ni la apariencia general.

### 13.1 Cambios permitidos

| Elemento | Original | Flutter (mejorado) |
|----------|----------|--------------------|
| Botones Sí/No | Imagen fija 160×160dp | Imagen escalada al 40% del ancho de pantalla, con área táctil mínima de 80×80dp garantizada |
| Texto del ingrediente preguntado | Tamaño fijo 35dp | Auto-size con mínimo 24sp y máximo 40sp según longitud del nombre |
| Fondos | PNG repetido | Misma imagen, usando `BoxFit.cover` |
| Imágenes de ingredientes | `centerInside` con padding fijo | `BoxFit.contain` con padding proporcional a pantalla |
| Feedback táctil | Ninguno en original | Vibración suave (haptic feedback) al pulsar Sí o No |
| Onboarding inicial | No existe | Tutorial breve 1 vez por device para explicar cuenta, devices y acceso admin por gesto |

### 13.2 Lo que NO cambia

- Los colores de los fondos, mantel, azulejo, etc.
- El flujo de preguntas (mismo orden, misma lógica)
- Los textos literales (mismas frases, mismo idioma)
- La navegación (mismo mapa de pantallas)
- La ausencia de menús, configuración o botones extra visibles
- En un dispositivo sin admin habilitado no hay acceso a gestión de dispositivos ni a cambio de nombre de device.

### 13.3 Nuevo comportamiento: logging silencioso

Cada vez que el usuario complete un menú (almuerzo o cena), la app guarda en Firestore (en segundo plano, sin bloquear UI):
- El menú completo con sus ingredientes
- La fecha y hora exacta
- El tipo (`almuerzo` o `cena`)
- `createdByMode = "user"` (MVP)
- `createdByDeviceId = deviceId` actual

Cada vez que la app se abre, guarda un evento `app_opened` con timestamp.

Regla de `sourceMode` en eventos:
1. `app_opened` usa el modo de entrada real del device (`admin` o `user`).
2. Eventos de menú (`menu_started`, `menu_completed`, `menu_abandoned`) usan `sourceMode="user"` en MVP.

Este logging es invisible para el usuario. No hay spinner, no hay aviso, no hay diferencia en la experiencia.

---

## 14. Pantallas del modo administrador

El admin no cambia de aplicación: usa el mismo punto de entrada y flujo de menús que modo usuario, con acceso adicional al panel admin. El idioma sigue siendo español.

### 14.1 Acceso a admin por gesto oculto

**Condición de aparición:** La app ya tiene sesión de cuenta iniciada.

**Comportamiento:**
1. En Home (modo usuario), se ejecuta gesto oculto.
2. Se consulta `adminEnabled` del `deviceId` actual.
3. Si `adminEnabled = true`: abre un `Modal Bottom Sheet` con acción `Entrar en modo admin`.
4. Al confirmar: pone `adminModeActive = true` (in-memory), guarda `lastMode = "admin"` (SharedPreferences) y abre Dashboard admin.
5. Si `adminEnabled = false`: no abre admin, sin feedback visible.

No existe botón visible de "cambiar a admin" para el usuario normal.

**Relación con onboarding:** En primera apertura, el onboarding ya explica este gesto y su disponibilidad según `adminEnabled`.

---

### 14.2 Dashboard admin (pantalla principal del admin)

**Propósito:** Vista rápida del estado del sistema y punto de acceso a todas las funciones.

**Elementos:**
- Cabecera: nombre de la app + botón `Cerrar sesión` (icono, solo visible desde modo admin)
- **Tarjeta de actividad reciente:**
  - Última vez que se abrió la app en **cualquier device** de la cuenta: fecha y hora (desde `profile.lastOpenedAtAnyDevice`)
  - Última vez que se abrió la app en **este device**: fecha y hora (desde `devices/{currentDeviceId}.lastSeenAt` o `appEvents` filtrado por `sourceDeviceId`)
  - Estado de este device: `adminEnabled` (activo/inactivo)
  - Número de menús generados en los últimos 7 días
  - Número de menús generados en total
- **Accesos directos (grid 2×2 o lista):**
  - `Dispositivos` → navega a sección 14.7
  - `Historial de menús` → navega a sección 14.3
  - `Gestionar alimentos` → navega a sección 14.4
  - `Estadísticas` → navega a sección 14.6
  - `Papelera` → navega a sección 14.5
- Datos en tiempo real (Firestore stream, se actualiza solo).

**Comportamiento del botón `Cerrar sesión`:**
- Solo es visible y accesible cuando el device está en modo admin (es decir, `adminModeActive = true`).
- Al pulsar: diálogo de confirmación `"¿Cerrar sesión en este dispositivo?"` con botones `Cancelar` / `Cerrar sesión`.
- Si confirma: cierra la sesión de Firebase Auth **únicamente en este device** (`FirebaseAuth.instance.signOut()` afecta solo al device local).
- Los demás devices de la misma cuenta **no se ven afectados**: cada device mantiene su propia sesión de Firebase Auth de forma independiente.
- Del mismo modo, cuando un device nuevo inicia sesión en la cuenta, no interrumpe ni modifica la sesión del resto de devices.
- Tras el cierre, este device vuelve a la pantalla de login y sus datos locales (`lastMode`, `onboardingSeen`, rotaciones) se conservan para cuando vuelva a iniciar sesión.

---

### 14.3 Historial de menús

**Propósito:** Ver todos los menús que el usuario ha generado, con fecha, hora y detalle de ingredientes.

**Elementos:**
- Lista cronológica inversa (más reciente primero)
- Cada ítem de la lista muestra:
  - Tipo: etiqueta `ALMUERZO` o `CENA` (con color diferenciador)
  - Fecha y hora: `"Lunes 26 mayo 2025, 13:42"`
  - Vista compacta: los nombres de los ingredientes en una línea (`"Quinoa · Lentejas · Calabacín · Cúrcuma"`)
- Al pulsar un ítem: se expande o navega a detalle con:
  - Cada ingrediente en su propio bloque: nombre, cantidad, instrucción de preparación
  - La imagen del ingrediente (del asset local o de Storage si es uno nuevo; si viene de `imageRef`, resolver URL con `getDownloadURL()` según sección 12.7)

**Filtros disponibles:**
- Por tipo (Almuerzo / Cena / Todos)
- Por rango de fechas (selector de fechas)

**Paginación:** Se cargan 20 elementos, con carga adicional al llegar al final de la lista (infinite scroll).

---

### 14.4 Gestión del catálogo de alimentos

**Propósito:** Añadir, editar y eliminar ingredientes de cualquier categoría.

**Estructura de la pantalla:**
- Selector de tipo de comida: `Almuerzo` / `Cena` (tabs o segmented button)
- Selector de categoría: desplegable con las categorías correspondientes
  - Almuerzo: Cereales, Legumbres, Hortalizas y tubérculos, Condimentos
  - Cena: Verduras, Proteínas, Pescados, Condimentos
- Lista de ingredientes activos de la categoría seleccionada
  - Cada ítem muestra: foto (thumbnail) + nombre + cantidad + instrucción
  - Botón de editar (lápiz) y botón de eliminar (papelera) por ítem
- Botón flotante `+` para añadir nuevo ingrediente

**Pantalla / modal de añadir o editar ingrediente:**
- Campo: Nombre del ingrediente (texto libre)
- Campo: Cantidad (texto libre, ej: `"140 gramos"`)
- Campo: Instrucción de preparación (texto libre, ej: `"22 minutos."`)
- Foto:
  - Si es un ingrediente existente con asset local: muestra la imagen actual
  - Botón `"Cambiar foto"` que abre selector de galería o cámara del dispositivo
  - La foto se sube a Firebase Storage en `households/{householdId}/ingredients/{itemId}.jpg`
  - Tras guardar, se persiste solo `imageRef` (path); la visualización se resuelve con `getDownloadURL()` (sección 12.7)
  - Mientras sube: spinner visible, botón guardar desactivado
- Botón `"Guardar"` (desactivado si nombre está vacío)
- Botón `"Cancelar"`

**Regla de negocio:** No se puede tener dos ingredientes con el mismo nombre exacto en la misma categoría.

**Eliminación de un ingrediente:**
- Al pulsar el icono de papelera: diálogo de confirmación `"¿Mover [nombre] a la papelera?"` con botones `Cancelar` / `Mover a papelera`
- Si confirma: el ingrediente pasa a `activo: false` con `deletedAt: now()` en Firestore. No se elimina físicamente del catálogo.
- El ingrediente eliminado desaparece inmediatamente de la lista activa y de la app de usuario en su próxima sincronización.

---

### 14.5 Papelera

**Propósito:** Ver ingredientes eliminados, con opción de recuperarlos o borrarlos definitivamente.

**Elementos:**
- Lista de ingredientes con `activo: false`, ordenados por `deletedAt` (más reciente primero)
- Cada ítem muestra: nombre + categoría + fecha de eliminación (`"Eliminado el 12 may 2025"`)
- Dos acciones por ítem:
  - `Recuperar` → pone `activo: true`, borra `deletedAt`, vuelve a aparecer en el catálogo activo
  - `Eliminar definitivamente` → diálogo de confirmación → borra el documento de Firestore y el archivo de Storage si tenía foto propia (no borra assets locales del app bundle)
- Botón `"Vaciar papelera"` en la cabecera: elimina definitivamente todos los items de la papelera (con confirmación)

---

### 14.6 Estadísticas de uso

**Propósito:** Dar al admin una visión rápida de los patrones de uso de la app.

**Métricas mostradas:**

**Actividad general:**
- Primera vez que se usó la app (fecha)
- Última vez que se abrió la app en cualquier device (fecha y hora; fuente: `profile.lastOpenedAtAnyDevice`)
- Última vez que se abrió la app en este device (fecha y hora, actualizado en tiempo real; fuente: `appEvents` con `event="app_opened"` y `sourceDeviceId=currentDeviceId`, o `devices.lastSeenAt`)
- Total de menús generados (almuerzo + cena separados)

**Uso por device:**
- Lista/ranking de devices por última apertura (`lastSeenAt`)
- Contador de aperturas por device (a partir de `appEvents` con `event="app_opened"` agrupado por `sourceDeviceId`)

**Menús por período:**
- Gráfico de barras semanal: nº de menús por día en los últimos 7 días
- Tabla mensual: nº de menús por semana en los últimos 3 meses

**Ingredientes más elegidos:**
- Top 5 ingredientes más elegidos globalmente
- Desglosado por categoría: el ingrediente más elegido en cada una

**Racha de uso:**
- Días consecutivos con al menos un menú generado (racha actual y máxima histórica)

**Implementación:** Las estadísticas se calculan en cliente haciendo queries a Firestore. Las Cloud Functions se usan para bootstrap de cuenta y registro inicial de dispositivos, no para cálculo de métricas. Para el free tier, el coste esperado de estas lecturas sigue siendo bajo.

---

### 14.7 Gestión de dispositivos (admin)

**Propósito:** Controlar en qué dispositivos de la misma cuenta está permitido abrir modo admin con gesto.

**Elementos:**
- Lista de dispositivos registrados en `households/{householdId}/devices`
  - Incluye **todos los devices** de la cuenta compartida
  - Orden por `lastSeenAt` descendente (más reciente primero)
- Por ítem:
  - Alias del dispositivo
  - Plataforma
  - Última conexión (`lastSeenAt`)
  - Estado `adminEnabled` (activo/inactivo)
- Acciones por ítem:
  - `Editar nombre` (actualiza alias visible del dispositivo)
  - toggle `Permitir modo admin`

**Reglas:**
1. El dispositivo actual no puede desactivarse a sí mismo.
2. Al desactivar otro dispositivo, ese dispositivo deja de poder abrir admin con gesto.
3. La reactivación se hace desde cualquier dispositivo que siga habilitado.
4. Cambiar alias nunca modifica `adminEnabled`.
5. Un dispositivo con `adminEnabled=false` no puede entrar a este panel ni ejecutar cambio de nombre.

---

## 15. Mapa de navegación completo (ambos modos)

```
[PRIMERA APERTURA DE DEVICE]
Login cuenta (Google Sign-In o email/contraseña — por device, independiente)
  └─→ Onboarding (1 vez)
          └─→ Home

[MODO USUARIO]
Home
├─→ Selección Almuerzo ──→ Menú Final Almuerzo
│                               ├─→ Home
│                               └─→ Menú Final Cena*
├─→ Selección Cena ──────→ Menú Final Cena
│                               ├─→ Home
│                               └─→ Menú Final Almuerzo*
├─→ Menú Final Almuerzo* (si existe)
└─→ Menú Final Cena* (si existe)

[MODO ADMIN]
Arranque (si `lastMode=admin` y `adminEnabled=true`)
  └─→ Dashboard
Home (usuario)
  └─→ Gesto oculto (solo si adminEnabled=true) ─→ Dashboard
Dashboard
  ├─→ Dispositivos
  ├─→ Historial de menús
  │       └─→ Detalle de menú
  ├─→ Gestión del catálogo
  │       └─→ Añadir/Editar ingrediente
  ├─→ Papelera
  ├─→ Estadísticas
  └─→ Salir de admin → Home (usuario)
```

---

## 16. Comportamiento offline

| Situación | Modo usuario | Modo admin |
|-----------|----------|-----------|
| Sin conexión, primera apertura en un device nuevo | Si no tiene sesión previa, no puede iniciar cuenta hasta recuperar red. Si ya tiene sesión/caché, funciona con datos locales hardcodeados o cacheados. | Si no existe caché local de `adminEnabled`, no se permite abrir admin. |
| Sin conexión, no primera apertura | Usa catálogo cacheado localmente. Funciona 100%. El log de menús se encola y se envía cuando recupere conexión. | Puede ver datos cacheados (Firestore offline persistence). Los cambios admin quedan pendientes de sincronización. |
| Conexión recuperada | Envía logs encolados. Descarga catálogo actualizado si hay cambios. | Sincroniza automáticamente. |

---

## 17. Decisiones de implementación pendientes (no bloqueantes para empezar)

La arquitectura base ya queda cerrada. Estos puntos se pueden decidir durante la implementación:

1. **Migración de rotación legacy:** si se importan o no los valores de SharedPreferences del Android Java original.
2. **Sincronización de catálogo en caliente:** aplicar cambios admin al instante en una sesión en modo usuario en curso, o en la siguiente apertura (recomendado: siguiente apertura por simplicidad).
3. **Retención histórica:** definir política de conservación (ej: indefinida o purgado opcional >24 meses).
