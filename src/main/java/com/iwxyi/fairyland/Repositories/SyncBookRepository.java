package com.iwxyi.fairyland.Repositories;

import java.util.List;

import com.iwxyi.fairyland.Models.SyncBook;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SyncBookRepository extends CrudRepository<SyncBook,Long> {
    List<SyncBook> findByUserIdOrderByModifyTimeDesc(String userId);
    
    @Query("select * from sync_book where user_id = :userId and deleted != 1 order by modify_time desc")
    List<SyncBook> getUserBooks(@Param("userId") String userId);
}
