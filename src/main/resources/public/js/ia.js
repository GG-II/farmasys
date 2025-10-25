const API_URL = 'http://localhost:4567/api';
let demandChart = null;

async function predecirDemanda() {
    const productName = document.getElementById('productName').value;
    const weeksAhead = parseInt(document.getElementById('weeksAhead').value);
    
    if (!productName) {
        alert('Por favor selecciona una medicina');
        return;
    }
    
    if (weeksAhead < 1 || weeksAhead > 12) {
        alert('Las semanas deben estar entre 1 y 12');
        return;
    }
    
    // Mostrar loading
    document.getElementById('loading').style.display = 'block';
    document.getElementById('resultados').style.display = 'none';
    
    try {
        const response = await fetch(`${API_URL}/ai/predict`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                productName: productName,
                weeksAhead: weeksAhead
            })
        });
        
        const result = await response.json();
        
        document.getElementById('loading').style.display = 'none';
        
        if (result.status === 'success') {
            mostrarResultados(result);
        } else {
            alert('Error: ' + result.message);
        }
        
    } catch (error) {
        document.getElementById('loading').style.display = 'none';
        console.error('Error:', error);
        alert('Error al conectar con el servidor');
    }
}

function mostrarResultados(data) {
    document.getElementById('resultados').style.display = 'block';
    
    // Mostrar resumen
    const resumenHTML = `
        <div class="col-md-3">
            <div class="card text-center">
                <div class="card-body">
                    <h3 class="text-primary">${data.summary.total_forecast}</h3>
                    <p class="mb-0">Demanda Total</p>
                    <small class="text-muted">${data.forecast_period}</small>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center">
                <div class="card-body">
                    <h3 class="text-success">${data.summary.average_weekly_demand}</h3>
                    <p class="mb-0">Promedio Semanal</p>
                    <small class="text-muted">unidades/semana</small>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center">
                <div class="card-body">
                    <h3 class="text-info">${data.summary.peak_demand}</h3>
                    <p class="mb-0">Pico de Demanda</p>
                    <small class="text-muted">Semana ${data.summary.peak_week}</small>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center">
                <div class="card-body">
                    <h3 class="text-warning">${Math.round(data.confidence * 100)}%</h3>
                    <p class="mb-0">Confianza</p>
                    <small class="text-muted">del modelo</small>
                </div>
            </div>
        </div>
    `;
    document.getElementById('resumenCards').innerHTML = resumenHTML;
    
    // Mostrar predicciones semanales
    let weeklyHTML = '';
    data.predictions.forEach(pred => {
        weeklyHTML += `
            <div class="week-card">
                <div class="row align-items-center">
                    <div class="col-md-2">
                        <strong>Semana ${pred.week}</strong>
                        <br><small class="text-muted">${pred.date}</small>
                    </div>
                    <div class="col-md-3">
                        <div class="progress" style="height: 25px;">
                            <div class="progress-bar bg-primary" 
                                 style="width: ${(pred.predicted_demand / data.summary.peak_demand) * 100}%">
                                ${pred.predicted_demand} unidades
                            </div>
                        </div>
                    </div>
                    <div class="col-md-7">
                        <small class="text-muted">
                            Rango: ${pred.lower_bound} - ${pred.upper_bound} unidades
                        </small>
                    </div>
                </div>
            </div>
        `;
    });
    document.getElementById('weeklyPredictions').innerHTML = weeklyHTML;
    
    // Crear gráfico
    crearGrafico(data.predictions);
    
    // Mostrar recomendaciones
    const invHTML = `
        <div class="row">
            <div class="col-md-3">
                <div class="alert alert-info">
                    <h5>${data.inventory_recommendations.safety_stock}</h5>
                    <p class="mb-0">Stock de Seguridad</p>
                </div>
            </div>
            <div class="col-md-3">
                <div class="alert alert-warning">
                    <h5>${data.inventory_recommendations.reorder_point}</h5>
                    <p class="mb-0">Punto de Reorden</p>
                </div>
            </div>
            <div class="col-md-3">
                <div class="alert alert-success">
                    <h5>${data.inventory_recommendations.recommended_order_quantity}</h5>
                    <p class="mb-0">Cantidad Recomendada</p>
                </div>
            </div>
            <div class="col-md-3">
                <div class="alert alert-primary">
                    <h5>${data.inventory_recommendations.lead_time_coverage}</h5>
                    <p class="mb-0">Cobertura</p>
                </div>
            </div>
        </div>
        <div class="alert alert-light">
            <strong>💡 Información del Modelo:</strong><br>
            MAE: ${data.model_info.mae} | RMSE: ${data.model_info.rmse}<br>
            Última demanda observada: ${data.model_info.last_observed_demand}<br>
            Promedio histórico: ${data.model_info.historical_average}<br>
            Tendencia: ${data.model_info.trend || 'N/A'}
        </div>
    `;
    document.getElementById('inventoryRecommendations').innerHTML = invHTML;
}

function crearGrafico(predictions) {
    const ctx = document.getElementById('demandChart').getContext('2d');
    
    // Destruir gráfico anterior si existe
    if (demandChart) {
        demandChart.destroy();
    }
    
    const labels = predictions.map(p => `Semana ${p.week}`);
    const data = predictions.map(p => p.predicted_demand);
    const lower = predictions.map(p => p.lower_bound);
    const upper = predictions.map(p => p.upper_bound);
    
    demandChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Demanda Predicha',
                    data: data,
                    borderColor: 'rgb(75, 192, 192)',
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    tension: 0.1,
                    fill: true
                },
                {
                    label: 'Límite Inferior',
                    data: lower,
                    borderColor: 'rgba(255, 99, 132, 0.5)',
                    borderDash: [5, 5],
                    fill: false
                },
                {
                    label: 'Límite Superior',
                    data: upper,
                    borderColor: 'rgba(54, 162, 235, 0.5)',
                    borderDash: [5, 5],
                    fill: false
                }
            ]
        },
        options: {
            responsive: true,
            plugins: {
                title: {
                    display: true,
                    text: 'Predicción de Demanda con Intervalos de Confianza'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Unidades'
                    }
                }
            }
        }
    });
}