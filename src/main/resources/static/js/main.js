document.addEventListener("DOMContentLoaded", function() {
    console.log("main.js cargado y ejecutándose.");

    const tourButton = document.getElementById('tour-chatbox-button');
    if (!tourButton) {
        console.warn("Botón del tour no encontrado. El tour no se activará.");
        return;
    }

    // Definición de los pasos del tour
    const tourSteps = [
        { element: '#welcome-banner', title: '¡Bienvenido a Toke Inca!', content: 'Este es nuestro banner principal. Aquí encontrarás las novedades más importantes.' },
        { element: '#product-section-title', title: 'Nuestros Productos', content: 'Esta sección muestra nuestro increíble catálogo. ¡Sigue bajando para descubrirlo!' },
        { element: '#product-list-container .product-card:first-child', title: 'Tarjeta de Producto', content: 'Cada producto tiene su propia tarjeta. Haz clic en "Ver Detalles" para conocer más.' },
        { element: '.navbar-nav a[href="/carrito"]', title: 'Tu Carrito', content: 'Cuando agregues productos, podrás verlos y gestionar tu compra desde aquí. ¡Es hora de explorar!' }
    ];

    let currentStep = -1;
    let currentPopover = null;

    // Función principal que muestra un paso del tour
    function showStep(stepIndex) {
        // 1. Limpia el popover y el resaltado del paso anterior
        if (currentPopover) {
            currentPopover.dispose();
        }
        document.querySelectorAll('.tour-highlight').forEach(el => el.classList.remove('tour-highlight'));
        
        // 2. Si el tour ha terminado, resetea y detiene
        if (stepIndex >= tourSteps.length) {
            currentStep = -1; 
            return;
        }

        const step = tourSteps[stepIndex];
        const targetElement = document.querySelector(step.element);

        // 3. Si el elemento del paso no existe, salta al siguiente
        if (!targetElement) {
            console.warn(`Elemento del tour no encontrado: ${step.element}. Saltando al siguiente paso.`);
            showStep(stepIndex + 1);
            return;
        }

        // 4. Mueve la vista al elemento y lo resalta
        targetElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
        targetElement.classList.add('tour-highlight');

        const isLastStep = stepIndex === tourSteps.length - 1;
        const buttonText = isLastStep ? 'Finalizar Tour' : 'Siguiente';

        // 5. Construye el contenido del popover de forma segura
        const popoverContentHTML = step.content + 
                                   '<hr>' + 
                                   '<button class="btn btn-primary btn-sm w-100 tour-next-btn">' + 
                                   buttonText + 
                                   '</button>';

        // 6. Crea y muestra el nuevo popover
        currentPopover = new bootstrap.Popover(targetElement, {
            title: step.title,
            content: popoverContentHTML,
            html: true,
            placement: 'bottom',
            trigger: 'manual' // Controlamos la visibilidad manualmente
        });
        
        currentPopover.show();
        currentStep = stepIndex; // Actualiza el estado del paso actual
    }

    // Listener para iniciar el tour. Esto funciona.
    tourButton.addEventListener('click', () => {
        if (currentStep === -1) { // Solo inicia si no está ya en curso
            console.log("Iniciando tour...");
            showStep(0);
        }
    });

    // Listener de clic en el BODY (Delegación de eventos).
    // Esta es la forma más robusta de escuchar clics en elementos dinámicos.
    document.body.addEventListener('click', function(event) {
        // Si el elemento en el que se hizo clic tiene la clase 'tour-next-btn'...
        if (event.target.classList.contains('tour-next-btn')) {
            console.log("Botón 'Siguiente/Finalizar' presionado. Avanzando al paso:", currentStep + 1);
            // ...entonces avanza al siguiente paso.
            showStep(currentStep + 1);
        }
    });
});