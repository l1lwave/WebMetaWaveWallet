package web.meta.wave.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.Status;
import web.meta.wave.model.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUserFrom(CustomUser userFrom);

    List<Transaction> findAllByUserTo(CustomUser userTo);

    List<Transaction> findAllByStatus(Status status);
}
