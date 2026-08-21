package org.course.ecommerce.repository;

import org.course.ecommerce.entity.OrderDetails;
import org.course.ecommerce.entity.OrderDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, OrderDetailsId> {

    @Query("SELECT od FROM OrderDetails od JOIN FETCH od.product WHERE od.id.orderId = :orderId ORDER BY od.id.productId")
    List<OrderDetails> findByIdOrderId(@Param("orderId") Integer orderId);

    @Query("SELECT od FROM OrderDetails od JOIN FETCH od.product WHERE od.id = :id")
    Optional<OrderDetails> findByIdWithProduct(@Param("id") OrderDetailsId id);
}
