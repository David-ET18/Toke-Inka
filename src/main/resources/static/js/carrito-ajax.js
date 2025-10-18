document.addEventListener("DOMContentLoaded", function() {
    // Selecciona todos los formularios de "agregar al carrito"
    const addToCartForms = document.querySelectorAll('.form-add-to-cart'); // Necesitarías añadir esta clase a tu form

    addToCartForms.forEach(form => {
        form.addEventListener('submit', function(event) {
            // 1. Prevenir el envío normal del formulario que recarga la página
            event.preventDefault();

            // 2. Recolectar los datos del formulario
            const formData = new FormData(form);
            const url = form.action;

            // 3. Enviar los datos al servidor usando AJAX (Fetch API)
            fetch(url, {
                method: 'POST',
                body: formData
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Hubo un problema al añadir el producto.');
                }
                return response.json(); // Suponiendo que el controlador devuelve JSON
            })
            .then(data => {
                // 4. Actualizar la UI sin recargar la página
                console.log('Producto añadido!', data);
                
                // Actualizar el contador del carrito en el header
                const cartCounter = document.querySelector('.badge');
                if (cartCounter) {
                    cartCounter.textContent = data.nuevoTotalItems; // El controlador debería devolver este dato
                    cartCounter.style.display = 'inline';
                }

                // Mostrar una notificación de éxito (ej. con un Toast de Bootstrap)
                // ... Lógica para mostrar el toast ...
            })
            .catch(error => {
                console.error('Error:', error);
                // Mostrar una notificación de error
            });
        });
    });
});