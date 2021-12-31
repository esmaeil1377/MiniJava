package main.java.code.generator;

/**
 * Created by mohammad hosein on 6/28/2015.
 */
public class Address {
    public int num;
    public TypeAddress Type;
    public VarType varType;

    public Address(int num, VarType varType, TypeAddress Type) {
        this.num = num;
        this.Type = Type;
        this.varType = varType;
    }

    public Address(int num, VarType varType) {
        this.num = num;
        this.Type = TypeAddress.Direct;
        this.varType = varType;
    }
    public String toString(){
        if (Type == Direct) {
            return num+"";
        } else if (Type == Indirect) {
            return "@"+num;
        } else if (Type == Imidiate) {
            return "#"+num;
        }
        return return num+"";
    }
}
