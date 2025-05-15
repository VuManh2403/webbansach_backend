package com.example.webbansach_backend.dao;

import com.example.webbansach_backend.entity.OrderDetail;
import com.example.webbansach_backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "reviews")
public interface ReviewRepository extends JpaRepository<Review, Long> {
    public Review findReviewByOrderDetail(OrderDetail orderDetail);
    public long countBy();
}
