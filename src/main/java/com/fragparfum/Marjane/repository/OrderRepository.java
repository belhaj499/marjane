package com.fragparfum.Marjane.repository;

import com.fragparfum.Marjane.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {}
