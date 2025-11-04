package com.example.toke.controllers;
import com.example.toke.exception.ProductoNoEncontradoException;
import com.example.toke.exception.StockInsuficienteException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ModelAndView handleProductoNoEncontrado(ProductoNoEncontradoException ex) {
        ModelAndView mav = new ModelAndView("error/404"); // Vista para el error 404
        mav.addObject("mensajeError", ex.getMessage());
        return mav;
    }

    // Puedes añadir más manejadores para otras excepciones personalizadas
    // @ExceptionHandler(StockInsuficienteException.class)
    // public ModelAndView handleStockInsuficiente...
     @ExceptionHandler(StockInsuficienteException.class)
    public ModelAndView handleStockInsuficiente(StockInsuficienteException ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/stock-insuficiente");
        
        // Pasamos el mensaje de error a la vista
        mav.addObject("mensajeError", ex.getMessage());
        
        // Obtenemos la URL de la página anterior para el botón "Volver"
        String referer = request.getHeader("Referer");
        // Si no hay referer, volvemos a la página principal por seguridad
        mav.addObject("refererUrl", (referer != null && !referer.isEmpty()) ? referer : "/");
        
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        ModelAndView mav = new ModelAndView("error/generico"); // Vista para un error genérico
        mav.addObject("mensajeError", "Ha ocurrido un error inesperado. Por favor, intente más tarde.");
        return mav;
    }
}