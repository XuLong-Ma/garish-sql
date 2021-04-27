package org.dregs.garish.sql.utils;

import org.springframework.transaction.TransactionStatus;

public class TransactionFunction {

    private TransactionHub transactionHub;

    public TransactionFunction(TransactionHub transactionHub) {
        this.transactionHub = transactionHub;
    }

    public <A> boolean run(BoolFunction<A> boolFunction, A a){
        TransactionStatus transactionStatus = transactionHub.beginTransaction();
        try{
            boolean flag = boolFunction.apply(a);
            transactionHub.commitOrRollBack(transactionStatus,flag);
            return flag;
        }catch (Exception e){
            transactionHub.rollback(transactionStatus);
            Logs.error(e);
            return false;
        }
    }
    public <A,B> boolean run(BoolFunction2<A,B> boolFunction, A a, B b){
        TransactionStatus transactionStatus = transactionHub.beginTransaction();
        try{
            boolean flag = boolFunction.apply(a,b);
            transactionHub.commitOrRollBack(transactionStatus,flag);
            return flag;
        }catch (Exception e){
            transactionHub.rollback(transactionStatus);
            Logs.error(e);
            return false;
        }
    }
    public <A,B,C> boolean run(BoolFunction3<A,B,C> boolFunction, A a, B b, C c){
        TransactionStatus transactionStatus = transactionHub.beginTransaction();
        try{
            boolean flag = boolFunction.apply(a,b,c);
            transactionHub.commitOrRollBack(transactionStatus,flag);
            return flag;
        }catch (Exception e){
            transactionHub.rollback(transactionStatus);
            Logs.error(e);
            return false;
        }
    }

    public <A,B,C,D> boolean run(BoolFunction4<A,B,C,D> boolFunction, A a, B b, C c,D d){
        TransactionStatus transactionStatus = transactionHub.beginTransaction();
        try{
            boolean flag = boolFunction.apply(a,b,c,d);
            transactionHub.commitOrRollBack(transactionStatus,flag);
            return flag;
        }catch (Exception e){
            transactionHub.rollback(transactionStatus);
            Logs.error(e);
            return false;
        }
    }

    public <A,B,C,D,E> boolean run(BoolFunction5<A,B,C,D,E> boolFunction, A a, B b, C c, D d, E e){
        TransactionStatus transactionStatus = transactionHub.beginTransaction();
        try{
            boolean flag = boolFunction.apply(a,b,c,d,e);
            transactionHub.commitOrRollBack(transactionStatus,flag);
            return flag;
        }catch (Exception ex){
            transactionHub.rollback(transactionStatus);
            Logs.error(ex);
            return false;
        }
    }
    @FunctionalInterface
    public interface BoolFunction<A> {
        boolean apply(A a);
    }
    @FunctionalInterface
    public interface BoolFunction2<A,B> {
        boolean apply(A a, B b);
    }

    @FunctionalInterface
    public interface BoolFunction3<A,B,C> {
        boolean apply(A a, B b, C c);
    }


    @FunctionalInterface
    public interface BoolFunction4<A,B,C,D> {
        boolean apply(A a,B b,C c,D d);
    }

    @FunctionalInterface
    public interface BoolFunction5<A,B,C,D,E> {
        boolean apply(A a,B b,C c,D d,E e);
    }

}
