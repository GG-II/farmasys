import sys

# Leer el archivo original
with open('pharmacy_demand_predictor.py', 'r', encoding='utf-8') as f:
    content = f.read()

# Cambiar la ruta de models
content = content.replace(
    "models_dir = './models'",
    "models_dir = 'python_ai/models'"
)

# Guardar
with open('pharmacy_demand_predictor.py', 'w', encoding='utf-8') as f:
    f.write(content)

print("✅ Script corregido")
