package core.basesyntax;

import core.basesyntax.db.DataBase;
import core.basesyntax.db.DataBaseCsvImpl;
import core.basesyntax.model.Fruit;
import core.basesyntax.model.Operation;
import core.basesyntax.model.Transaction;
import core.basesyntax.service.DailyTransactionsStringToTransactions;
import core.basesyntax.service.FruitTransactionServiceImpl;
import core.basesyntax.service.OperationStrategy;
import core.basesyntax.service.OperationStrategyImpl;
import core.basesyntax.service.ReadDailyTransactions;
import core.basesyntax.service.WriteDailyReportToFile;
import core.basesyntax.service.strategy.BalanceOperationHandler;
import core.basesyntax.service.strategy.OperationHandler;
import core.basesyntax.service.strategy.PurchaseOperationHandler;
import core.basesyntax.service.strategy.ReturnOperationHandler;
import core.basesyntax.service.strategy.SupplyOperationHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static final String SEPARATE_SYMBOL_FOR_CSV =
            DailyTransactionsStringToTransactions.SEPARATE_SYMBOL_FOR_CSV;
    private static Map<Operation, OperationHandler> operationsMap = new HashMap<>();

    public static void main(String[] args) {
        createFileTransactionsCsv();
        createTransactionsMap();
        ReadDailyTransactions readDailyTransactions = new ReadDailyTransactions();
        List<String> transactionsStringList = new ArrayList<>(
                readDailyTransactions.getListOfTransactions());
        DailyTransactionsStringToTransactions dailyTransactionsStringToTransactions =
                new DailyTransactionsStringToTransactions();
        List<Transaction> transactionsList = new ArrayList<>(
                dailyTransactionsStringToTransactions.stringListToTransactionList(
                        transactionsStringList));
        OperationStrategy operationStrategy = new OperationStrategyImpl(operationsMap);
        FruitTransactionServiceImpl fruitTransactionServiceImpl =
                new FruitTransactionServiceImpl(operationStrategy);
        List<String> totalDailyTransactionsList =
                fruitTransactionServiceImpl.processDailyReport(transactionsList);
        List<String> dailyTransactionsReport =
                fruitTransactionServiceImpl.createReport(totalDailyTransactionsList);
        WriteDailyReportToFile writeDailyReportToFile = new WriteDailyReportToFile();
        writeDailyReportToFile.writeReportToFile(dailyTransactionsReport);
    }

    public static void createTransactionsMap() {
        operationsMap.put(Operation.SUPPLY, new SupplyOperationHandler());
        operationsMap.put(Operation.BALANCE, new BalanceOperationHandler());
        operationsMap.put(Operation.RETURN, new ReturnOperationHandler());
        operationsMap.put(Operation.PURCHASE, new PurchaseOperationHandler());
    }

    public static void createFileTransactionsCsv() {
        List<Transaction> transaction = new ArrayList<>();
        transaction.add(new Transaction(Operation.BALANCE, Fruit.BANANA, 20));
        transaction.add(new Transaction(Operation.BALANCE, Fruit.APPLE, 100));
        transaction.add(new Transaction(Operation.SUPPLY, Fruit.BANANA, 100));
        transaction.add(new Transaction(Operation.PURCHASE, Fruit.BANANA, 13));
        transaction.add(new Transaction(Operation.RETURN, Fruit.APPLE, 10));
        transaction.add(new Transaction(Operation.PURCHASE, Fruit.APPLE, 20));
        transaction.add(new Transaction(Operation.PURCHASE, Fruit.BANANA, 5));
        transaction.add(new Transaction(Operation.SUPPLY, Fruit.BANANA, 50));
        DataBase transactionsDataBase =
                new DataBaseCsvImpl(ReadDailyTransactions.TRANSACTION_FULL_PATH);
        transactionsDataBase.writeDataToFile(transactionsListToStringList(
                transaction, ReadDailyTransactions.HEAD_OF_TRANSACTION_TABLE));
    }

    public static List<String> transactionsListToStringList(
            List<Transaction> transactionsList, String headOfTable) {

        List<String> list = transactionsList
                .stream()
                .map(t -> t.getOperation().getOperation()
                        + SEPARATE_SYMBOL_FOR_CSV
                        + t.getFruit().getFruitName()
                        + SEPARATE_SYMBOL_FOR_CSV
                        + t.getAmount())
                .collect(Collectors.toList());
        list.add(0, headOfTable);
        return list;
    }
}
