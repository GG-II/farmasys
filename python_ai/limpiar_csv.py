import csv

# Leer CSV con comillas y reescribir sin comillas
with open('pharmacy_otc_sales_data.csv', 'r', encoding='utf-8') as infile:
    reader = csv.reader(infile)
    
    with open('pharmacy_otc_sales_data_clean.csv', 'w', encoding='utf-8', newline='') as outfile:
        writer = csv.writer(outfile, quoting=csv.QUOTE_NONE, escapechar='\\')
        
        for row in reader:
            writer.writerow(row)

print("✅ CSV limpio creado: pharmacy_otc_sales_data_clean.csv")

# Reemplazar el archivo original
import os
os.replace('pharmacy_otc_sales_data_clean.csv', 'pharmacy_otc_sales_data.csv')
print("✅ Archivo original reemplazado")