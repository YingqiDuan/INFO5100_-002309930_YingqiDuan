package StrategyPattern;

public class CashPayment implements PaymentStrategy{
    @Override
    public void pay(double amount){
        System.out.println(amount+" paid using Cash.");
    }
}
