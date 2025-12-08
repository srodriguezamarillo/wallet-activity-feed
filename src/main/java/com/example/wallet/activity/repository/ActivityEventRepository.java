package com.example.wallet.activity.repository;

import com.example.wallet.activity.domain.ActivityEvent;
import com.example.wallet.activity.domain.ActivityStatus;
import com.example.wallet.activity.domain.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

/**
 * JPA repository for activity events.
 */
public interface ActivityEventRepository extends JpaRepository<ActivityEvent, String>
{

	@Query("""
			select e from ActivityEvent e
			where e.userId = :userId
			  and (:product is null or e.product = :product)
			  and (:status is null or e.status = :status)
			  and (:currency is null or e.currency = :currency)
			  and (:from is null or e.occurredAt >= :from)
			  and (:to is null or e.occurredAt <= :to)
			  and (
			        :search is null
			     or lower(FUNCTION('JSON_UNQUOTE', FUNCTION('JSON_EXTRACT', e.metadata, '$.merchantName')))
			            like lower(concat('%', :search, '%'))
			     or lower(FUNCTION('JSON_UNQUOTE', FUNCTION('JSON_EXTRACT', e.metadata, '$.peerName')))
			            like lower(concat('%', :search, '%'))
			     or lower(FUNCTION('JSON_UNQUOTE', FUNCTION('JSON_EXTRACT', e.metadata, '$.source')))
			            like lower(concat('%', :search, '%'))
			  )
			order by e.occurredAt desc
			""")
	Page<ActivityEvent> searchFeed(@Param("userId") String userId, @Param("product") ProductType product,
			@Param("status") ActivityStatus status, @Param("currency") String currency, @Param("from") Instant from,
			@Param("to") Instant to, @Param("search") String search, Pageable pageable);

}
