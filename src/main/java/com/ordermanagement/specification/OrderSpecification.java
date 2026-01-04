package com.ordermanagement.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.ordermanagement.entity.Order;
import com.ordermanagement.entity.OrderStatus;
import com.ordermanagement.entity.User;

public class OrderSpecification {

    public static Specification<Order> hasUser(User user) {
        return (root, query, cb) -> user == null ? null : cb.equal(root.get("user"), user);
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Order> priceBetween(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null)
                return null;
            if (min == null)
                return cb.lessThanOrEqualTo(root.get("price"), max);
            if (max == null)
                return cb.greaterThanOrEqualTo(root.get("price"), min);
            return cb.between(root.get("price"), min, max);
        };
    }



    public static Specification<Order> createdBetween(
            LocalDate from, LocalDate to) {

        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from == null) return cb.lessThanOrEqualTo(root.get("createdAt"), to);
            if (to == null) return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
            return cb.between(root.get("createdAt"), from, to);
        };
    }

}
