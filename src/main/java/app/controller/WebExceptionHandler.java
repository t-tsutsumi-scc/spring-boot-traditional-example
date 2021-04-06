package app.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class WebExceptionHandler extends ErrorHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handle(Exception ex, WebRequest request) {
        return handle(ex, request, (status, errorResponse) -> {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("error");
            mav.setStatus(status);
            mav.addObject("status", status.value());
            mav.addObject("error", status.getReasonPhrase());
            mav.addObject("message", "Error ID: " + errorResponse.id);
            return mav;
        });
    }

}
