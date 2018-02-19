package validator;

import domain.Account;


import static java.util.Objects.compare;

public class AccountValidator implements Validator<Account> {

    @Override
    public void validate(Account obj) throws ValidationException {
        if(!obj.getUsername().matches("[a-zA-Z0-9]+"))
            throw new ValidationException("Username must contain only letters and digits!",100);
        if(obj.getEmail().equals(""))
            throw new ValidationException("Email format invalid!",101);
        if(obj.getKcalGoal()!= null && (int)obj.getKcalGoal()<0)
            throw new ValidationException("Calories goal not valid!",102);
    }

    @Override
    public void validate_partial(Account obj) throws ValidationException {
        Integer id;
        String username;
        String email;
        Integer kcalGoal;
        if(obj.getId() == null)
            id = 0;
        else{
            id=obj.getId();
        }

        if(obj.getKcalGoal() == null)
            kcalGoal = 0;
        else{
            kcalGoal=obj.getKcalGoal();
        }
        if(obj.getEmail() == null)
            email ="a";
        else{
            email=obj.getEmail();
        }
        if(obj.getUsername() == null)
            username = "emptyUsername";
        else{
            username=obj.getUsername();
        }
        Account acc = new Account(id,username,email,kcalGoal);
        validate(acc);
    }
}
