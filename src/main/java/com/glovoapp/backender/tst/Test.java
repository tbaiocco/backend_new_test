package com.glovoapp.backender.tst;

public class Test {

	public static void mainTst(String[] args) {
		// TODO Auto-generated method stub
		
		double val = 0.5;
		System.out.println("0,2:"+new Double(0.2/val).intValue());
		
		System.out.println("0,6:"+new Double(0.6/val).intValue());
		
		System.out.println("0,999:"+new Double(0.999/val).intValue());
		
		System.out.println("0,001:"+new Double(0.001/val).intValue());
		
		System.out.println("5,6:"+new Double(5.6/val).intValue());
		
		System.out.println("1:"+new Double(1/val).intValue());
		
		System.out.println("6:"+new Double(6/val).intValue());

	}

}
