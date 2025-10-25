const API_URL = 'http://localhost:4567/api';
let modalProveedor;
let editandoId = null;

document.addEventListener('DOMContentLoaded', function() {
    modalProveedor = new bootstrap.Modal(document.getElementById('modalProveedor'));
    cargarProveedores();
    
    document.getElementById('buscarProveedor').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') buscarProveedores();
    });
});

async function cargarProveedores() {
    try {
        const response = await fetch(`${API_URL}/proveedores`);
        const proveedores = await response.json();
        mostrarProveedores(proveedores);
    } catch (error) {
        console.error('Error:', error);
        alert('Error al cargar proveedores');
    }
}

async function buscarProveedores() {
    const termino = document.getElementById('buscarProveedor').value.trim();
    
    if (!termino) {
        cargarProveedores();
        return;
    }
    
    try {
        const response = await fetch(`${API_URL}/proveedores/buscar/${encodeURIComponent(termino)}`);
        const proveedores = await response.json();
        mostrarProveedores(proveedores);
    } catch (error) {
        console.error('Error:', error);
        alert('Error al buscar proveedores');
    }
}

function mostrarProveedores(proveedores) {
    const tbody = document.getElementById('bodyProveedores');
    tbody.innerHTML = '';
    
    if (proveedores.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center">No se encontraron proveedores</td></tr>';
        return;
    }
    
    proveedores.forEach(prov => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${prov.id}</td>
            <td><strong>${prov.nombre}</strong></td>
            <td>${prov.contacto || 'N/A'}</td>
            <td>
                <button class="btn btn-sm btn-primary" onclick="editarProveedor(${prov.id})">
                    <i class="fas fa-edit"></i>
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function abrirModalNuevo() {
    editandoId = null;
    document.getElementById('tituloModal').textContent = 'Nuevo Proveedor';
    document.getElementById('formProveedor').reset();
    modalProveedor.show();
}

async function editarProveedor(id) {
    try {
        const response = await fetch(`${API_URL}/proveedores/${id}`);
        const proveedor = await response.json();
        
        if (proveedor.error) {
            alert('Error: ' + proveedor.error);
            return;
        }
        
        editandoId = id;
        document.getElementById('tituloModal').textContent = 'Editar Proveedor';
        document.getElementById('proveedorId').value = proveedor.id;
        document.getElementById('nombre').value = proveedor.nombre || '';
        document.getElementById('contacto').value = proveedor.contacto || '';
        
        modalProveedor.show();
    } catch (error) {
        console.error('Error:', error);
        alert('Error al cargar proveedor');
    }
}

async function guardarProveedor() {
    const form = document.getElementById('formProveedor');
    
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const proveedor = {
        nombre: document.getElementById('nombre').value,
        contacto: document.getElementById('contacto').value
    };
    
    try {
        let response;
        
        if (editandoId) {
            proveedor.id = editandoId;
            response = await fetch(`${API_URL}/proveedores/${editandoId}`, {
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(proveedor)
            });
        } else {
            response = await fetch(`${API_URL}/proveedores`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(proveedor)
            });
        }
        
        const result = await response.json();
        
        if (response.ok) {
            alert(result.message || 'Operación exitosa');
            modalProveedor.hide();
            cargarProveedores();
        } else {
            alert('Error: ' + (result.error || 'Error desconocido'));
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al guardar proveedor');
    }
}