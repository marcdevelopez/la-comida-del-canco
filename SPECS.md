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
- **Orientación:** Todas las pantallas de usuario están bloqueadas en horizontal (landscape). Las pantallas de admin pueden ser portrait o libre.
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

## 11. Arquitectura multi-rol

### 11.1 Filosofía de diseño

La app tiene **una sola codebase Flutter**. Al abrirse, lee el rol almacenado localmente en el dispositivo y muestra una experiencia completamente diferente según ese rol. El usuario no-admin nunca ve ningún elemento de la interfaz de admin. El admin nunca ve la interfaz simplificada de usuario.

La separación de roles es de **UX** y también de **seguridad de backend**:
- UX: cada dispositivo recuerda su rol y no vuelve a preguntarlo.
- Backend: todo acceso a datos se valida con Firebase Auth + reglas Firestore por membresía.

### 11.2 Los dos roles

| Rol | Dispositivo | Experiencia | Autenticación |
|-----|-------------|-------------|---------------|
| `user` | Dispositivo del usuario | Idéntica a la app original con mejoras menores de UX | **Firebase Auth anónimo silencioso** (sin pantalla de login) |
| `admin` | Móvil del desarrollador (u otro device) | Panel completo de gestión e historial | Login con email/contraseña (Firebase Auth) + claim `admin=true` |

### 11.3 Asignación de rol en el dispositivo

El rol se guarda en **SharedPreferences local** al primer uso. Una vez asignado, **nunca se pregunta de nuevo**.

**Flujo de primer arranque:**

```
App se abre por primera vez
    │
    ├─ ¿Hay rol guardado localmente?
    │       SÍ → entra directamente en la experiencia del rol guardado
    │
    └─ NO → ¿Se ha activado el gesto secreto de configuración?
                │
                ├─ NO → entra como "user" (comportamiento por defecto)
                │       Guarda rol "user" localmente
                │       y hace signInAnonymously() en segundo plano
                │
                └─ SÍ → muestra pantalla de login admin
                            Login correcto → guarda rol "admin" localmente
                            Login incorrecto → vuelve a la pantalla de login
```

**Gesto secreto de configuración:** Pulsar el título/logo de la pantalla de inicio **7 veces seguidas en menos de 4 segundos**. Este gesto es prácticamente imposible de activar por accidente. Solo lo conoce el desarrollador.

**Por qué el defecto es "user" y no "elegir rol":** Si el usuario instala una actualización o reinstala la app desde cero, sin hacer el gesto, la app arranca como usuario. Nunca aparecerá un modal de selección de rol que lo confunda.

### 11.4 Identidad compartida: `householdId` (antes `userId`)

Ambos roles leen y escriben en el mismo proyecto Firebase, bajo un identificador compartido de hogar: `householdId`.

- El dispositivo `user` crea (o recupera) su `householdId`.
- El dispositivo `admin` se **vincula** a ese `householdId` una sola vez.
- Desde ese momento, el admin ve en tiempo real los menús y eventos de ese hogar.

`householdId` no depende de una ruta editable por cliente ni de un UUID “fiado”; siempre se valida por membresía autenticada en Firestore.

### 11.5 Vinculación admin ↔ user (pairing)

La vinculación entre dispositivos se realiza con **código de emparejamiento de un solo uso** (y opcionalmente QR con el mismo código).

Flujo:
1. En el dispositivo `user`, el desarrollador activa un gesto oculto de mantenimiento.
2. Se genera un código temporal (ej: 6 dígitos) válido 10 minutos.
3. En el dispositivo `admin`, tras login, se introduce o escanea ese código.
4. El backend valida el código y crea la membresía admin en ese `householdId`.
5. Desde entonces, no hace falta repetir pairing salvo logout/borrado de app.

El usuario final nunca ve esta UI en uso normal.

### 11.6 Datos locales exclusivos del dispositivo de usuario

El **orden de rotación de las listas** (qué ingrediente aparece primero) se guarda localmente en el dispositivo del usuario. Es el único dato que no se sincroniza en la nube, por dos motivos:

1. No necesita sincronizarse: solo lo usa un dispositivo.
2. Evita escrituras en Firestore en cada menú generado.

Si el usuario cambia de móvil, el orden de rotación se reinicia a los valores por defecto (mismo comportamiento que la app original en su primer uso).

---

## 12. Backend — Firebase

### 12.1 Stack elegido

| Servicio | Uso | Coste estimado (uso real) |
|----------|-----|--------------------------|
| Firebase Auth | Sesión anónima silenciosa del rol `user` + login del rol `admin` | Esperado dentro de Spark plan para este volumen |
| Cloud Firestore | Historial, catálogo de alimentos, eventos de uso, membresías | Esperado dentro de free tier (50K lecturas/día, 20K escrituras/día) |
| Firebase Storage | Fotos de ingredientes subidas por el admin | Esperado dentro de free tier (5 GB) |
| Firebase Functions (callable) | Bootstrap seguro de hogar + emparejamiento admin | Uso mínimo; esperado dentro de free tier |

**Conclusión de costes:** Con 1 hogar activo y bajo volumen, el uso esperado encaja holgadamente en el free tier. Aun así, se debe monitorizar mensualmente en Firebase Console.

### 12.2 Estructura de Firestore

```
households/
  {householdId}/
    profile:
      createdAt: Timestamp
      lastOpenedAt: Timestamp
      totalMenusCreated: number

    members/                         ← subcolección (control de acceso)
      {authUid}/
        role: "user" | "admin"
        createdAt: Timestamp
        active: boolean

    devices/                         ← opcional para auditoría técnica
      {deviceId}/
        roleHint: "user" | "admin"
        lastSeenAt: Timestamp
        platform: "android" | "ios"

    menus/                           ← subcolección: historial de menús
      {menuId}/
        type: "almuerzo" | "cena"
        createdAt: Timestamp
        createdByRole: "user" | "admin"
        ingredients:
          - { categoria, nombre, cantidad, preparacion, imageRef }
        deviceInfo: string            ← opcional, para debug

    appEvents/                       ← subcolección: eventos de uso
      {eventId}/
        event: "app_opened" | "menu_started" | "menu_completed" | "menu_abandoned"
        timestamp: Timestamp
        sourceRole: "user" | "admin"
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

pairingTokens/
  {tokenId}/                         ← documento efímero (TTL)
    householdId: string
    createdByUid: string
    createdAt: Timestamp
    expiresAt: Timestamp
    consumedAt: Timestamp | null
    consumedByUid: string | null
```

**Nota importante:** `catalogItems` en Firestore es la fuente de verdad a largo plazo por hogar. Al arrancar la app de usuario, **si tiene conexión**, descarga el catálogo activo y lo cachea localmente. **Si no tiene conexión**, usa el catálogo cacheado (o los datos hardcodeados del primer uso). El orden de rotación sigue siendo local.

### 12.3 Identidad y bootstrap seguro

**Dispositivo user:**
1. `signInAnonymously()` en segundo plano (sin UI de login).
2. Si no existe `householdId` local:
   - llama a callable `bootstrapUserHousehold()`
   - la función crea `householdId` + `members/{authUid, role:user}` atómicamente
   - devuelve `householdId` y se guarda localmente.

**Dispositivo admin:**
1. Login email/contraseña.
2. Debe tener claim `admin=true` (asignado previamente desde Firebase Admin SDK/Console por el desarrollador).
3. Si no está vinculado a ningún hogar, usa pairing:
   - introduce/escanea código temporal
   - callable `pairAdminToHousehold(token)` valida token y crea `members/{adminUid, role:admin}`.

Este diseño evita depender de un UUID local no autenticado para seguridad.

### 12.4 Reglas de seguridad Firestore (modelo cerrado)

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    function isSignedIn() {
      return request.auth != null;
    }

    function isAdminClaim() {
      return isSignedIn() && request.auth.token.admin == true;
    }

    function memberDoc(householdId) {
      return /databases/$(database)/documents/households/$(householdId)/members/$(request.auth.uid);
    }

    function isHouseholdMember(householdId) {
      return isSignedIn() && exists(memberDoc(householdId));
    }

    function isHouseholdAdmin(householdId) {
      return isHouseholdMember(householdId) && get(memberDoc(householdId)).data.role == "admin";
    }

    // Perfil hogar
    match /households/{householdId} {
      allow read: if isHouseholdMember(householdId);
      allow create, delete: if false; // solo backend/callables
      allow update: if isHouseholdMember(householdId);
    }

    // Membresías (solo backend/callables)
    match /households/{householdId}/members/{uid} {
      allow read: if isHouseholdMember(householdId);
      allow create, update, delete: if false;
    }

    // Menús históricos
    match /households/{householdId}/menus/{menuId} {
      allow read: if isHouseholdMember(householdId);
      allow create: if isHouseholdMember(householdId);
      allow update, delete: if isHouseholdAdmin(householdId);
    }

    // Eventos de app
    match /households/{householdId}/appEvents/{eventId} {
      allow read: if isHouseholdMember(householdId);
      allow create: if isHouseholdMember(householdId);
      allow update, delete: if false;
    }

    // Catálogo editable
    match /households/{householdId}/catalogItems/{itemId} {
      allow read: if isHouseholdMember(householdId);
      allow write: if isHouseholdAdmin(householdId);
    }

    // Tokens de pairing: solo backend/callables
    match /pairingTokens/{tokenId} {
      allow read, write: if false;
    }
  }
}
```

### 12.5 Storage y permisos

- Las fotos se guardan por `householdId` en rutas:
  - `households/{householdId}/ingredients/{itemId}.jpg`
- Solo admin del hogar puede subir/reemplazar/borrar.
- El rol user solo puede leer mediante URL firmada o reglas de lectura por membresía (según estrategia elegida en implementación).

### 12.6 App Check

Se habilita Firebase App Check (Android Play Integrity / iOS App Attest o DeviceCheck) para reducir abuso de API y escrituras no legítimas desde clientes no oficiales.

---

## 13. Cambios en la experiencia del usuario (rol `user`)

La experiencia del usuario debe ser **funcionalmente idéntica** a la app original. Solo se permiten cambios de UX que no alteren el flujo ni la apariencia general.

### 13.1 Cambios permitidos

| Elemento | Original | Flutter (mejorado) |
|----------|----------|--------------------|
| Botones Sí/No | Imagen fija 160×160dp | Imagen escalada al 40% del ancho de pantalla, con área táctil mínima de 80×80dp garantizada |
| Texto del ingrediente preguntado | Tamaño fijo 35dp | Auto-size con mínimo 24sp y máximo 40sp según longitud del nombre |
| Fondos | PNG repetido | Misma imagen, usando `BoxFit.cover` |
| Imágenes de ingredientes | `centerInside` con padding fijo | `BoxFit.contain` con padding proporcional a pantalla |
| Feedback táctil | Ninguno en original | Vibración suave (haptic feedback) al pulsar Sí o No |

### 13.2 Lo que NO cambia

- Los colores de los fondos, mantel, azulejo, etc.
- El flujo de preguntas (mismo orden, misma lógica)
- Los textos literales (mismas frases, mismo idioma)
- La navegación (mismo mapa de pantallas)
- La ausencia de menús, configuración o botones extra visibles

### 13.3 Nuevo comportamiento: logging silencioso

Cada vez que el usuario complete un menú (almuerzo o cena), la app guarda en Firestore (en segundo plano, sin bloquear UI):
- El menú completo con sus ingredientes
- La fecha y hora exacta
- El tipo (`almuerzo` o `cena`)

Cada vez que la app se abre, guarda un evento `app_opened` con timestamp.

Este logging es invisible para el usuario. No hay spinner, no hay aviso, no hay diferencia en la experiencia.

---

## 14. Pantallas del administrador (rol `admin`)

El admin entra a una app visualmente diferente: moderna, con más información, orientación libre. El idioma sigue siendo español.

### 14.1 Pantalla de login admin

**Condición de aparición:** Solo cuando el rol guardado es `admin` y no hay sesión activa en Firebase Auth.

**Elementos:**
- Título: `"La Comida del Canco — Admin"`
- Campo email
- Campo contraseña (con toggle mostrar/ocultar)
- Botón `"Entrar"`
- Mensaje de error si login falla (sin revelar si el email existe o no)

**Comportamiento:** Si el login es correcto, Firebase Auth guarda la sesión.

Tras el login:
- Si la cuenta admin ya está vinculada a un `householdId` → entra al Dashboard.
- Si no está vinculada → abre flujo de **vinculación por código/QR** y, al completarlo, entra al Dashboard.

Las siguientes aperturas van directamente al Dashboard sin pasar por login (hasta que la sesión expire o se haga logout explícito).

---

### 14.2 Dashboard admin (pantalla principal del admin)

**Propósito:** Vista rápida del estado del sistema y punto de acceso a todas las funciones.

**Elementos:**
- Cabecera: nombre de la app + botón de logout (icono)
- **Tarjeta de actividad reciente:**
  - Última vez que se abrió la app de usuario: fecha y hora
  - Número de menús generados en los últimos 7 días
  - Número de menús generados en total
- **Accesos directos (grid 2×2 o lista):**
  - `Historial de menús` → navega a sección 14.3
  - `Gestionar alimentos` → navega a sección 14.4
  - `Estadísticas` → navega a sección 14.6
  - `Papelera` → navega a sección 14.5
- Datos en tiempo real (Firestore stream, se actualiza solo).

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
  - La imagen del ingrediente (del asset local o de Storage si es uno nuevo)

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
- Última vez que se abrió la app (fecha y hora, actualizado en tiempo real)
- Total de menús generados (almuerzo + cena separados)

**Menús por período:**
- Gráfico de barras semanal: nº de menús por día en los últimos 7 días
- Tabla mensual: nº de menús por semana en los últimos 3 meses

**Ingredientes más elegidos:**
- Top 5 ingredientes más elegidos globalmente
- Desglosado por categoría: el ingrediente más elegido en cada una

**Racha de uso:**
- Días consecutivos con al menos un menú generado (racha actual y máxima histórica)

**Implementación:** Las estadísticas se calculan en cliente haciendo queries a Firestore. Las Cloud Functions se usan solo para bootstrap/pairing, no para cálculo de métricas. Para el free tier, el coste esperado de estas lecturas sigue siendo bajo.

---

## 15. Mapa de navegación completo (ambos roles)

```
[ROL USER]
Home
├─→ Selección Almuerzo ──→ Menú Final Almuerzo
│                               ├─→ Home
│                               └─→ Menú Final Cena*
├─→ Selección Cena ──────→ Menú Final Cena
│                               ├─→ Home
│                               └─→ Menú Final Almuerzo*
├─→ Menú Final Almuerzo* (si existe)
└─→ Menú Final Cena* (si existe)

[ROL ADMIN]
Login (si no hay sesión)
    └─→ ¿Cuenta vinculada a hogar?
            ├─ NO → Vinculación (código o QR) ─→ Dashboard
            └─ SÍ → Dashboard
                    ├─→ Historial de menús
                    │       └─→ Detalle de menú
                    ├─→ Gestión del catálogo
                    │       └─→ Añadir/Editar ingrediente
                    ├─→ Papelera
                    └─→ Estadísticas
```

---

## 16. Comportamiento offline

| Situación | Rol user | Rol admin |
|-----------|----------|-----------|
| Sin conexión, primera apertura | Funciona en local con datos hardcodeados. El `bootstrapUserHousehold` queda pendiente hasta recuperar red. | No puede hacer login ni vinculación. Muestra error de red. |
| Sin conexión, no primera apertura | Usa catálogo cacheado localmente. Funciona 100%. El log de menús se encola y se envía cuando recupere conexión. | Puede ver datos cacheados (Firestore offline persistence). No puede hacer cambios que requieran sync inmediato. |
| Conexión recuperada | Envía logs encolados. Descarga catálogo actualizado si hay cambios. | Sincroniza automáticamente. |

---

## 17. Decisiones de implementación pendientes (no bloqueantes para empezar)

La arquitectura base ya queda cerrada. Estos puntos se pueden decidir durante la implementación:

1. **Migración de rotación legacy:** si se importan o no los valores de SharedPreferences del Android Java original.
2. **Sincronización de catálogo en caliente:** aplicar cambios admin al instante en una sesión user en curso, o en la siguiente apertura (recomendado: siguiente apertura por simplicidad).
3. **Canal de pairing principal:** permitir ambos (código + QR) o lanzar primero solo con código y añadir QR después.
4. **Retención histórica:** definir política de conservación (ej: indefinida o purgado opcional >24 meses).
