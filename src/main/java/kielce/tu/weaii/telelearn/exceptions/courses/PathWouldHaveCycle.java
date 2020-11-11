package kielce.tu.weaii.telelearn.exceptions.courses;

import kielce.tu.weaii.telelearn.exceptions.BusinessLogicException;

public class PathWouldHaveCycle extends BusinessLogicException {
    public PathWouldHaveCycle() {
        super("Po wykonaniu operacji ścieżka posiadałaby cykl, co jest zabronione.");
    }
}
