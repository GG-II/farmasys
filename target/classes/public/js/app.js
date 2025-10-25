// URL base de la API
const API_URL = 'http://localhost:4567/api';

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Iniciando FarmaSys Web...');
    
    // Verificar conexión con el servidor
    verificarServidor();
    
    // Cargar datos iniciales
    cargarMedicinas();
    cargarClientes();
    cargarProveedores();
});

// Verificar estado del servidor
async function verificarServidor() {
    try {
        const response = await fetch(`${API_URL}/test`);
        const data = await response.json();
        
        if (data.status === 'ok') {
            document.getElementById('estadoServidor').innerHTML = 
                '<i class="fas fa-check-circle text-success"></i>';
            console.log('✅ Servidor conectado correctamente');
        }
    } catch (error) {
        document.getElementById('estadoServidor').innerHTML = 
            '<i class="fas fa-times-circle text-danger"></i>';
        console.error('❌ Error de conexión con el servidor:', error);
    }
}

// Cargar medicinas
async function cargarMedicinas() {
    try {
        const response = await fetch(`${API_URL}/medicinas`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const medicinas = await response.json();
        console.log('📦 Medicinas cargadas:', medicinas.length);
        
        // Actualizar contador
        document.getElementById('totalMedicinas').textContent = medicinas.length;
        
        // Mostrar tabla y ocultar loading
        document.getElementById('loadingMedicinas').style.display = 'none';
        document.getElementById('tablaMedicinas').style.display = 'block';
        
        // Llenar tabla
        const tbody = document.getElementById('bodyMedicinas');
        tbody.innerHTML = '';
        
        medicinas.forEach(med => {
            const tr = document.createElement('tr');
            
            // Determinar estado del stock
            let estadoBadge = '';
            if (med.cantidad <= 0) {
                estadoBadge = '<span class="badge badge-custom status-danger">Agotado</span>';
            } else if (med.cantidad < 10) {
                estadoBadge = '<span class="badge badge-custom status-warning">Stock Bajo</span>';
            } else {
                estadoBadge = '<span class="badge badge-custom status-ok">Stock OK</span>';
            }
            
            tr.innerHTML = `
                <td>${med.id}</td>
                <td><strong>${med.nombre || 'N/A'}</strong></td>
                <td>${med.dosis || 'N/A'}</td>
                <td>${med.tipo || 'N/A'}</td>
                <td>${med.cantidad || 0}</td>
                <td>Q${(med.precio || 0).toFixed(2)}</td>
                <td>${med.fechaCaducidad || 'N/A'}</td>
                <td>${estadoBadge}</td>
            `;
            
            tbody.appendChild(tr);
        });
        
    } catch (error) {
        console.error('❌ Error al cargar medicinas:', error);
        document.getElementById('loadingMedicinas').style.display = 'none';
        document.getElementById('errorMedicinas').style.display = 'block';
        document.getElementById('errorMedicinas').textContent = 
            'Error al cargar medicinas: ' + error.message;
    }
}

// Cargar clientes
async function cargarClientes() {
    try {
        const response = await fetch(`${API_URL}/clientes`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const clientes = await response.json();
        console.log('👥 Clientes cargados:', clientes.length);
        console.log('📋 Ejemplo de cliente:', clientes[0]); // Para debug
        
        // Actualizar contador
        document.getElementById('totalClientes').textContent = clientes.length;
        
        // Mostrar tabla y ocultar loading
        document.getElementById('loadingClientes').style.display = 'none';
        document.getElementById('tablaClientes').style.display = 'block';
        
        // Llenar tabla
        const tbody = document.getElementById('bodyClientes');
        tbody.innerHTML = '';
        
        clientes.forEach(cli => {
            const tr = document.createElement('tr');
            
            // Construir nombre completo - adaptarse a cualquier estructura de propiedades
            const nombre1 = cli.primerNombre || cli.nombre1 || cli.nombre_1 || '';
            const nombre2 = cli.segundoNombre || cli.nombre2 || cli.nombre_2 || '';
            const apellido1 = cli.primerApellido || cli.apellido1 || cli.apellido_1 || '';
            const apellido2 = cli.segundoApellido || cli.apellido2 || cli.apellido_2 || '';
            const nombreCompleto = `${nombre1} ${nombre2} ${apellido1} ${apellido2}`.trim() || 'N/A';
            
            // ID puede venir como id, idCliente o id_cliente
            const idCliente = cli.id || cli.idCliente || cli.id_cliente || 'N/A';
            
            tr.innerHTML = `
                <td>${idCliente}</td>
                <td><strong>${nombreCompleto}</strong></td>
                <td>${cli.contacto || 'N/A'}</td>
                <td>${cli.genero || 'N/A'}</td>
            `;
            
            tbody.appendChild(tr);
        });
        
    } catch (error) {
        console.error('❌ Error al cargar clientes:', error);
        document.getElementById('loadingClientes').style.display = 'none';
        document.getElementById('errorClientes').style.display = 'block';
        document.getElementById('errorClientes').textContent = 
            'Error al cargar clientes: ' + error.message;
    }
}

// Cargar proveedores
async function cargarProveedores() {
    try {
        const response = await fetch(`${API_URL}/proveedores`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const proveedores = await response.json();
        console.log('🚚 Proveedores cargados:', proveedores.length);
        
        // Actualizar contador
        document.getElementById('totalProveedores').textContent = proveedores.length;
        
        // Mostrar tabla y ocultar loading
        document.getElementById('loadingProveedores').style.display = 'none';
        document.getElementById('tablaProveedores').style.display = 'block';
        
        // Llenar tabla
        const tbody = document.getElementById('bodyProveedores');
        tbody.innerHTML = '';
        
        proveedores.forEach(prov => {
            const tr = document.createElement('tr');
            
            tr.innerHTML = `
                <td>${prov.id}</td>
                <td><strong>${prov.nombre || 'N/A'}</strong></td>
                <td>${prov.contacto || 'N/A'}</td>
            `;
            
            tbody.appendChild(tr);
        });
        
    } catch (error) {
        console.error('❌ Error al cargar proveedores:', error);
        document.getElementById('loadingProveedores').style.display = 'none';
        document.getElementById('errorProveedores').style.display = 'block';
        document.getElementById('errorProveedores').textContent = 
            'Error al cargar proveedores: ' + error.message;
    }
}