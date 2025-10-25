const API_URL = 'http://localhost:4567/api';
let modalCliente;
let editandoId = null;

document.addEventListener('DOMContentLoaded', function() {
    modalCliente = new bootstrap.Modal(document.getElementById('modalCliente'));
    cargarClientes();
    
    document.getElementById('buscarCliente').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') buscarClientes();
    });
});

async function cargarClientes() {
    try {
        const response = await fetch(`${API_URL}/clientes`);
        const clientes = await response.json();
        mostrarClientes(clientes);
    } catch (error) {
        console.error('Error:', error);
        alert('Error al cargar clientes');
    }
}

async function buscarClientes() {
    const termino = document.getElementById('buscarCliente').value.trim();
    
    if (!termino) {
        cargarClientes();
        return;
    }
    
    try {
        const response = await fetch(`${API_URL}/clientes/buscar/${encodeURIComponent(termino)}`);
        const clientes = await response.json();
        mostrarClientes(clientes);
    } catch (error) {
        console.error('Error:', error);
        alert('Error al buscar clientes');
    }
}

function mostrarClientes(clientes) {
    const tbody = document.getElementById('bodyClientes');
    tbody.innerHTML = '';
    
    if (clientes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">No se encontraron clientes</td></tr>';
        return;
    }
    
    clientes.forEach(cli => {
        const nombreCompleto = `${cli.primerNombre || ''} ${cli.segundoNombre || ''} ${cli.primerApellido || ''} ${cli.segundoApellido || ''}`.trim();
        
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${cli.id}</td>
            <td><strong>${nombreCompleto}</strong></td>
            <td>${cli.contacto || 'N/A'}</td>
            <td>${cli.genero || 'N/A'}</td>
            <td>
                <button class="btn btn-sm btn-primary" onclick="editarCliente(${cli.id})">
                    <i class="fas fa-edit"></i>
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function abrirModalNuevo() {
    editandoId = null;
    document.getElementById('tituloModal').textContent = 'Nuevo Cliente';
    document.getElementById('formCliente').reset();
    modalCliente.show();
}

async function editarCliente(id) {
    try {
        const response = await fetch(`${API_URL}/clientes/${id}`);
        const cliente = await response.json();
        
        if (cliente.error) {
            alert('Error: ' + cliente.error);
            return;
        }
        
        editandoId = id;
        document.getElementById('tituloModal').textContent = 'Editar Cliente';
        document.getElementById('clienteId').value = cliente.id;
        document.getElementById('primerNombre').value = cliente.primerNombre || '';
        document.getElementById('segundoNombre').value = cliente.segundoNombre || '';
        document.getElementById('primerApellido').value = cliente.primerApellido || '';
        document.getElementById('segundoApellido').value = cliente.segundoApellido || '';
        document.getElementById('contacto').value = cliente.contacto || '';
        document.getElementById('genero').value = cliente.genero || '';
        
        modalCliente.show();
    } catch (error) {
        console.error('Error:', error);
        alert('Error al cargar cliente');
    }
}

async function guardarCliente() {
    const form = document.getElementById('formCliente');
    
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const cliente = {
        primerNombre: document.getElementById('primerNombre').value,
        segundoNombre: document.getElementById('segundoNombre').value,
        primerApellido: document.getElementById('primerApellido').value,
        segundoApellido: document.getElementById('segundoApellido').value,
        contacto: document.getElementById('contacto').value,
        genero: document.getElementById('genero').value
    };
    
    try {
        let response;
        
        if (editandoId) {
            cliente.id = editandoId;
            response = await fetch(`${API_URL}/clientes/${editandoId}`, {
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(cliente)
            });
        } else {
            response = await fetch(`${API_URL}/clientes`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(cliente)
            });
        }
        
        const result = await response.json();
        
        if (response.ok) {
            alert(result.message || 'Operación exitosa');
            modalCliente.hide();
            cargarClientes();
        } else {
            alert('Error: ' + (result.error || 'Error desconocido'));
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al guardar cliente');
    }
}