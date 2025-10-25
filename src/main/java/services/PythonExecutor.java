package services;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class PythonExecutor {
    
    private static final String PYTHON_SCRIPT = "python_ai/pharmacy_demand_predictor.py";
    private static final Gson gson = new Gson();
    
    public static Map<String, Object> predictDemand(String productName, int weeksAhead) {
        try {
            // Crear parámetros
            Map<String, Object> params = new HashMap<>();
            params.put("prediction_type", "single_product");
            params.put("product_name", productName);
            params.put("weeks_ahead", weeksAhead);
            
            String jsonParams = gson.toJson(params);
            
            System.out.println("JSON enviando: " + jsonParams);
            
            // Guardar JSON en archivo temporal
            File tempFile = File.createTempFile("ai_params_", ".json");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(jsonParams);
            }
            
            // Ejecutar script Python con subprocess
            ProcessBuilder pb = new ProcessBuilder(
                "python", 
                "-c",
                "import sys; import json; " +
                "params = json.load(open('" + tempFile.getAbsolutePath().replace("\\", "/") + "')); " +
                "import subprocess; " +
                "result = subprocess.run(['python', '" + PYTHON_SCRIPT.replace("\\", "/") + "', json.dumps(params)], " +
                "capture_output=True, text=True); " +
                "print(result.stdout)"
            );
            
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            // Leer resultado
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            
            int exitCode = process.waitFor();
            
            // Eliminar archivo temporal
            tempFile.delete();
            
            if (exitCode == 0) {
                String jsonOutput = output.toString().trim();
                return gson.fromJson(jsonOutput, Map.class);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("status", "error");
                error.put("message", "Python script failed: " + output.toString());
                return error;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Error executing Python: " + e.getMessage());
            return error;
        }
    }
    
    public static Map<String, Object> getAllProductsForecast(int weeksAhead) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("prediction_type", "all_products");
            params.put("weeks_ahead", weeksAhead);
            
            String jsonParams = gson.toJson(params);
            
            // Guardar JSON en archivo temporal
            File tempFile = File.createTempFile("ai_params_", ".json");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(jsonParams);
            }
            
            ProcessBuilder pb = new ProcessBuilder(
                "python", 
                "-c",
                "import sys; import json; " +
                "params = json.load(open('" + tempFile.getAbsolutePath().replace("\\", "/") + "')); " +
                "import subprocess; " +
                "result = subprocess.run(['python', '" + PYTHON_SCRIPT.replace("\\", "/") + "', json.dumps(params)], " +
                "capture_output=True, text=True); " +
                "print(result.stdout)"
            );
            
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            
            int exitCode = process.waitFor();
            
            tempFile.delete();
            
            if (exitCode == 0) {
                String jsonOutput = output.toString().trim();
                return gson.fromJson(jsonOutput, Map.class);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("status", "error");
                error.put("message", "Python script failed");
                return error;
            }
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Error: " + e.getMessage());
            return error;
        }
    }
}