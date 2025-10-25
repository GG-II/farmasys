const API_URL = 'http://localhost:4567/api';
let modalMedicina;
let editandoId = null;
let editandoTipoId = null; // Para guardar el tipoID original al editar
let tiposMap = {}; // Cache de tipos

document.addEventListener('DOMContentLoaded', function() {
    modalMedicina = new bootstrap.Modal(document.getElementById('modalMedicina'));
    cargarTodasLasMedicinas();
    cargarTiposMedicina();
    
    // Actualizar tipoID cuando cambia el tipo
    document.getElementById('medicinaTipo').addEventListener('change', function(e) {
        const tipoSeleccionado = e.target.value;
        editandoTipoId = tiposMap[tipoSeleccionado] || 1;
        console.log('Tipo cambiado a:', tipoSeleccionado, 'ID:', editandoTipoId);
    });
    
    // Enter en búsqueda
    document.getElementById('buscarMedicina').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') buscarMedicinas();
    });
});

async function cargarTodasLasMedicinas() {
    try {
        document.getElementById('loadingMedicinas').style.display = 'block';
        document.getElementById('tablaMedicinas').style.display = 'none';
        
        const response = await fetch(`${API_URL}/medicinas`);
        const medicinas = await response.json();
        
        mostrarMedicinas(medicinas);
    } catch (error) {
        console.error('Error:', error);
        alert('Error al cargar medicinas');
    }
}

async function buscarMedicinas() {
    const termino = document.getElementById('buscarMedicina').value.trim();
    
    if (!termino) {
        cargarTodasLasMedicinas();
        return;
    }
    
    try {
        document.getElementById('loadingMedicinas').style.display = 'block';
        document.getElementById('tablaMedicinas').style.display = 'none';
        
        const response = await fetch(`${API_URL}/medicinas/buscar/${encodeURIComponent(termino)}`);
        const medicinas = await response.json();
        
        mostrarMedicinas(medicinas);
    } catch (error) {
        console.error('Error:', error);
        alert('Error al buscar medicinas');
    }
}

function mostrarMedicinas(medicinas) {
    document.getElementById('loadingMedicinas').style.display = 'none';
    document.getElementById('tablaMedicinas').style.display = 'block';
    
    const tbody = document.getElementById('bodyMedicinas');
    tbody.innerHTML = '';
    
    if (medicinas.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center">No se encontraron medicinas</td></tr>';
        return;
    }
    
    medicinas.forEach(med => {
        const tr = document.createElement('tr');
        
        let badgeStock = '';
        if (med.cantidad <= 0) {
            badgeStock = '<span class="badge bg-danger">Agotado</span>';
        } else if (med.cantidad < 10) {
            badgeStock = '<span class="badge bg-warning text-dark">Stock Bajo</span>';
        } else {
            badgeStock = '<span class="badge bg-success">Stock OK</span>';
        }
        
        tr.innerHTML = `
            <td>${med.id}</td>
            <td><strong>${med.nombre || 'N/A'}</strong></td>
            <td>${med.dosis || 'N/A'}</td>
            <td>${med.tipo || 'N/A'}</td>
            <td>${badgeStock} ${med.cantidad || 0}</td>
            <td>Q${(med.precio || 0).toFixed(2)}</td>
            <td>${med.fechaCaducidad || 'N/A'}</td>
            <td>
                <button class="btn btn-sm btn-primary" onclick="editarMedicina(${med.id})">
                    <i class="fas fa-edit"></i>
                </button>
            </td>
        `;
        
        tbody.appendChild(tr);
    });
}

async function cargarTiposMedicina() {
    try {
        const response = await fetch(`${API_URL}/medicinas/tipos/lista`);
        const tipos = await response.json();
        
        const select = document.getElementById('medicinaTipo');
        select.innerHTML = '<option value="">Seleccione...</option>';
        
        // Crear mapeo de tipos a IDs (hardcoded según tu BD)
        tiposMap = {
            'AMPOLLAS': 1,
            'BLISTER': 2,
            'TABLETAS': 3,
            'JARABE': 4,
            'GEL': 5,
            'CREMA': 6,
            'SOBRES': 7,
            'SPRAY': 8
        };
        
        tipos.forEach(tipo => {
            const option = document.createElement('option');
            option.value = tipo;
            option.textContent = tipo;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Error al cargar tipos:', error);
    }
}

function abrirModalNueva() {
    editandoId = null;
    editandoTipoId = null;
    document.getElementById('tituloModal').textContent = 'Nueva Medicina';
    document.getElementById('formMedicina').reset();
    document.getElementById('medicinaId').value = '';
    modalMedicina.show();
}

async function editarMedicina(id) {
    try {
        const response = await fetch(`${API_URL}/medicinas/${id}`);
        const medicina = await response.json();
        
        if (medicina.error) {
            alert('Error: ' + medicina.error);
            return;
        }
        
        editandoId = id;
        document.getElementById('tituloModal').textContent = 'Editar Medicina';
        document.getElementById('medicinaId').value = medicina.id;
        document.getElementById('medicinaNombre').value = medicina.nombre || '';
        document.getElementById('medicinaDosis').value = medicina.dosis || '';
        document.getElementById('medicinaTipo').value = medicina.tipo || '';
        document.getElementById('medicinaCantidad').value = medicina.cantidad || 0;
        document.getElementById('medicinaPrecio').value = medicina.precio || 0;
        document.getElementById('medicinaDescripcion').value = medicina.descripcion || '';
        
        // Guardar el tipoID original para usarlo al guardar
        editandoTipoId = medicina.tipoID || medicina.tipo_id || tiposMap[medicina.tipo] || 1;
        
        // Formatear fecha
        if (medicina.fechaCaducidad) {
            const fecha = new Date(medicina.fechaCaducidad);
            const fechaFormateada = fecha.toISOString().split('T')[0];
            document.getElementById('medicinaFechaCaducidad').value = fechaFormateada;
        }
        
        modalMedicina.show();
    } catch (error) {
        console.error('Error:', error);
        alert('Error al cargar medicina');
    }
}

async function guardarMedicina() {
    const form = document.getElementById('formMedicina');
    
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const tipoSeleccionado = document.getElementById('medicinaTipo').value;
    
    // SIEMPRE obtener el tipoID del mapa, no importa si es edición o nuevo
    const tipoID = tiposMap[tipoSeleccionado];
    
    if (!tipoID) {
        alert('Error: Tipo de medicina no válido');
        return;
    }
    
    const medicina = {
        nombre: document.getElementById('medicinaNombre').value,
        dosis: document.getElementById('medicinaDosis').value,
        tipoID: tipoID,  // Solo enviar tipoID, no enviar "tipo"
        cantidad: parseInt(document.getElementById('medicinaCantidad').value),
        precio: parseFloat(document.getElementById('medicinaPrecio').value),
        descripcion: document.getElementById('medicinaDescripcion').value,
        fechaCaducidad: document.getElementById('medicinaFechaCaducidad').value
    };
    
    console.log('Enviando medicina:', medicina); // Para debug
    
    try {
        let response;
        
        if (editandoId) {
            // Actualizar
            medicina.id = editandoId;
            response = await fetch(`${API_URL}/medicinas/${editandoId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(medicina)
            });
        } else {
            // Crear
            response = await fetch(`${API_URL}/medicinas`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(medicina)
            });
        }
        
        const result = await response.json();
        
        if (response.ok) {
            alert(result.message || 'Operación exitosa');
            modalMedicina.hide();
            editandoId = null;
            editandoTipoId = null;
            cargarTodasLasMedicinas();
        } else {
            alert('Error: ' + (result.error || 'Error desconocido'));
            console.error('Error del servidor:', result);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al guardar medicina');
    }
}