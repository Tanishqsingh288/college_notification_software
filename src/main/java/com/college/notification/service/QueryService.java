package com.college.notification.service;

import com.college.notification.dto.AddQueryRequest;
import com.college.notification.entity.Query;
import com.college.notification.repository.QueryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QueryService {

    private final QueryRepository queryRepository;

    public QueryService(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    // 1️⃣ Get all queries
    public List<Query> getAllQueries() {
        return queryRepository.findAll();
    }

    // 2️⃣ Get unresolved queries
    public List<Query> getUnresolvedQueries() {
        return queryRepository.findByResolvedFalse();
    }

    // 3️⃣ Get resolved queries
    public List<Query> getResolvedQueries() {
        return queryRepository.findByResolvedTrue();
    }

    public Query addQuery(AddQueryRequest request) {
        Query query = new Query();
        query.setTitle(request.getTitle());
        query.setDescription(request.getDescription());
        query.setSentByEmail(request.getSentByEmail());
        query.setResolved(false); // default

        return queryRepository.save(query);
    }

    // 5️⃣ Delete query by ID
    public void deleteQueryById(Long id) {
        queryRepository.deleteById(id);
    }

    // 6️⃣ Resolve query (only if unresolved)
    public Query resolveQuery(Long id, String resolvedBy) {
        Query query = queryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Query not found"));

        if (!query.isResolved()) {
            query.setResolved(true);
            query.setResolvedBy(resolvedBy);
            query.setResolvedAt(LocalDateTime.now());
            return queryRepository.save(query);
        }
        return query;
    }

    // ---------- New method: Get single query by ID ----------
    public Query getQueryById(Long id) {
        return queryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Query not found with id: " + id));
    }

}
