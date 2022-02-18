package utils;

import org.apache.poi.ss.usermodel.CellType;

public enum Roles {

    ADMIN(1),
    EMPLEADO(2);



    private final int code;


    Roles(int code) {
        this.code = code;
    }



    public int getCode() {
        return this.code;
    }
}
