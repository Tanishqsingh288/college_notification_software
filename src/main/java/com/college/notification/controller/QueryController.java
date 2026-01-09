package com.college.notification.controller;

import com.college.notification.dto.AddQueryRequest;
import com.college.notification.entity.Query;
import com.college.notification.service.QueryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/queries")
@CrossOrigin
public class QueryController {

    private final QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    // 1️⃣ Get ALL queries (resolved + unresolved)
    @GetMapping
    public List<Query> getAllQueries() {
        return queryService.getAllQueries();
    }

    // 2️⃣ Get only UNRESOLVED queries
    @GetMapping("/unresolved")
    public List<Query> getUnresolvedQueries() {
        return queryService.getUnresolvedQueries();
    }

    // 3️⃣ Get only RESOLVED queries
    @GetMapping("/resolved")
    public List<Query> getResolvedQueries() {
        return queryService.getResolvedQueries();
    }

    // 4️⃣ Add new query
    @PostMapping
    public Query addQuery(@RequestBody AddQueryRequest request) {
        return queryService.addQuery(request);
    }

    // 5️⃣ Delete query by ID
    @DeleteMapping("/{id}")
    public String deleteQuery(@PathVariable Long id) {
        queryService.deleteQueryById(id);
        return "Query deleted successfully";
    }

    // 6️⃣ Resolve query (unresolved → resolved)
    @PutMapping("/{id}/resolve")
    public Query resolveQuery(
            @PathVariable Long id,
            @RequestParam String resolvedBy
    ) {
        return queryService.resolveQuery(id, resolvedBy);
    }


    @GetMapping("/{id}")
    public Query getQuery(@PathVariable Long id) {
        return queryService.getQueryById(id);
    }
}
