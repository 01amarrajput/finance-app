package com.finance.config;

import com.finance.entity.*;
import com.finance.entity.FinancialRecord.RecordType;
import com.finance.entity.User.Role;
import com.finance.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository            userRepo;
    private final FinancialRecordRepository recordRepo;
    private final PasswordEncoder           encoder;

    public DataSeeder(UserRepository userRepo,
                      FinancialRecordRepository recordRepo,
                      PasswordEncoder encoder) {
        this.userRepo   = userRepo;
        this.recordRepo = recordRepo;
        this.encoder    = encoder;
    }

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) return;

        log.info("Seeding demo data...");

        User admin = userRepo.save(User.builder()
                .name("Admin User").email("admin@finance.com")
                .password(encoder.encode("admin123"))
                .role(Role.ADMIN).status(User.UserStatus.ACTIVE).build());

        userRepo.save(User.builder()
                .name("Alice Analyst").email("analyst@finance.com")
                .password(encoder.encode("analyst123"))
                .role(Role.ANALYST).status(User.UserStatus.ACTIVE).build());

        userRepo.save(User.builder()
                .name("Victor Viewer").email("viewer@finance.com")
                .password(encoder.encode("viewer123"))
                .role(Role.VIEWER).status(User.UserStatus.ACTIVE).build());

        List<FinancialRecord> records = List.of(
            rec(8500,  RecordType.INCOME,  "Salary",        LocalDate.now().minusDays(2),   "Monthly salary",    admin),
            rec(1200,  RecordType.INCOME,  "Freelance",     LocalDate.now().minusDays(5),   "Design project",    admin),
            rec(450,   RecordType.EXPENSE, "Rent",          LocalDate.now().minusDays(1),   "Monthly rent",      admin),
            rec(120,   RecordType.EXPENSE, "Utilities",     LocalDate.now().minusDays(3),   "Electricity",       admin),
            rec(200,   RecordType.EXPENSE, "Groceries",     LocalDate.now().minusDays(4),   null,                admin),
            rec(60,    RecordType.EXPENSE, "Transport",     LocalDate.now().minusDays(6),   "Uber rides",        admin),
            rec(9200,  RecordType.INCOME,  "Salary",        LocalDate.now().minusMonths(1).minusDays(2),  "Monthly salary",     admin),
            rec(350,   RecordType.INCOME,  "Dividends",     LocalDate.now().minusMonths(1).minusDays(10), "Quarterly dividend", admin),
            rec(480,   RecordType.EXPENSE, "Rent",          LocalDate.now().minusMonths(1).minusDays(1),  "Monthly rent",       admin),
            rec(95,    RecordType.EXPENSE, "Subscriptions", LocalDate.now().minusMonths(1).minusDays(7),  "Netflix, Spotify",   admin),
            rec(7800,  RecordType.INCOME,  "Salary",        LocalDate.now().minusMonths(2).minusDays(2),  "Monthly salary",     admin),
            rec(900,   RecordType.INCOME,  "Consulting",    LocalDate.now().minusMonths(2).minusDays(15), "Strategy session",   admin),
            rec(460,   RecordType.EXPENSE, "Rent",          LocalDate.now().minusMonths(2).minusDays(1),  "Monthly rent",       admin),
            rec(310,   RecordType.EXPENSE, "Healthcare",    LocalDate.now().minusMonths(2).minusDays(20), "Dental",             admin)
        );

        recordRepo.saveAll(records);
        log.info("Seeded 3 users and {} records.", records.size());
        log.info("Login: admin@finance.com / admin123");
    }

    private FinancialRecord rec(double amount, RecordType type,
                                String category, LocalDate date,
                                String notes, User creator) {
        return FinancialRecord.builder()
                .amount(BigDecimal.valueOf(amount))
                .type(type).category(category)
                .date(date).notes(notes)
                .createdBy(creator).build();
    }
}
