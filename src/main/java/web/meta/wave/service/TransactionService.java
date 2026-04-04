package web.meta.wave.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.MetaToken;
import web.meta.wave.model.Status;
import web.meta.wave.model.Transaction;
import web.meta.wave.repository.TransactionRepository;
import web.meta.wave.statements.TransactionStatements;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    private static final TransactionStatements transactionStatements = new TransactionStatements();

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void addTransaction(CustomUser userFrom,
                               CustomUser userTo,
                               MetaToken tokenFrom,
                               MetaToken tokenTo,
                               BigDecimal amountFrom,
                               BigDecimal amountTo,
                               Status status) {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(transactionStatements.getFormat());

        transactionRepository.save(new Transaction(formatter.format(now), userFrom, userTo, tokenFrom, tokenTo, amountFrom, amountTo, status));
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
