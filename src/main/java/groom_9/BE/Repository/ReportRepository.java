package groom_9.BE.Repository;

import groom_9.BE.Domain.Report;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends MongoRepository<Report, ObjectId> {
    List<Report> findByUserId(ObjectId userId);
    List<Report> findByPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(LocalDateTime periodStart, LocalDateTime periodEnd);
    List<Report> findByAverageCompletionRateGreaterThanEqual(Double rate);
}