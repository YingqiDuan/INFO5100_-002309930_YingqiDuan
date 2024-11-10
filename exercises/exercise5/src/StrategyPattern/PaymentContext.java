package StrategyPattern;

public class PaymentContext {
    private PaymentStrategy paymentStrategy;

    public void setPaymentStrategy(PaymentStrategy strategy){
        this.paymentStrategy=strategy;
    }

    public void executePayment(double amount){
        if(paymentStrategy==null){
            throw new IllegalStateException("Payment Strategy not set.");
        }
        paymentStrategy.pay(amount);
    }
}
