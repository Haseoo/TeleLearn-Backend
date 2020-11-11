package kielce.tu.weaii.telelearn.exceptions.courses;

import kielce.tu.weaii.telelearn.exceptions.NotFoundException;

public class PathNotFound extends NotFoundException {
    public PathNotFound(Long id) {
        super("scieżka", id);
    }
}
