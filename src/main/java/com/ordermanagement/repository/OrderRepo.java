package com.ordermanagement.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository; 

import com.ordermanagement.entity.Order;
import com.ordermanagement.entity.User;

@Repository
public interface OrderRepo extends JpaRepository<Order,Long>,JpaSpecificationExecutor<Order> {

    @Query("""
    SELECT o FROM Order o
    JOIN FETCH o.user
    WHERE o.user = :user
""")
Page<Order> findByUserWithUser(
    @Param("user") User user,
    Pageable pageable
);


     long count();

    @Query("SELECT COALESCE(SUM(o.price),0) FROM Order o")
    Double totalRevenue();

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countByStatus();
    

    @Query("""
        SELECT COALESCE(SUM(o.price),0)
        FROM Order o
        WHERE o.createdAt BETWEEN :from AND :to
    """)
    Double revenueBetween(LocalDateTime from, LocalDateTime to);


   @Query("""
    select date(o.createdAt), count(o)
    from Order o
    where o.createdAt >= :from
    group by date(o.createdAt)
    order by date(o.createdAt)
""")
List<Object[]> ordersPerDay(
    @Param("from") LocalDateTime from
);


@Query("""
    select date(o.createdAt), count(o)
    from Order o
    where o.createdAt between :from and :to
    group by date(o.createdAt)
    order by date(o.createdAt)
""")
List<Object[]> ordersPerDayRange(
    @Param("from") LocalDateTime from,
    @Param("to") LocalDateTime to
);



}
