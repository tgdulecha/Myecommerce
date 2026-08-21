package org.course.ecommerce.dto;

import java.util.List;

public class PageDto<T> {

    private List<T> content;
    private int page;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public PageDto(List<T> content, int page, int pageSize, long totalElements) {
        this.content = content;
        this.page = page;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
    }

    public List<T> getContent() { return content; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
}
