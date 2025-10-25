const API_URL = 'http://localhost:4567/api';
let carrito = [];
let medicinas = [];
let clientes = [];

document.addEventListener('DOMContentLoaded', function() {
    cargarMedicinasDisponibles();
    cargarClientes();
    
    document.getElementById('buscarProducto').addEventListener('input', filtrarProductos);
});

async function cargarMedicinasDisponibles() {
    try {
        const response = await fetch(`${API_URL}/medicinas/disponibles/lista`);
        medicinas = await response.json();
        mostrarProductos(medicinas);
    } catch (error) {
        console.error('Error:', error);
        alert('Error al cargar medicinas');
    }
}

async function cargarClientes() {
    try {
        const response = await fetch(`${API_URL}/clientes`);
        clientes = await response.json();
        
        const select = document.getElementById('selectCliente');
        select.innerHTML = '<option value="">Seleccione un cliente...</option>';
        
        clientes.forEach(cli => {
            const nombreCompleto = `${cli.primerNombre || ''} ${cli.segundoNombre || ''} ${cli.primerApellido || ''} ${cli.segundoApellido || ''}`.trim();
            const option = document.createElement('option');
            option.value = cli.id;
            option.textContent = nombreCompleto;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Error:', error);
    }
}

function mostrarProductos(lista) {
    const container = document.getElementById('listaProductos');
    container.innerHTML = '';
    
    if (lista.length === 0) {
        container.innerHTML = '<p class="text-center text-muted">No hay medicinas disponibles</p>';
        return;
    }
    
    lista.forEach(med => {
        const div = document.createElement('div');
        div.className = 'producto-item border rounded p-3 mb-2';
        div.onclick = () => agregarAlCarrito(med);
        
        div.innerHTML = `
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <strong>${med.nombre}</strong>
                    <br>
                    <small class="text-muted">${med.dosis || ''} - ${med.tipo || ''}</small>
                    <br>
                    <span class="badge bg-info">Stock: ${med.cantidad}</span>
                </div>
                <div class="text-end">
                    <h5 class="mb-0 text-primary">Q${med.precio.toFixed(2)}</h5>
                    <button class="btn btn-sm btn-success mt-1">
                        <i class="fas fa-plus"></i> Agregar
                    </button>
                </div>
            </div>
        `;
        
        container.appendChild(div);
    });
}

function filtrarProductos() {
    const termino = document.getElementById('buscarProducto').value.toLowerCase();
    const filtrados = medicinas.filter(m => 
        m.nombre.toLowerCase().includes(termino)
    );
    mostrarProductos(filtrados);
}

function agregarAlCarrito(medicina) {
    // Verificar si ya está en el carrito
    const index = carrito.findIndex(item => item.id === medicina.id);
    
    if (index !== -1) {
        // Ya existe, aumentar cantidad si hay stock
        if (carrito[index].cantidad < medicina.cantidad) {
            carrito[index].cantidad++;
        } else {
            alert('No hay más stock disponible');
            return;
        }
    } else {
        // Agregar nuevo
        carrito.push({
            id: medicina.id,
            nombre: medicina.nombre,
            precio: medicina.precio,
            cantidad: 1,
            stockDisponible: medicina.cantidad
        });
    }
    
    actualizarVistaCarrito();
}

function actualizarVistaCarrito() {
    const container = document.getElementById('itemsCarrito');
    
    if (carrito.length === 0) {
        container.innerHTML = '<p class="text-muted text-center">El carrito está vacío</p>';
        document.getElementById('totalVenta').textContent = '0.00';
        return;
    }
    
    container.innerHTML = '';
    let total = 0;
    
    carrito.forEach((item, index) => {
        const subtotal = item.precio * item.cantidad;
        total += subtotal;
        
        const div = document.createElement('div');
        div.className = 'border rounded p-2 mb-2 bg-light';
        div.innerHTML = `
            <div class="d-flex justify-content-between align-items-center">
                <div class="flex-grow-1">
                    <strong>${item.nombre}</strong>
                    <br>
                    <small>Q${item.precio.toFixed(2)} c/u</small>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <button class="btn btn-sm btn-secondary" onclick="cambiarCantidad(${index}, -1)">
                        <i class="fas fa-minus"></i>
                    </button>
                    <span class="px-2"><strong>${item.cantidad}</strong></span>
                    <button class="btn btn-sm btn-secondary" onclick="cambiarCantidad(${index}, 1)">
                        <i class="fas fa-plus"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="eliminarDelCarrito(${index})">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            </div>
            <div class="text-end mt-1">
                <small>Subtotal: <strong>Q${subtotal.toFixed(2)}</strong></small>
            </div>
        `;
        
        container.appendChild(div);
    });
    
    document.getElementById('totalVenta').textContent = total.toFixed(2);
}

function cambiarCantidad(index, cambio) {
    const item = carrito[index];
    const nuevaCantidad = item.cantidad + cambio;
    
    if (nuevaCantidad <= 0) {
        eliminarDelCarrito(index);
        return;
    }
    
    if (nuevaCantidad > item.stockDisponible) {
        alert('No hay suficiente stock');
        return;
    }
    
    item.cantidad = nuevaCantidad;
    actualizarVistaCarrito();
}

function eliminarDelCarrito(index) {
    carrito.splice(index, 1);
    actualizarVistaCarrito();
}

function limpiarCarrito() {
    if (carrito.length === 0) return;
    
    if (confirm('¿Limpiar todo el carrito?')) {
        carrito = [];
        actualizarVistaCarrito();
    }
}

async function procesarVenta() {
    // Validaciones
    const clienteId = document.getElementById('selectCliente').value;
    
    if (!clienteId) {
        alert('Debe seleccionar un cliente');
        return;
    }
    
    if (carrito.length === 0) {
        alert('El carrito está vacío');
        return;
    }
    
    // Calcular total
    const total = carrito.reduce((sum, item) => sum + (item.precio * item.cantidad), 0);
    
    // Preparar datos de la venta
    const venta = {
        idCliente: parseInt(clienteId),
        fecha: new Date().toISOString().split('T')[0],
        total: total,
        detalles: carrito.map(item => ({
            idMedicina: item.id,
            cantidad: item.cantidad,
            precio: item.precio
        }))
    };
    
    try {
        const response = await fetch(`${API_URL}/ventas`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(venta)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            alert(`¡Venta registrada exitosamente!\nTotal: Q${total.toFixed(2)}\nID Venta: ${result.idVenta}`);
            
            // Limpiar todo
            carrito = [];
            actualizarVistaCarrito();
            document.getElementById('selectCliente').value = '';
            
            // Recargar medicinas para actualizar stock
            cargarMedicinasDisponibles();
        } else {
            alert('Error: ' + (result.error || 'Error al procesar venta'));
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al procesar la venta');
    }
}