// =============================================
// VERSIÓN DE DEPURACIÓN de carrito-dinamico.js
// =============================================
console.log("Cargando carrito-dinamico.js...");

document.addEventListener("DOMContentLoaded", () => {
    console.log("DOM completamente cargado. Iniciando script del carrito.");

    let isUpdating = false;

    const sendCartRequest = async (url, method) => {
        console.log(`[sendCartRequest] Iniciando petición. URL: ${url}, Método: ${method}`);
        if (isUpdating) {
            console.warn("[sendCartRequest] Petición bloqueada: ya hay una actualización en curso.");
            return;
        }
        isUpdating = true;

        const token = document.querySelector('meta[name="_csrf"]')?.content;
        const header = document.querySelector('meta[name="_csrf_header"]')?.content;

        if (!token || !header) {
            console.error("[sendCartRequest] ¡ERROR FATAL! No se encontraron las metaetiquetas CSRF.");
            alert("Error de seguridad. Por favor, recarga la página.");
            isUpdating = false;
            return null;
        }
        console.log("[sendCartRequest] Token CSRF encontrado.");

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    [header]: token
                }
            });

            const data = await response.json();
            console.log("[sendCartRequest] Respuesta recibida del servidor:", data);

            if (!response.ok) {
                throw new Error(data.error || "Error al procesar la solicitud.");
            }
            return data;
        } catch (error) {
            console.error(`[sendCartRequest] Fallo en la operación del carrito:`, error);
            alert("Hubo un error: " + error.message);
            return null;
        } finally {
            console.log("[sendCartRequest] Petición finalizada.");
            isUpdating = false;
        }
    };
    
    // El contenedor principal para la delegación de eventos.
    const cartContainer = document.querySelector('.row.g-5'); // Selector más específico
    
    if (cartContainer) {
        console.log("Contenedor del carrito encontrado. Añadiendo listener de clic...");

        cartContainer.addEventListener('click', async (e) => {
            console.log("Clic detectado dentro del contenedor del carrito.");

            const button = e.target.closest('button');
            if (!button) {
                // Si el clic no fue en un botón, no hacemos nada.
                return;
            }
            console.log("El clic fue en un elemento de botón:", button);

            const itemRow = button.closest('.cart-item-row');
            if (!itemRow) {
                // Si el botón no está dentro de una fila de producto, no es un botón que nos interese.
                return;
            }
            console.log("El botón está dentro de una fila de producto (cart-item-row).");

            const productId = itemRow.dataset.productId;
            const tallaId = itemRow.dataset.tallaId;
            const quantityInput = itemRow.querySelector('.quantity-input');
            let quantity = parseInt(quantityInput.value);

            console.log(`Datos del producto: ID=${productId}, TallaID=${tallaId}, Cantidad actual=${quantity}`);

            let url, method, isDeletion = false;

            if (button.classList.contains('btn-plus')) {
                console.log("Botón '+' presionado.");
                quantity++;
                url = `/carrito/actualizar?productoId=${productId}&tallaId=${tallaId}&cantidad=${quantity}`;
                method = 'PUT';
                quantityInput.value = quantity; // Actualización optimista
            } else if (button.classList.contains('btn-minus')) {
                console.log("Botón '-' presionado.");
                if (quantity > 1) {
                    quantity--;
                    url = `/carrito/actualizar?productoId=${productId}&tallaId=${tallaId}&cantidad=${quantity}`;
                    method = 'PUT';
                    quantityInput.value = quantity; // Actualización optimista
                } else {
                    console.log("Cantidad es 1, no se puede decrementar más.");
                }
            } else if (button.classList.contains('btn-delete')) {
                console.log("Botón 'Quitar' presionado.");
                if (confirm("¿Estás seguro de que quieres eliminar este producto del carrito?")) {
                    console.log("Usuario confirmó la eliminación.");
                    isDeletion = true;
                    url = `/carrito/eliminar?productoId=${productId}&tallaId=${tallaId}`;
                    method = 'DELETE';
                    itemRow.style.opacity = '0.5';
                } else {
                     console.log("Usuario canceló la eliminación.");
                }
            }

            if (url && method) {
                console.log("Preparando para enviar petición AJAX...");
                const data = await sendCartRequest(url, method);
                
                // La función updateUI se llamará desde dentro de sendCartRequest si todo va bien.
                // Aquí solo manejamos la lógica visual si la petición falla.
                if (data) {
                    // La UI se actualiza si hay éxito. No necesitamos hacer nada extra aquí.
                    // ¡PERO VAMOS A ACTUALIZAR EL SUBTOTAL LOCALMENTE!
                    const precioUnitarioText = itemRow.querySelector('p.text-muted.small:last-of-type').textContent;
                    const precioUnitario = parseFloat(precioUnitarioText.match(/(\d+\.\d+)/)[0]);
                    itemRow.querySelector('.item-subtotal').textContent = `S/ ${(precioUnitario * quantity).toFixed(2)}`;
                    
                    if (isDeletion) itemRow.remove();
                    
                    document.getElementById('subtotal-summary').textContent = `S/ ${data.total.toFixed(2)}`;
                    document.getElementById('grand-total').textContent = `S/ ${data.total.toFixed(2)}`;
                    const cartCount = document.getElementById('cart-item-count');
                    if (cartCount) cartCount.textContent = data.totalItems;
                    if(data.totalItems === 0) window.location.reload();

                } else {
                    console.log("La petición AJAX falló o no devolvió datos. Revirtiendo cambios visuales.");
                    if (!isDeletion) {
                        const originalQuantity = quantity + (method === 'PUT' && button.classList.contains('btn-plus') ? -1 : 1);
                         quantityInput.value = originalQuantity;
                    }
                    itemRow.style.opacity = '1';
                }
            } else {
                console.log("No se generó URL o método, no se envía petición.");
            }
        });
    } else {
        console.warn("ADVERTENCIA: No se encontró el contenedor del carrito ('.row.g-5'). El script del carrito no se activará.");
    }
});