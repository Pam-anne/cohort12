package app.model;

import app.framework.Cohort12Form;
import app.framework.Cohort12FormField;
import app.framework.Cohort12Table;
import app.framework.Cohort12TableCol;

import java.io.Serializable;

@Cohort12Form(label = "Trainer Register",
        actionUrl = "./register_trainer")
@Cohort12Table(label = "Trainers",
        tableUrl = "./trainer_lists",
        registerUrl = "./register_trainer")
public class Trainer implements Serializable {

    @Cohort12FormField(label = "Trainer Name",
            placeholder = "Please enter Name")
    @Cohort12TableCol(label = "Trainer Name")
    private String name;

    @Cohort12FormField(label = "Trainer Gender",
            placeholder = "Please enter  Gender")
    @Cohort12TableCol(label = "Trainer ID")
    private String gender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
