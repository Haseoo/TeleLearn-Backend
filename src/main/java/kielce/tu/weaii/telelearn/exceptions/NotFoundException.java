package kielce.tu.weaii.telelearn.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String resourceName, Long id) {
        super(String.format("Resource %s with id %s not found.", resourceName, id));
    }
}
