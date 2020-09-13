package com.iwxyi.fairyland.Repositories;

import java.util.List;

import com.iwxyi.fairyland.Models.SyncBook;

import org.springframework.data.repository.CrudRepository;

public interface SyncBookRepository extends CrudRepository<SyncBook,Long> {
    List<SyncBook> findByUserIdOrderByModifyTimeDesc(String userId);
}
