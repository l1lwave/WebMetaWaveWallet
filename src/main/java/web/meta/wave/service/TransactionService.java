package web.meta.wave.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.MetaToken;
import web.meta.wave.model.Status;
import web.meta.wave.model.Transaction;
import web.meta.wave.repository.TransactionRepository;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public boolean addTransaction(CustomUser userFrom,
                                  CustomUser userTo,
                                  MetaToken tokenFrom,
                                  MetaToken tokenTo,
                                  BigDecimal amountFrom,
                                  BigDecimal amountTo,
                                  Status status) {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        transactionRepository.save(new Transaction(formatter.format(now), userFrom, userTo, tokenFrom, tokenTo, amountFrom, amountTo, status));
        return true;
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsbyUserFrom(CustomUser user) {
        return transactionRepository.findAllByUserFrom(user);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsbyUserTo(CustomUser user) {
        return transactionRepository.findAllByUserTo(user);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
