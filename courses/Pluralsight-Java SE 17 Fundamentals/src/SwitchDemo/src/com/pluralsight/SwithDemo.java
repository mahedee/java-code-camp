package com.pluralsight;

public class SwithDemo {
    public  static  void main(String[] args){

        double value1 = 50;
        double value2 = 10;

        final double result;
        char opCode = 'a';

        switch (opCode){
            case 'a':
                result = value1 + value2;
                break;
            case 's':
                result = value1 - value2;
                break;
            case 'm':
                result = value1 * value2;
                break;
            case 'd':
                result = value2 !=0 ? value1 + value2 : 0.0d;
                break;
            default:
                System.out.println("Invalid opCode: " + opCode);
                result = 0.0d;
                break;
        }

        System.out.println(result);
    }
}
