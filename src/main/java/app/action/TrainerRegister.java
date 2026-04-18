package app.action;

import app.model.Trainer;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "Register Trainer",
        urlPatterns = { "/register_trainer" },
        initParams = {
                @WebInitParam(name = "pageName", value = "Register - Training Academy"),
                @WebInitParam(name = "pageHeader", value = "Training Registration - IT")
        })
public class TrainerRegister extends BaseAction<Trainer> {
}
