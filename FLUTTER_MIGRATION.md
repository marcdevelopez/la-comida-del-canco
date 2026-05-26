# Flutter Migration Workspace

Este repositorio queda organizado para migrar progresivamente desde Android nativo (Java) a Flutter.

## Estructura acordada

- `app/`: código Android legado (referencia funcional actual)
- `flutter_app/`: nuevo proyecto Flutter donde irá la nueva implementación
- `project-resources/`: assets sensibles fuera de versionado (ignorado por git)

## Objetivo práctico

1. Mantener `app/` como baseline de comportamiento.
2. Implementar en `flutter_app/` una réplica funcional basada en `SPECS.md`.
3. Añadir nuevas funcionalidades en Flutter sin tocar la lógica legacy salvo para validaciones puntuales.

## Arranque rápido de Flutter

```bash
cd flutter_app
flutter run
```

## Próximos pasos recomendados

1. Definir el modelo de dominio (`Ingrediente`, categorías y persistencia).
2. Crear rutas/pantallas equivalentes (`Home`, selección almuerzo/cena, menús finales).
3. Importar assets reutilizables y mapear nombres de recurso.
4. Portar el algoritmo de rotación y tests de regresión de lógica.
