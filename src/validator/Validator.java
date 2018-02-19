package validator;

public interface Validator<E> {
    void validate(E obj) throws ValidationException;
    void validate_partial(E obj) throws ValidationException;
}