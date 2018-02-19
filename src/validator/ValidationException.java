package validator;

/*
* ValidationException
* ERROR CODES LIST:
*   Account errors
*   --------------
*
*   100 -> Account username incorrect format
*   101 -> Account email incorrect format
*   102 -> Account calories incorrect format
*   103 -> Account password too weak
*   104 -> DB Error saving account
*   105 -> Account kcal goal cannot be null
*   106 -> Invalid username or password
*   107 -> username or email already exists
*   108 -> username not found.
*
*   Generic errors
*   ---------------
*   200->Missing id/incorrect id
*   250 -> Id not found
*
*   Ingredients errors
*   ------------------
*   300->Ingredient validation error
*
*   400 -> GENERIC SQL error
*   401 -> SQL DB connection error
*
*   501 -> Account not found in permissions
*   502 -> Error saving permission
*   500 -> Need permission for this action.
*   600 -> Error saving ingredient ownership
*   700 -> Error saving recipe
* */
public class ValidationException extends  Exception {
    private Integer code;
    public ValidationException(String message,Integer code){
        super(message);
        this.code = code;
    }
    public Integer getCode(){
        return this.code;
    }

}
