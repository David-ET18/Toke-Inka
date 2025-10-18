document.addEventListener("DOMContentLoaded", function() {
    console.log("main.js cargado y ejecutándose.");

    const tourButton = document.getElementById('tour-chatbox-button');
    if (!tourButton) return;

    const tourSteps = [
        {
            element: '#welcome-banner',
            title: '¡Bienvenido a Toke Inca!',
            content: 'Este es nuestro banner principal. Aquí encontrarás las novedades más importantes de nuestra tienda.'
        },
        {
            element: '#product-section-title',
            title: 'Nuestros Productos',
            content: 'Esta sección muestra nuestro increíble catálogo. ¡Sigue bajando para descubrirlo!'
        },
        {
            element: '#product-list-container .product-card:first-child',
            title: 'Tarjeta de Producto',
            content: 'Cada producto tiene su propia tarjeta. Haz clic en "Ver Detalles" para conocer más sobre él.'
        },
        {
            element: '.navbar-nav a[href="/carrito"]',
            title: 'Tu Carrito',
            content: 'Cuando agregues productos, podrás verlos y gestionar tu compra desde aquí. ¡Es hora de explorar por tu cuenta!'
        }
    ];

    let currentStep = -1;
    let currentPopover = null;

    function showStep(stepIndex) {
        if (currentPopover) {
            currentPopover.dispose();
            document.querySelectorAll('.tour-highlight').forEach(el => el.classList.remove('tour-highlight'));
        }
        
        if (stepIndex >= tourSteps.length) {
            currentStep = -1;
            return;
        }

        const step = tourSteps[stepIndex];
        const targetElement = document.querySelector(step.element);

        if (!targetElement) {
            console.warn(`Elemento del tour no encontrado: ${step.element}`);
            showStep(stepIndex + 1);
            return;
        }

        targetElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
        targetElement.classList.add('tour-highlight');

        const isLastStep = stepIndex === tourSteps.length - 1;
        const buttonText = isLastStep ? 'Finalizar Tour' : 'Siguiente';

        // --- CORRECCIÓN DEFINITIVA AQUÍ ---
        // Se reconstruye el HTML del contenido usando concatenación simple (+)
        // para máxima compatibilidad y evitar errores de sintaxis.
        const popoverContentHTML = step.content + 
                                   '<hr>' + 
                                   '<button class="btn btn-primary btn-sm w-100 tour-next-btn">' + 
                                   buttonText + 
                                   '</button>';

        currentPopover = new bootstrap.Popover(targetElement, {
            title: step.title,
            content: popoverContentHTML, // Usamos la cadena que acabamos de construir
            html: true,
            placement: 'bottom',
            trigger: 'manual'
        });
        
        targetElement.addEventListener('shown.bs.popover', () => {
            const popoverBody = document.querySelector('.popover-body');
            if (popoverBody) {
                const nextButton = popoverBody.querySelector('.tour-next-btn');
                if (nextButton) {
                    nextButton.addEventListener('click', () => {
                        showStep(currentStep + 1);
                    });
                }
            }
        }, { once: true });

        currentPopover.show();
        currentStep = stepIndex;
    }

    tourButton.addEventListener('click', () => {
        if (currentStep === -1) {
            showStep(0);
        }
    });
});