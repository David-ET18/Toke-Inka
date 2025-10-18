package com.example.toke.controllers;
import com.example.toke.exception.ProductoNoEncontradoException;
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

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        ModelAndView mav = new ModelAndView("error/generico"); // Vista para un error genérico
        mav.addObject("mensajeError", "Ha ocurrido un error inesperado. Por favor, intente más tarde.");
        return mav;
    }
}