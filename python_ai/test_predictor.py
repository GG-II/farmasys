import subprocess
import json

params = {
    "prediction_type": "single_product",
    "product_name": "DIABELIFE",
    "weeks_ahead": 4
}

result = subprocess.run(
    ["python", "pharmacy_demand_predictor.py", json.dumps(params)],
    capture_output=True,
    text=True
)

print("STDOUT:")
print(result.stdout)
print("\nSTDERR:")
print(result.stderr)
print("\nReturn code:", result.returncode)
